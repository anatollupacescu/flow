package org.flow2;

import org.junit.Test;
import seda.SedaType;

import static org.flow2.LogicTest.LogicTestType.SESSION;
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