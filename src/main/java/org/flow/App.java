package org.flow;

import org.flow.core.*;

import static org.flow.App.AppType.USER_LIST;
import static org.flow.App.AppType.USER_LIST;
import static org.flow.App.AppType.USER_LIST;
import static org.flow.App.Game.CELL_ID;
import static org.flow.App.Game.DATA;
import static org.flow.App.User.*;

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
        final FlowFormatter formatter = FlowFormatter.withSeparator("->");
        String updateName = "updateName";
        String updateStatus = "updateStatus";
        Data userList = Data.createNew("userList", SESSION, NAME, STATUS)
                .binding(updateName, SESSION, NAME)
                .binding(updateStatus, SESSION, STATUS)
                .build();

        Logic sendUserListToClient = Logic.createNew("sendUserListToClient")
                .inFields(USER_LIST, SESSION)
                .build();

        Flow clientConnected = Flow.newFlow("clientConnected", SESSION)
                .read(userList)
                .process(sendUserListToClient)
                .build();

        Flow broadcastUserList = Flow.newFlow("broadcastUserList")
                .read(userList)
                .build();

        Flow clientProvidedName = Flow.newFlow("clientProvidedName", SESSION, NAME)
                .update(userList, updateName)
                .process(broadcastUserList)
                .build();

        Flow updatePlayerStatus = Flow.newFlow("updatePlayerStatus", SESSION, STATUS)
                .update(userList, updateStatus)
                .build();

        Data game = Data.createNew("game", USER_LIST, DATA, CELL_ID)
                .binding("startGame", USER_LIST)
                .binding("stopGame")
                .build();

        Flow startGame = Flow.newFlow("startGame", USER_LIST)
                .update(game, "startGame")
                .build();

        Flow clientUpdatedStatus = Flow.newFlow("clientUpdatedStatus", SESSION, STATUS)
                .read(userList)
                .process(updatePlayerStatus)
                .processIf("Is the last player ready", startGame)
                .process(broadcastUserList)
                .build();

        FlowPathGenerator generator = new FlowPathGenerator(formatter);
        generator.generatePaths(clientConnected).forEach(System.out::println);
        generator.generatePaths(clientProvidedName).forEach(System.out::println);
        generator.generatePaths(clientUpdatedStatus).forEach(System.out::println);
    }
}
