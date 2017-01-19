package org.flow.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.flow.core.ActionTest.ActionType.*;
import static org.junit.Assert.*;

public class ActionTest {

    enum ActionType implements SedaType {
        ONE, TWO, THREE
    }

    @Test
    public void fieldSetTest() {
        FieldSet input = FieldSet.empty();
        assertTrue(input.isEmpty());
        input = FieldSet.of(ONE, THREE);
        assertEquals(Sets.newHashSet(ONE, THREE), ImmutableSet.copyOf(input));
        try {
            input.add(TWO);
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
            return;
        }
        fail("Exception expected");
    }

    @Test
    public void canUseFields() {
        FieldSet input = FieldSet.of(ONE, TWO);
        FieldSet output = FieldSet.of(TWO);
        Action action1 = Action.createNew("t1", input, output)
                .use(ONE)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canExecute() {
        Action childAction = Action.createNew("childAction", FieldSet.of(ONE), FieldSet.empty())
                .use(ONE)
                .build();

        FieldSet input = FieldSet.of(ONE, TWO);
        FieldSet output = FieldSet.of(ONE);
        Action action1 = Action.createNew("t1", input, output)
                .execute(childAction)
                .use(TWO)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canRead() {
        Data data = Data.createNew("userList", TWO);

        Action action1 = Action.createNew("t1", FieldSet.of(ONE), FieldSet.empty())
                .read(data, TWO)
                .use(ONE, TWO)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canNotReadMissingFields() {
        Data data = Data.createNew("userList", TWO);
        try {
            Action.createNew("t1", FieldSet.of(ONE), FieldSet.empty())
                    .read(data, TWO, THREE)
                    .use(ONE, TWO, THREE)
                    .build();
        } catch (Exception e) {
            assertTrue(e instanceof UnmatchedFieldsException);
            return;
        }
        fail("Exception expected");
    }

    @Test
    public void canUpdate() {
        Data data = Data.createNew("userList", TWO);

        Action action1 = Action.createNew("t1", FieldSet.of(ONE), FieldSet.empty())
                .update(data, TWO)
                .use(ONE)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void test1() {
        Action action2 = Action.createNew("childAction", FieldSet.of(ONE), FieldSet.empty())
                .use(ONE)
                .build();

        Data data = Data.createNew("userList", ONE, THREE);

        FieldSet input = FieldSet.of(ONE, TWO);
        FieldSet output = FieldSet.of(ONE);
        Action action1 = Action.createNew("t1", input, output)
                .execute(action2)
                .read(data, THREE)
                .update(data, ONE)
                .use(TWO, THREE)
                .build();
        assertNotNull(action1);
    }
}
