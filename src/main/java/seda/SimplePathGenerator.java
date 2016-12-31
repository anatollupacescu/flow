package seda;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class SimplePathGenerator implements PathGenerator<String, String> {

    private final String separator;
    private final String initial;
    private final Multimap<String, String> graph;

    private final Set<String> conditions;
    private final Set<String> parents;

    public SimplePathGenerator(String separator, String initial, Multimap<String, String> graph, Set<String> conditions, Set<String> parents) {
        this.separator = separator;
        this.initial = initial;
        this.graph = graph;
        this.conditions = conditions;
        this.parents = parents;
    }

    @Override
    public List<String> generatePaths(String start) {
        return parseElements(new ArrayList<>(graph.get(start)), Lists.newArrayList(initial));
    }

    private List<String> parseElements(List<String> elements, List<String> acc) {
        for (String e : elements) {
            if (hasCondition(e)) {
                List<String> stringsToEnrich = Lists.newArrayList(acc);
                updateAcc(e, stringsToEnrich);
                acc.addAll(stringsToEnrich);
            } else updateAcc(e, acc);
        }
        return acc;
    }

    private void updateAcc(String e, List<String> acc) {
        List<String> childElements = Lists.newArrayList(e);
        if (hasChildren(e)) {
            List<String> children = getChildren(e);
            parseElements(children, childElements);
        }
        crossJoin(childElements, acc);
    }

    private void crossJoin(List<String> children, List<String> acc) {
        ListIterator<String> it = acc.listIterator();
        while (it.hasNext()) {
            String val = it.next();
            it.remove();
            children.forEach(child -> it.add(val + separator + child));
        }
    }

    private List<String> getChildren(String e) {
        return new ArrayList<>(graph.get(e));
    }

    private boolean hasChildren(String c) {
        return parents.contains(c);
    }

    private boolean hasCondition(String e) {
        return conditions.contains(e);
    }
}
