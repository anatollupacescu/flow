package org.flow;

import org.flow.core.SedaType;

public class App {

    enum AppType implements SedaType {
        USER_LIST
    }

    enum Game implements SedaType {
        DATA, CELL_ID
    }

    enum User implements SedaType {
        SESSION, NAME, STATUS
    }

    public static void main(String[] args) {
    }
}
