package seda;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class FlowPathGenerator  implements PathGenerator<String, Flow> {

    private final String separator;
    private final String initial;

    public FlowPathGenerator(String separator, String initial) {
        this.separator = separator;
        this.initial = initial;
    }

    @Override
    public List<String> generatePaths(Flow start) {
        List<String> res = Lists.newArrayList(initial);
        parseElements(start, start.consumers, res);
        return res;
    }

    public List<String> parseElements(Flow mainFlow, List<Flow> elements, List<String> acc) {
        for (Flow subflow : elements) {
            if (hasCondition(mainFlow, subflow)) {
                List<String> stringsToEnrich = Lists.newArrayList(acc);
                updateAcc(subflow, stringsToEnrich);
                acc.addAll(stringsToEnrich);
            } else updateAcc(subflow, acc);
        }
        return acc;
    }

    private void updateAcc(Flow flow, List<String> acc) {
        List<String> childElements = Lists.newArrayList(flow.name);
        if (hasChilds(flow)) {
            List<Flow> childs = getChilds(flow);
            parseElements(flow, childs, childElements);
        }
        crossJoin(childElements, acc);
    }

    private void crossJoin(List<String> childs, List<String> acc) {
        ListIterator<String> it = acc.listIterator();
        while(it.hasNext()) {
            String childFlow = it.next();
            it.remove();
            childs.stream().forEach(child -> it.add(childFlow + separator + child));
        }
    }

    private boolean hasChilds(Flow subflow) {
        return !subflow.consumers.isEmpty();
    }

    private List<Flow> getChilds(Flow flow) {
        return flow.consumers;
    }

    private boolean hasCondition(Flow parentFlow, Flow subflow) {
        return parentFlow.hasCondition(subflow);
    }
}
