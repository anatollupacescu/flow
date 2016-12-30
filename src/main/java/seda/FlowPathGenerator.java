package seda;

import java.util.*;

public class FlowPathGenerator  implements PathGenerator<String, Flow> {

    private Flow flow;

    @Override
    public List<String> generatePaths(Flow start) {
        this.flow = start;
        List<String> res = new ArrayList<>();
        parseList(0, flow.name, res, flow.consumers, flow);
        return res;
    }

    private String getCondition(Flow main, Flow subflow) {
        if (hasCondition(main, subflow)) {
            return main.getCondition(subflow);
        }
        return null;
    }

    private void parseList(int index, String head, List<String> acc, List<Flow> alist, Flow parentFlow) {
        String path = head;
        for (int i = index; i < alist.size(); i++) {
            Flow c = alist.get(i);
            String condition = null;
            if (hasCondition(parentFlow, c)) {
                parseList(i + 1, path, acc, alist, c);
                condition = getCondition(parentFlow, c);
            }
            if(hasConsumers(c)) {
                parseList(0, path, acc, c.consumers, c);
            } else path += row(condition, c.name);
        }
        acc.add(path);
    }

    private String row(String head, String condition, String val) {
        if (condition != null) {
            return head + conditional(condition, val);
        }
        return head + simple(val);
    }

    private String row(String condition, String e) {
        return condition != null
                ? conditional(condition, e)
                : simple(e);
    }

    private String conditional(String condition, String name) {
        return String.format("->(%s)%s", condition, name);
    }

    private String simple(String name) {
        return String.format("->%s", name);
    }

    private boolean hasConsumers(Flow subflow) {
        return !subflow.consumers.isEmpty();
    }

    private boolean hasCondition(Flow parentFlow, Flow subflow) {
        return parentFlow.hasCondition(subflow);
    }
}
