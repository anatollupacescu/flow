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
        List<String> res = Lists.newArrayList(start.name);
        parseElements(start, start.consumers, res);
        return res;
    }

    private void parseElements(Flow mainFlow, List<Node> elements, List<String> acc) {
        for (Node subflow : elements) {
            if (hasCondition(mainFlow, subflow)) {
                Logic logic = (Logic)subflow;
                List<String> stringsToEnrich = Lists.newArrayList(acc);
                String condition = getCondition(mainFlow, logic);
                //updateAccWithNegation(acc, condition); only if we want to see the negation
                updateAcc(condition, logic, stringsToEnrich);
                acc.addAll(stringsToEnrich);
            } else updateAcc(null, subflow, acc);
        }
    }
/*
    private void updateAccWithNegation(List<String> acc, String condition) {
        ListIterator<String> it = acc.listIterator();
        while(it.hasNext()) {
            String line = it.next();
            it.set(String.format("%s%s%s(%s)", line , formatter.separator, "not" , condition));
        }
    }
*/
    private void updateAcc(String condition, Node flow, List<String> acc) {
        String flowDescription = flow.name;
        if(flow instanceof DataView) flowDescription += String.format("[%s]", ((DataView)flow).method);
        List<String> childElements = Lists.newArrayList(flowDescription);
        if (hasChildren(flow)) {
            List<Node> children = getChildren((Flow)flow);
            parseElements((Flow)flow, children, childElements);
        }
        crossJoin(condition, childElements, acc);
    }

    private void crossJoin(String condition, List<String> children, List<String> acc) {
        ListIterator<String> it = acc.listIterator();
        while (it.hasNext()) {
            String childFlow = it.next();
            it.remove();
            children.stream()
                    .map(child -> formatter.getRow(condition, childFlow, child))
                    .forEach(it::add);
        }
    }

    private boolean hasChildren(Node subflow) {
        return (subflow instanceof Flow) && !((Flow)subflow).consumers.isEmpty();
    }

    private List<Node> getChildren(Flow flow) {
        return flow.consumers;
    }

    private boolean hasCondition(Flow parentFlow, Node subflow) {
        if(subflow instanceof Data || subflow instanceof DataView) return false;
        String logic = parentFlow.getCondition((Logic)subflow);
        return !Strings.isNullOrEmpty(logic);
    }

    private String getCondition(Flow parentFlow, Logic subflow) {
        return parentFlow.getCondition(subflow);
    }
}
