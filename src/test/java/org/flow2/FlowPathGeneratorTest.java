package org.flow2;

import org.junit.Test;
import seda.SedaType;

import static org.junit.Assert.*;
import static org.flow2.FlowPathGeneratorTest.FlowTestType.*;

public class FlowPathGeneratorTest {

    enum FlowTestType implements SedaType {
        FIELD1, FIELD2, FIELD3
    }

    @Test
    public void canDo() {
        assertTrue(true);
    }

    @Test
    public void canCreateFlowWithTransition() {
        String updateName = "updateName";
        Logic logic1 = Logic.createNew("logic1").inFields(FIELD1).outFields(FIELD3).build();
        Data data = Data.createNew("userList", FIELD1, FIELD2).binding(updateName, FIELD1).build();
        Logic logic2 = Logic.createNew("logic2").inFields(FIELD3).build();
        Flow conditional = Flow.newFlow("subFlow", FIELD3).process(logic2).build();
        Flow flow = Flow.newFlow("userProvidedName", FIELD1)
                .update(data, updateName)
                .process(logic1)
                .processIf("Everything is fine", conditional)
                .build();
        assertEquals("Everything is fine", flow.getCondition(conditional));
        FlowFormatter formatter = FlowFormatter.withSeparator("->");
        FlowPathGenerator generator = new FlowPathGenerator(formatter);
        generator.generatePaths(flow).forEach(System.out::println);
    }
}