package seda;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.ListIterator;

class FlowPathGenerator implements PathGenerator<String, Flow> {

    private final FlowFormatter formatter;

    FlowPathGenerator(FlowFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public List<String> generatePaths(Flow start) {
        List<String> res = Lists.newArrayList(formatter.getInitial());
        parseElements(start, start.consumers, res);
        return res;
    }

    private void parseElements(Flow mainFlow, List<Flow> elements, List<String> acc) {
        for (Flow subflow : elements) {
            if (hasCondition(mainFlow, subflow)) {
                List<String> stringsToEnrich = Lists.newArrayList(acc);
                String condition = getCondition(mainFlow, subflow);
                updateAcc(condition, subflow, stringsToEnrich);
                acc.addAll(stringsToEnrich);
            } else updateAcc(null, subflow, acc);
        }
    }

    private void updateAcc(String condition, Flow flow, List<String> acc) {
        List<String> childElements = Lists.newArrayList(flow.name);
        if (hasChildren(flow)) {
            List<Flow> children = getChildren(flow);
            parseElements(flow, children, childElements);
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

    private boolean hasChildren(Flow subflow) {
        return !subflow.consumers.isEmpty();
    }

    private List<Flow> getChildren(Flow flow) {
        return flow.consumers;
    }

    private boolean hasCondition(Flow parentFlow, Flow subflow) {
        return parentFlow.hasCondition(subflow);
    }

    private String getCondition(Flow parentFlow, Flow subflow) {
        return parentFlow.getCondition(subflow);
    }
}
