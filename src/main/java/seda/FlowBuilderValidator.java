package seda;

import com.google.common.collect.Sets;

import java.util.*;

class FlowBuilderValidator {

    private final Set<SedaType> inFields;
    private final Set<SedaType> outFields;
    private final Set<SedaType> internalFields;
    private final Set<SedaType> unusedInputFields;
    private final List<Flow> consumers;

    FlowBuilderValidator(Set<SedaType> inFields, Set<SedaType> outFields, Set<SedaType> internalFields, Set<SedaType> unusedInputFields, List<Flow> consumers) {
        this.inFields = inFields;
        this.outFields = outFields;
        this.internalFields = internalFields;
        this.unusedInputFields = unusedInputFields;
        this.consumers = consumers;
    }

    private Set<SedaType> getMissingFlowsFields(Flow flow) {
        final Set<SedaType> missingFields = Sets.newHashSetWithExpectedSize(flow.inputFields.size());
        flow.inputFields.forEach(field -> {
            if (!internalFields.contains(field)) {
                missingFields.add(field);
            }
        });
        return missingFields;
    }

    void checkForUnboundFieldsForCondition(Collection<Flow> flowsUnderThisKey, Flow flow) {
        final Set<SedaType> availableFields = new HashSet<>(internalFields);
        flowsUnderThisKey.forEach(fl -> availableFields.addAll(fl.outputFields));
        final Set<SedaType> missingFields = new HashSet<>();
        flow.inputFields.forEach(field -> {
            if (!availableFields.contains(field)) {
                missingFields.add(field);
            }
        });
        if (!missingFields.isEmpty()) {
            throw new UnboundFieldException(flow.name, missingFields);
        }
    }

    void checkForConflictingNames(Flow consumer) {
        consumers.stream().filter(c -> c.name.equalsIgnoreCase(consumer.name))
                .findFirst()
                .ifPresent(c -> {
                    throw new IllegalStateException("Two consumers have the same name: " + c.name);
                });
    }

    void markInputFieldAsUsed(Flow consumer) {
        unusedInputFields.removeAll(consumer.inputFields);
    }

    void checkForUnboundFields(Flow consumer) {
        Optional.of(getMissingFlowsFields(consumer)).ifPresent(unboundFields -> {
            if (!unboundFields.isEmpty()) {
                throw new UnboundFieldException(consumer.name, unboundFields);
            }
        });
    }

    void checkIfFlowHasUnusedFields() {
        if (!unusedInputFields.isEmpty()) {
            throw new UnusedFieldException(unusedInputFields);
        }
    }

    void checkIfFlowHasValidFields() {
        if (inFields.isEmpty() && outFields.isEmpty()) {
            throw new IllegalArgumentException("Please specify either input or output fields");
        }
    }

    public static class UnboundFieldException extends RuntimeException {
        public UnboundFieldException(String name, Set<SedaType> unboundFields) {
            super(String.format("%s - could not bind following fields: %s", name, unboundFields.toString()));
        }
    }

    public static class UnusedFieldException extends RuntimeException {
        public UnusedFieldException(Set<SedaType> fields) {
            super(String.format("Fields declared and not used: %s", fields.toString()));
        }
    }
}
