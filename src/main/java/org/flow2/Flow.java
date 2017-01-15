package org.flow2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import seda.SedaType;

import java.util.*;

public class Flow extends Logic {

    final List<Node> children;
    private final Map<Logic, String> conditionMap;

    Flow(String name, Set<SedaType> inputFields, Set<SedaType> outFields, List<Node> children, Map<Logic, String> conditionMap) {
        super(name, inputFields, outFields);
        this.children = children;
        this.conditionMap = conditionMap;
    }

    public static FlowBuilder newFlow(String name, SedaType... fields) {
        if(fields.length == 0) {
            throw new IllegalStateException("Please specify at least one input field");
        }
        return new FlowBuilder(name, Arrays.asList(fields));
    }

    public String getCondition(Logic conditional) {
        return conditionMap.get(conditional);
    }

    @Override
    boolean canHaveChildren() {
        return true;
    }

    public static class FlowBuilder extends LogicBuilder {

        private final Set<SedaType> unusedFields;
        private final Set<SedaType> workingSet;
        private final ImmutableList.Builder<Node> children = ImmutableList.builder();
        private final ImmutableMap.Builder<Logic, String> conditionMap = ImmutableMap.builder();

        public FlowBuilder(String name, List<SedaType> inputs) {
            super(name);
            this.inFields.addAll(inputs);
            this.unusedFields = Sets.newHashSet(inputs);
            this.workingSet = Sets.newHashSet(inputs);
        }

        public Flow build() {
            if(!unusedFields.isEmpty()) {
                throw new IllegalStateException(couldNotBindFieldsMessage.apply(unusedFields.toString()));
            }
            return new Flow(name, ImmutableSet.copyOf(inFields), ImmutableSet.copyOf(outFields), children.build(), conditionMap.build());
        }

        public FlowBuilder process(Logic logic) {
            bindLogic(logic, workingSet);
            unusedFields.removeAll(logic.fields);
            workingSet.addAll(logic.outFields);
            children.add(logic);
            return this;
        }

        public FlowBuilder processIf(String condition, Logic flow) {
            conditionMap.put(flow, condition);
            return process(flow);
        }

        public FlowBuilder read(Data data) {
            workingSet.addAll(data.fields);
            children.add(data);
            return this;
        }

        public FlowBuilder update(Data data, String method) {
            bindData(data, method, workingSet);
            DataView view = new DataView(data, method);
            children.add(view);
            return this;
        }

        private void bindData(Data data, String method, Set<SedaType> fieldsToUpdate) {
            if(!data.bindings.containsKey(method)) {
                throw new IllegalStateException(String.format("Method not found '%s'", method));
            }
            Set<SedaType> methodFields = ImmutableSet.copyOf(data.bindings.get(method));
            Sets.SetView<SedaType> difference = Sets.difference(methodFields, fieldsToUpdate);
            if(!difference.isEmpty()) {
                throw new IllegalStateException(couldNotBindFieldsMessage.apply(difference.toString()));
            }
        }

        private void bindLogic(Logic logic, Set<SedaType> inputFields) {
            Preconditions.checkNotNull(logic);
            final Set<SedaType> unusedFields = Sets.difference(logic.fields, inputFields);
            if(!unusedFields.isEmpty()) {
                throw new IllegalStateException(couldNotBindFieldsMessage.apply(unusedFields.toString()));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Flow flow = (Flow) o;
        return Objects.equals(children, flow.children) &&
                Objects.equals(conditionMap, flow.conditionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), children, conditionMap);
    }
}
