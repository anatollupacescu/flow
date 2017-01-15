package org.flow.core;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.flow.core.DataTest.DataTestType.*;
import static org.junit.Assert.*;

public class DataTest {

    enum DataTestType implements SedaType {
        USER_LIST, USER_NAME, AGE
    }

    @Test
    public void canNotCreateDataWithoutFields() {
        try {
            Data.createNew("userList").build();
        } catch (RuntimeException e) {
            String errorMsg = "Can not create node without fields";
            assertEquals(errorMsg, e.getMessage());
            return;
        }
        fail("Exception not thrown");
    }

    @Test
    public void testCanCreateData() {
        Data userList = Data.createNew("userList", USER_LIST).build();
        assertNotNull(userList);
        assertTrue(userList.bindings.isEmpty());
    }

    @Test
    public void canCreateDataWithFields() {
        Data userList = Data.createNew("userList", USER_NAME, AGE)
                .binding("registerUser", USER_NAME, AGE)
                .build();
        assertEquals(ImmutableSet.of(USER_NAME, AGE), userList.bindings.get("registerUser"));
    }

    @Test
    public void canCreateDataWithFieldsAndMultipleBindings() {
        Data userList = Data.createNew("userList", USER_LIST, USER_NAME, AGE)
                .binding("registerUser", USER_NAME, AGE)
                .binding("updateUser", USER_LIST)
                .build();
        assertEquals(2, userList.bindings.asMap().size());
    }

    @Test
    public void canNotCreateBindingIfFieldsDoNotMatch() {
        final String registerUser = "registerUser";
        try {
            Data.createNew("userList", USER_LIST)
                    .binding(registerUser, AGE)
                    .build();
        } catch (IllegalStateException e) {
            String errorMsg = String.format("Could not register binding '%s' - missing fields - %s", registerUser, ImmutableSet.of(AGE).toString());
            assertEquals(errorMsg, e.getMessage());
            return;
        }
        fail("Exception not thrown");
    }
}
