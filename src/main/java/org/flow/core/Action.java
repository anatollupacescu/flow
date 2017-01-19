package org.flow.core;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public class Action {

    final String name;
    final FieldSet input;
    final FieldSet output;

    public Action(String name, FieldSet input, FieldSet output) {
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
        private final Set<SedaType> workingSet = Sets.newHashSet();
        private final FieldSet output;

        public ActionBuilder(String name, FieldSet input, FieldSet output) {
            this.name = name;
            this.input = input;
            this.output = output;
            workingSet.addAll(input);
        }

        public ActionBuilder execute(Action child) {
            workingSet.removeAll(child.input);
            workingSet.addAll(child.output);
            return this;
        }

        public ActionBuilder executeIf(String reason, Action child) {
            return this;
        }

        public Action build() {
            workingSet.removeAll(output);
            if (!workingSet.isEmpty()) {
                throw new UnusedFieldsException(workingSet);
            }
            return new Action(name, input, output);
        }

        public ActionBuilder read(Data data, SedaType... fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if (dataHasAllFields(data, fieldList)) {
                workingSet.addAll(fieldList);
            }
            return this;
        }

        public ActionBuilder update(Data data, SedaType... fields) {
            markFields(data, fields);
            return this;
        }

        private void markFields(Data data, SedaType[] fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if (dataHasAllFields(data, fieldList)) {
                workingSet.removeAll(fieldList);
            }
        }

        private boolean dataHasAllFields(Data data, Set<SedaType> fieldList) {
            if (!data.containsAll(fieldList)) {
                throw new UnmatchedFieldsException(data.missingFields(fieldList));
            }
            return true;
        }

        public ActionBuilder use(SedaType... fields) {
            Set<SedaType> fieldList = Sets.newHashSet(fields);
            if (!workingSet.containsAll(fieldList)) {
                throw new UnmatchedFieldsException(difference(fieldList, workingSet));
            }
            workingSet.removeAll(Arrays.asList(fields));
            return this;
        }

        private Set<SedaType> difference(Set<SedaType> fieldList, Set<SedaType> workingSet) {
            return Sets.difference(fieldList, workingSet);
        }
    }
}
