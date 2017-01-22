package org.flow.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import static org.flow.core.ActionTest.ActionType.*;
import static org.flow.core.ActionTest.GameEvent.*;
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
        assertEquals(ImmutableSet.of(ONE, THREE), ImmutableSet.copyOf(input));
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
                .use("bind status to user", ONE)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canExecute() {
        Action childAction = Action.createNew("childAction", FieldSet.of(ONE), FieldSet.empty())
                .use("bind status to user", ONE)
                .build();

        FieldSet input = FieldSet.of(ONE, TWO);
        FieldSet output = FieldSet.of(ONE);
        Action action1 = Action.createNew("t1", input, output)
                .execute(childAction)
                .use("bind status to user", TWO)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canRead() {
        Data data = Data.createNew("userList", TWO);

        Action action1 = Action.createNew("t1", FieldSet.of(ONE), FieldSet.empty())
                .read(data, TWO)
                .use("bind status to user", ONE, TWO)
                .build();
        assertNotNull(action1);
    }

    @Test
    public void canNotReadMissingFields() {
        Data data = Data.createNew("userList", TWO);
        try {
            Action.createNew("t1", FieldSet.of(ONE), FieldSet.empty())
                    .read(data, TWO, THREE)
                    .use("bind status to user", ONE, TWO, THREE)
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

        Action action1 = Action.createNew("t1", FieldSet.of(ONE, TWO), FieldSet.empty())
                .update(data, TWO)
                .use("bind status to user", ONE)
                .build();
        assertNotNull(action1);
    }

    enum GameEvent implements SedaType {
        USER_ID, USER_LIST, STATUS, GAME_DATA
    }

    @Test
    public void flowTest() {
        LoggingFlow flow = new LoggingFlow("start", Lists.newArrayList(), Maps.newHashMap());

        Data userList = Data.createNew("userList", USER_LIST, STATUS);

        Action updateUserStatus = Action.createNew("updateUserStatus", FieldSet.of(STATUS, USER_ID), FieldSet.empty())
                .withFlow(flow)
                .use("bind status to user", USER_ID, STATUS)
                .build();

        Action broadcastUserList = Action.createNew("broadcastUserList")
                .withFlow(flow)
                .read(userList, USER_LIST)
                .use("broadcast updated user list", USER_LIST)
                .build();

        Data gameData = Data.createNew("gameData", USER_ID, GAME_DATA);

        Action startGame = Action.createNew("startGame")
                .read(userList, USER_LIST)
                .use("count users", USER_LIST)
                .create(gameData, GAME_DATA)
                .build();

        Action action1 = Action.createNew("userIsReady", FieldSet.of(USER_ID, STATUS))
                .withFlow(flow)
                .execute(updateUserStatus)
                .execute(broadcastUserList)
                .executeIf("Is the last user ready", startGame)
                .build();

        assertNotNull(action1);
        FlowPathGenerator generator = new FlowPathGenerator(FlowFormatter.withSeparator("->"));
        generator.generatePaths(flow).forEach(System.out::println);
    }
}
