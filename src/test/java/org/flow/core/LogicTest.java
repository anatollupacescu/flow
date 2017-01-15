package org.flow.core;

import org.junit.Test;

import static org.flow.core.LogicTest.LogicTestType.SESSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LogicTest {

    enum LogicTestType implements SedaType {
        SESSION
    }

    @Test
    public void canCreateNode() {
        String sendUserListToUser = "sendUserListToUser";
        Logic l = Logic.createNew(sendUserListToUser).inFields(SESSION).build();
        assertNotNull(l);
        assertEquals(sendUserListToUser, l.name);
    }
}