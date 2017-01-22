package org.flow.core;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public class Action {

    final String name;
    final FieldSet input;
    final FieldSet output;

    private Action(String name, FieldSet input, FieldSet output) {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public static ActionBuilder createNew(String name) {
        return new ActionBuilder(name, FieldSet.empty(), FieldSet.empty());
    }

    public static ActionBuilder createNew(String name, FieldSet of) {
        return new ActionBuilder(name, of, FieldSet.empty());
    }

    public static ActionBuilder createNew(String name, FieldSet input, FieldSet output) {
        return new ActionBuilder(name, input, output);
    }

    public static class ActionBuilder {

        private final String name;
        private final FieldSet input;
        private final Set<SedaType> allFields = Sets.newHashSet();
        private final Set<SedaType> usedFields = Sets.newHashSet();
        private final FieldSet output;
        private Flow flow = new Flow() {
            public void addChild(Action child) { }
            public void addChildWithCondition(Action child, String reason) { }
            public void addChild(Set<SedaType> fieldList) { }
            public void addChild(String operation, Data data, Set<SedaType> fieldList) { }
            public void addChild(String operation, Set<SedaType> fieldList) { }
        };

        private ActionBuilder(String name, FieldSet input, FieldSet output) {
            this.name = name;
            this.input = input;
            this.output = output;
            allFields.addAll(input);
        }

        public ActionBuilder withFlow(LoggingFlow flow) {
            this.flow = flow;
            return this;
        }

        public ActionBuilder execute(Action child) {
            usedFields.addAll(child.input);
            allFields.addAll(child.output);
            flow.addChild(child);
            return this;
        }

        public ActionBuilder executeIf(String reason, Action child) {
            usedFields.addAll(child.input);
            allFields.addAll(child.output);
            flow.addChildWithCondition(child, reason);
            return this;
        }

        public Action build() {
            usedFields.addAll(output);
            Set<SedaType> unusedFields = Sets.symmetricDifference(allFields, usedFields);
            if (!unusedFields.isEmpty()) {
                throw new UnusedFieldsException(unusedFields);
            }
            return new Action(name, input, output);
        }

        public ActionBuilder read(Data data, SedaType... fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if(fieldList.isEmpty()) {
                fieldList = data.fields;
            }
            if (dataHasAllFields(data, fieldList)) {
                allFields.addAll(fieldList);
            }
            flow.addChild("read", data, fieldList);
            return this;
        }

        public ActionBuilder update(Data data, SedaType... fields) {
            modify("update", data, fields);
            return this;
        }

        public ActionBuilder create(Data data, SedaType... fields) {
            modify("create", data, fields);
            allFields.addAll(Arrays.asList(fields));
            return this;
        }

        private void modify(String modType, Data data, SedaType[] fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if (dataHasAllFields(data, fieldList)) {
                usedFields.addAll(fieldList);
            }
            flow.addChild(modType, data, fieldList);
        }

        public ActionBuilder use(String operation, SedaType... fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if (!allFields.containsAll(fieldList)) {
                throw new UnmatchedFieldsException(difference(fieldList, allFields));
            }
            usedFields.addAll(fieldList);
            flow.addChild(operation, fieldList);
            return this;
        }

        private boolean dataHasAllFields(Data data, Set<SedaType> fieldList) {
            if (!data.containsAll(fieldList)) {
                throw new UnmatchedFieldsException(data.missingFields(fieldList));
            }
            return true;
        }

        private Set<SedaType> difference(Set<SedaType> fieldList, Set<SedaType> workingSet) {
            return Sets.difference(fieldList, workingSet);
        }
    }
}
