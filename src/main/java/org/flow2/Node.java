package org.flow2;

import seda.SedaType;

import java.util.Set;
import java.util.function.Function;

abstract class Node {

    static final Function<String, String> couldNotBindFieldsMessage = (st) -> String.format("Could not bind field(s): '%s'", st);

    final String name;
    final Set<SedaType> fields;

    Node(String name, Set<SedaType> inFields) {
        this.name = name;
        this.fields = inFields;
    }

    abstract boolean canHaveChildren();

    abstract boolean canHaveCondition();
}
