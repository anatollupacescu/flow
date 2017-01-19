package org.flow.core;

import java.util.Set;

public class UnmatchedFieldsException extends RuntimeException {
    public UnmatchedFieldsException(Set<SedaType> sedaTypes) {
        super(String.format("Could not match this fields: '%s'", sedaTypes));
    }
}
