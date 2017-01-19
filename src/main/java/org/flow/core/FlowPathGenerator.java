package org.flow.core;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Collections;
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

    private void parseChildren(Flow parent, List<String> accumulator) {
        for (Flow child : parent.children) {
            List<String> oldAccumulator = Collections.emptyList();
            String condition = getCondition(parent, child);
            if (!Strings.isNullOrEmpty(condition)) {
                oldAccumulator = Lists.newArrayList(accumulator);
            }
            List<String> childElements = getChildElements(child);
            enrichAccRecordsWithChildren(condition, childElements, accumulator);
            accumulator.addAll(oldAccumulator);
        }
    }

    private List<String> getChildElements(Flow flow) {
        List<String> childElements = Lists.newArrayList(flow.name);
        if (hasChildren(flow)) {
            parseChildren(flow, childElements);
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

    private boolean hasChildren(Flow subflow) {
        return subflow.children.isEmpty();
    }

    private String getCondition(Flow parentFlow, Flow subflow) {
        return parentFlow.getCondition(subflow);
    }
}
