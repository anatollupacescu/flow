package org.flow.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LoggingFlow implements Flow {

    final String name;
    final List<LoggingFlow> children;
    private Map<LoggingFlow, String> conditionMap;

    LoggingFlow(String name, List<LoggingFlow> children, Map<LoggingFlow, String> conditionMap) {
        this.name = name;
        this.children = children;
        this.conditionMap = conditionMap;
    }

    private LoggingFlow(String name) {
        this.name = name;
        this.conditionMap = ImmutableMap.of();
        this.children = ImmutableList.of();
    }

    String getCondition(LoggingFlow conditional) {
        return conditionMap.get(conditional);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LoggingFlow flow = (LoggingFlow) o;
        return Objects.equals(children, flow.children) &&
                Objects.equals(conditionMap, flow.conditionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), children, conditionMap);
    }

    public void addChild(Action child) {
        LoggingFlow flow = new LoggingFlow(child.name);
        children.add(flow);
    }

    public void addChildWithCondition(Action child, String reason) {
        LoggingFlow flow = new LoggingFlow(child.name);
        children.add(flow);
        conditionMap.put(flow, reason);
    }

    public void addChild(Set<SedaType> fieldList) {
        LoggingFlow flow = new LoggingFlow(String.format("compute(%s)", fieldList));
        children.add(flow);
    }

    public void addChild(String operation, Data data, Set<SedaType> fieldList) {
        LoggingFlow flow = new LoggingFlow(String.format("%s(%s)%s", operation, fieldList, data.name));
        children.add(flow);
    }

    public void addChild(String operation, Set<SedaType> fieldList) {
        LoggingFlow flow = new LoggingFlow(String.format("%s(%s)", operation, fieldList));
        children.add(flow);
    }
}
