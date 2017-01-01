package seda;

public class FlowBuilderTest {

    enum Test implements SedaType {
        UNU, DOI
    }

    @org.junit.Test(expected = IllegalStateException.class)
    public void shouldHaveDifferentNames() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1).consumer(c2);
    }


    @org.junit.Test(expected = FlowBuilderValidator.UnboundFieldException.class)
    public void conditionalFlowShouldOnlyShareFieldsWithSameCondition() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).outField(Test.DOI).build();
        Flow c3 = Flow.newWithName("c3").inFields(Test.DOI).build();

        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key", c2)
                .consumer(c3)
                .build();
    }

    @org.junit.Test
    public void conditionalFlowShouldOnlyShareFieldsWithSameConditionOK() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).outField(Test.DOI).build();
        Flow c3 = Flow.newWithName("c3").inFields(Test.DOI).build();

        Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key", c2)
                .ifTrue("key", c3)
                .build();
    }
}