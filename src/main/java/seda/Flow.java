package seda;

import java.util.*;

public class Flow {

    final String name;
    final List<Flow> consumers;
    final Set<SedaType> inputFields;
    final Set<SedaType> outputFields;

    private final Map<Flow, String> conditionMap;

    Flow(String name, Set<SedaType> input, Set<SedaType> output, List<Flow> consumers, Map<Flow, String> conditionsMap) {
        this.name = name;
        this.consumers = consumers;
        this.conditionMap = conditionsMap;
        this.inputFields = Collections.unmodifiableSet(input);
        this.outputFields = Collections.unmodifiableSet(output);
    }

    public static FlowBuilder newWithName(String name) {
        return new FlowBuilder(name);
    }

    public String getCondition(Flow consumer) {
        return conditionMap.get(consumer);
    }

    public boolean hasCondition(Flow consumer) {
        return conditionMap.containsKey(consumer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flow flow = (Flow) o;
        return Objects.equals(name, flow.name) &&
                Objects.equals(consumers, flow.consumers) &&
                Objects.equals(inputFields, flow.inputFields) &&
                Objects.equals(outputFields, flow.outputFields) &&
                Objects.equals(conditionMap, flow.conditionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, consumers, inputFields, outputFields, conditionMap);
    }

    @Override
    public String toString() {
        return name + "{" + getCondition(this) + "}";
    }

}
