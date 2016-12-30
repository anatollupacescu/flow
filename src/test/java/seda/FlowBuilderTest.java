package seda;

import seda.message.SedaType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class FlowBuilderTest {

    enum Test implements SedaType {
       UNU, DOI
    }

    @org.junit.Test(expected = IllegalStateException.class)
    public void souldHaveDifferentNames() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).buildConsumer();
        Flow c2 = Flow.newWithName("c1").inFields(Test.UNU).buildConsumer();
        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1).consumer(c2);
    }


    @org.junit.Test(expected = FlowBuilderValidator.UnboundFieldException.class)
    public void conditionalFlowShouldOnlyShareFieldsWithSameCondition() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).buildConsumer();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).outField(Test.DOI).buildConsumer();
        Flow c3 = Flow.newWithName("c3").inFields(Test.DOI).buildConsumer();

        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .conditionalLink("key", c2)
                .consumer(c3)
                .build();
    }

    @org.junit.Test
    public void conditionalFlowShouldOnlyShareFieldsWithSameConditionOK() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).buildConsumer();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).outField(Test.DOI).buildConsumer();
        Flow c3 = Flow.newWithName("c3").inFields(Test.DOI).buildConsumer();

        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .conditionalLink("key", c2)
                .conditionalLink("key", c3)
                .build();
    }
}