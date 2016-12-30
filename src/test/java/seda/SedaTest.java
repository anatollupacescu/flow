package seda;

import org.junit.Test;
import seda.example.Account;
import seda.example.User;

public class SedaTest {

    @Test(expected = FlowBuilderValidator.UnusedFieldException.class)
    public void unusedFieldsThrowsException() {
        Flow downStreamMessageFlow = Flow.newWithName("consumer")
                .inFields(User.NAME).buildConsumer();

        Flow.newWithName("main")
                .inFields(User.NAME)
                .inFields(User.AGE)
                .consumer(downStreamMessageFlow).build();
    }

    @Test(expected = FlowBuilderValidator.UnboundFieldException.class)
    public void testMessageFlow() {
        Flow downStreamMessageFlow = Flow.newWithName("consumer")
                .inFields(User.NAME)
                .inFields(User.INCOME).buildConsumer();

        Flow.newWithName("main")
                .inFields(User.NAME)
                .inFields(User.AGE)
                .consumer(downStreamMessageFlow).build();
    }

    @Test
    public void testMessageFlowOK() {
        Flow incomeFetcher = Flow.newWithName("incomeFetcher")
                .inFields(User.NAME)
                .outField(User.INCOME).buildConsumer();

        Flow incomeFlow = Flow.newWithName("incomeFlow")
                .inFields(User.LASTNAME)
                .inFields(User.INCOME).buildConsumer();

        Flow.newWithName("main")
                .inFields(User.NAME)
                .inFields(User.LASTNAME)
                .consumer(incomeFetcher)
                .consumer(incomeFlow)
                .build();
    }

    @Test
    public void test3MessageFlowsWorkOK() {
        Flow downStreamMessageFlow = Flow.newWithName("down1")
                .inFields(User.NAME)
                .outField(Account.ID).buildConsumer();
        Flow downStreamMessageFlow2 = Flow.newWithName("down2")
                .inFields(User.LASTNAME).buildConsumer();
        Flow downStreamMessageFlow3 = Flow.newWithName("down3")
                .inFields(Account.ID).buildConsumer();

        Flow.newWithName("main")
                .inFields(User.NAME)
                .inFields(User.LASTNAME)
                .consumer(downStreamMessageFlow)
                .consumer(downStreamMessageFlow2)
                .consumer(downStreamMessageFlow3).build();
    }
}