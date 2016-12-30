package seda.message;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class SedaObject extends HashSet<SedaType> implements SedaType {

    public SedaObject(SedaType... types) {
        super(buildSet(types));
    }

    private static Set<SedaType> buildSet(SedaType[] types) {
        ImmutableSet.Builder<SedaType> builder = ImmutableSet.builder();
        for(SedaType type : types) {
            builder.add(type);
        }
        return builder.build();
    }
}
