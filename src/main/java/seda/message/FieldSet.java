package seda.message;

import seda.example.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FieldSet extends HashSet<SedaType> {

    private FieldSet(Set<SedaType> input) {
        super(input);
    }

    public static FieldSet immutable(Set<SedaType>  fields) {
        return new FieldSet(Collections.unmodifiableSet(fields));
    }

    public static void main(String[] args) {
        FieldSet set = FieldSet.immutable(new HashSet<>());
        set.add(User.AGE);
    }
}
