package org.flow2;

import com.google.common.collect.*;
import seda.SedaType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Data extends Node {

    final Multimap<String, SedaType> bindings;

    public Data(String name, Set<SedaType> fields, Multimap<String, SedaType> bindings) {
        super(name, fields);
        this.bindings = bindings;
    }

    public static DataBuilder createNew(String name, SedaType ...fields) {
        if(fields.length == 0) {
            throw new IllegalStateException("Can not create node without fields");
        }
        return new DataBuilder(name, fields);
    }

    @Override
    boolean canHaveChildren() {
        return false;
    }

    @Override
    boolean canHaveCondition() {
        return false;
    }

    public static class DataBuilder {
        private final String name;
        private final Multimap<String, SedaType> bindings = HashMultimap.create();
        private final Set<SedaType> fields;

        public DataBuilder(String name, SedaType ... argFields) {
            this.name = name;
            fields = ImmutableSet.copyOf(Lists.newArrayList(argFields));
        }

        public Data build() {
            return new Data(name, fields, bindings);
        }

        public DataBuilder binding(String method, SedaType ... inFields) {
            List<SedaType> fieldList = Arrays.asList(inFields);
            if(fields.containsAll(fieldList)) {
                this.bindings.putAll(method, fieldList);
            } else {
                Sets.SetView<SedaType> missingFields = Sets.difference(ImmutableSet.copyOf(fieldList), fields);
                throw new IllegalStateException(String.format("Could not register binding '%s' - missing fields - %s", method, missingFields));
            }
            return this;
        }
    }
}
