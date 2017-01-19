package org.flow.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Flow {

    final List<Flow> children;
    final Map<Flow, String> conditionMap;
    final String name;

    Flow(String name, List<Flow> children, Map<Flow, String> conditionMap) {
        this.name = name;
        this.children = children;
        this.conditionMap = conditionMap;
    }

    String getCondition(Flow conditional) {
        return conditionMap.get(conditional);
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
