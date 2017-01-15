package org.flow2;

import org.junit.Test;
import seda.SedaType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.flow2.FlowTest.FlowTestType.*;

public class FlowTest {

    enum FlowTestType implements SedaType {
        FIELD1, FIELD2, FIELD3
    }

    @Test
    public void canNotCreateFlowWithoutInputs() {
        try {
            Flow.newFlow("flow").build();
        } catch (RuntimeException e) {
            assertEquals("Please specify at least one input field", e.getMessage());
            return;
        }
        fail("Exception not thrown");
    }

    @Test
    public void canNotCreateFlowWithUnmatchedInputs() {
        try {
            Flow.newFlow("flow", FIELD1).build();
        } catch (RuntimeException e) {
            String field1 = "[FIELD1]";
            String errorMsg = Optional.of(field1).map(Node.couldNotBindFieldsMessage).get();
            assertEquals(errorMsg, e.getMessage());
             return;
        }
        fail("Expected exception not thrown");
    }

    @Test
    public void canCreateFlowWithInputs() {
        Logic logic = Logic.createNew("logic").inFields(FIELD1).build();
        Flow flow = Flow.newFlow("flow", FIELD1)
                .process(logic)
                .build();
        assertNotNull(flow);
    }

    @Test
    public void canNotCreateFlowWithInconsistentInputs() {
        Logic logic1 = Logic.createNew("logic").inFields(FIELD1).build();
        Logic logic2 = Logic.createNew("logic").inFields(FIELD2).build();
        try {
            Flow.newFlow("flow", FIELD1)
                    .process(logic1)
                    .process(logic2)
                    .build();
        } catch (RuntimeException e) {
            String errorMsg = Optional.of(Collections.singleton(FIELD2).toString()).map(Node.couldNotBindFieldsMessage).get();
            assertEquals(errorMsg, e.getMessage());
            return;
        }
        fail("Expected exception not thrown");
    }

    @Test
    public void canCreateFlowWithMultipleLogicNodes() {
        Logic logic1 = Logic.createNew("logic1")
                .inFields(FIELD1)
                .outFields(FIELD2)
                .build();
        Logic logic2 = Logic.createNew("logic2").inFields(FIELD2).build();
        Flow.newFlow("flow", FIELD1)
                .process(logic1)
                .process(logic2)
                .build();
    }

    @Test
    public void canCreateFlowWithReadDataNodes() {
        Logic logic1 = Logic.createNew("logic1").inFields(FIELD1, FIELD2).build();
        Data data = Data.createNew("data", FIELD2).build();
        Flow.newFlow("flow", FIELD1)
                .read(data)
                .process(logic1)
                .build();
    }

    @Test
    public void canNotCreateFlowWithMissingInputsInLogicNodes() {
        Logic logic1 = Logic.createNew("logic1").inFields(FIELD1, FIELD3).build();
        Data data = Data.createNew("data", FIELD2).build();
        try {
            Flow.newFlow("flow", FIELD1)
                    .read(data)
                    .process(logic1)
                    .build();
        }catch (RuntimeException e) {
            String errorMsg = Optional.of(Collections.singleton(FIELD3).toString()).map(Node.couldNotBindFieldsMessage).get();
            assertEquals(errorMsg, e.getMessage());
            return;
        }
        fail("Expected exception not thrown");
    }

    @Test
    public void canCreateFlowWithUpdateDataNodes() {
        Logic logic1 = Logic.createNew("logic1").inFields(FIELD1, FIELD2).build();
        Data data = Data.createNew("data", FIELD1, FIELD2)
                .binding("addField", FIELD1)
                .build();
        Flow.newFlow("flow", FIELD1)
                .update(data, "addField")
                .read(data)
                .process(logic1)
                .build();
    }

    @Test
    public void canCreateFlowWithOtherFlowAsProcessors() {
        Logic logic2 = Logic.createNew("logic2").inFields(FIELD3).build();
        Flow flow = Flow.newFlow("flow1", FIELD3).process(logic2).build();
        Flow.newFlow("flow", FIELD3)
                .process(flow)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void canNotCreateFlowWithLinkToNull() {
        Flow.newFlow("flow", FIELD3)
                .process(null)
                .build();
    }

    @Test
    public void canCreateFlowWithTransition() {
        Logic logic1 = Logic.createNew("logic1").inFields(FIELD1).outFields(FIELD3).build();
        Data data = Data.createNew("data", FIELD1, FIELD2).binding("addField", FIELD1).build();
        Logic logic2 = Logic.createNew("logic2").inFields(FIELD3).build();
        Flow conditional = Flow.newFlow("flow1", FIELD3).process(logic2).build();
        Flow flow = Flow.newFlow("flow", FIELD1)
                .update(data, "addField")
                .process(logic1)
                .processIf("Everything is fine", conditional)
                .build();
        assertEquals("Everything is fine", flow.getCondition(conditional));
    }
}
