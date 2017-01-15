package org.flow2;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.ListIterator;

public class FlowPathGenerator {

    private final FlowFormatter formatter;

    public FlowPathGenerator(FlowFormatter formatter) {
        this.formatter = formatter;
    }

    public List<String> generatePaths(Flow start) {
        List<String> accumulator = Lists.newArrayList(start.name);
        parseChildren(start, accumulator);
        return accumulator;
    }

    private void parseChildren(Flow parent, List<String> acc) {
        for (Node child : parent.children) {
            if (childHasCondition(parent, child)) {
                updateAccConditional(parent, child, acc);
            } else {
                updateAcc(child, acc);
            }
        }
    }

    private void updateAcc(Node node, List<String> acc) {
        List<String> childElements = getChildElements(node);
        enrichAccRecordsWithChildren(null, childElements, acc);
    }

    private void updateAccConditional(Flow parent, Node node, List<String> acc) {
        Logic logicNode = (Logic) node;
        List<String> stringsToEnrich = Lists.newArrayList(acc);
        String condition = getCondition(parent, logicNode);
        List<String> childElements = getChildElements(logicNode);
        enrichAccRecordsWithChildren(condition, childElements, acc);
        acc.addAll(stringsToEnrich);
    }

    private List<String> getChildElements(Node flow) {
        String nodeDescription = formatter.formatNode(flow);
        List<String> childElements = Lists.newArrayList(nodeDescription);
        if (hasChildren(flow)) {
            parseChildren((Flow)flow, childElements);
        }
        return childElements;
    }

    private void enrichAccRecordsWithChildren(String condition, List<String> children, List<String> acc) {
        ListIterator<String> it = acc.listIterator();
        while (it.hasNext()) {
            String parentFlow = it.next();
            children.stream()
                .map(childFlow -> formatter.formatRow(condition, parentFlow, childFlow))
                .forEach(it::set);
        }
    }

    private boolean hasChildren(Node subflow) {
        if (subflow.canHaveChildren())
            return ((Flow) subflow).children.isEmpty();
        return false;
    }

    private boolean childHasCondition(Flow parentFlow, Node subflow) {
        if (subflow.canHaveCondition()) {
            String logic = parentFlow.getCondition((Logic) subflow);
            return !Strings.isNullOrEmpty(logic);
        }
        return false;
    }

    private String getCondition(Flow parentFlow, Logic subflow) {
        return parentFlow.getCondition(subflow);
    }
}
