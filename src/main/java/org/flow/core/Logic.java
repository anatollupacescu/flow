package org.flow.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class Logic extends Node {

    final Set<SedaType> outFields;

    Logic(String name, Set<SedaType> inFields, Set<SedaType> outFields) {
        super(name, inFields);
        this.outFields = outFields;
    }

    public static LogicBuilder createNew(String name) {
        return new LogicBuilder(name);
    }

    @Override
    boolean canHaveChildren() {
        return false;
    }

    @Override
    boolean canHaveCondition() {
        return true;
    }

    public static class LogicBuilder {

        final String name;
        final Set<SedaType> inFields = Sets.newHashSet();
        final Set<SedaType> outFields = Sets.newHashSet();

        public LogicBuilder(String name) {
            this.name = name;
        }

        public LogicBuilder inFields(SedaType... fields) {
            inFields.addAll(Arrays.asList(fields));
            return this;
        }

        public LogicBuilder outFields(SedaType... fields) {
            outFields.addAll(Arrays.asList(fields));
            return this;
        }

        public Logic build() {
            return new Logic(name, ImmutableSet.copyOf(inFields), ImmutableSet.copyOf(outFields));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Logic logic = (Logic) o;
        return Objects.equals(outFields, logic.outFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outFields);
    }
}
