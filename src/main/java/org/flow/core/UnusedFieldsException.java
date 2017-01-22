package org.flow.core;

import java.util.Set;

public class UnusedFieldsException extends RuntimeException {

    public UnusedFieldsException(Set<SedaType> workingSet) {
        super(String.format("Could not bind fields: '%s'", workingSet));
    }
}
