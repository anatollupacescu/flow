package org.flow.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

public class Data {

    public final String name;
    public final Set<SedaType> fields;

    private Data(String name, Set<SedaType> fields) {
        this.name = name;
        this.fields = ImmutableSet.copyOf(fields);
    }

    public static Data createNew(String name, SedaType ...fields) {
        return new Data(name, Sets.newHashSet(fields));
    }

    boolean containsAll(Set<SedaType> fieldList) {
        return fields.containsAll(fieldList);
    }

    Set<SedaType> missingFields(Set<SedaType> fieldList) {
        return Sets.difference(fieldList, fields);
    }
}
