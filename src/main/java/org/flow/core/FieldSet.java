package org.flow.core;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class FieldSet extends ForwardingSet<SedaType> {

    private final Set<SedaType> fields;

    private FieldSet(SedaType... inputFields) {
        fields = ImmutableSet.copyOf(inputFields);
    }

    public static FieldSet of(SedaType... fields) {
        return new FieldSet(fields);
    }

    public static FieldSet empty() {
        return new FieldSet();
    }

    @Override
    protected Set<SedaType> delegate() {
        return fields;
    }
}
