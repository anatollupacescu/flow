package seda;

import com.google.common.collect.*;

import java.util.*;

public class FlowBuilder {

    private final String name;
    private final Set<SedaType> inFields = Sets.newHashSet();
    private final Set<SedaType> outFields = Sets.newHashSet();
    private final Set<SedaType> internalFields = Sets.newHashSet();
    private final Set<SedaType> unusedInputFields = Sets.newHashSet();
    private final List<Flow> consumers = Lists.newArrayList();
    private final Multimap<String, Flow> localConditionsMap = HashMultimap.create();
    private final Map<Flow, String> conditionsMap = Maps.newHashMap();
    private final FlowBuilderValidator validator;

    FlowBuilder(String name) {
        this.name = name;
        validator = new FlowBuilderValidator(inFields, outFields, internalFields, unusedInputFields, consumers);
    }

    public FlowBuilder consumer(Flow flow) {
        validator.checkForConflictingNames(flow);
        validator.checkForUnboundFields(flow);
        validator.markInputFieldAsUsed(flow);
        consumers.add(flow);
        internalFields.addAll(flow.outputFields);
        return this;
    }

    public FlowBuilder conditionalLink(String key, Flow flow) {
        validator.checkForConflictingNames(flow);
        Collection<Flow> flowsWithKey = localConditionsMap.get(key);
        validator.checkForUnboundFieldsForCondition(flowsWithKey, flow);
        validator.markInputFieldAsUsed(flow);
        flowsWithKey.add(flow);
        conditionsMap.put(flow, key);
        consumers.add(flow);
        return this;
    }

    public FlowBuilder inFields(SedaType... fields) {
        Arrays.stream(fields).forEach(field -> {
            this.inFields.add(field);
            this.internalFields.add(field);
            this.unusedInputFields.add(field);
        });
        return this;
    }

    public FlowBuilder outField(SedaType... fields) {
        Arrays.stream(fields).forEach(outFields::add);
        return this;
    }

    public Flow build() {
        validator.checkIfFlowHasValidFields();
        if (!consumers.isEmpty()) {
            validator.checkIfFlowHasUnusedFields();
            return new Flow(name, inFields, outFields, consumers, conditionsMap);
        }
        return new Flow(name, inFields, outFields, Collections.emptyList(), conditionsMap);
    }
}
