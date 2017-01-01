package org.flow;

import seda.Flow;
import seda.FlowFormatter;
import seda.FlowPathGenerator;
import seda.SedaType;

import static org.flow.App.AppType.*;

public class App {

    enum AppType implements SedaType {
        SESSION, USER_LIST, USER_NAME, STATUS, GAME_DATA
    }

    public static void main(String[] args) {

        //logic and state
        Flow userList = Flow.newWithName("userList")
                .outField(USER_LIST)
                .build();

        Flow userListSessionWriter = Flow.newWithName("sessionWriter")
                .inFields(SESSION, USER_LIST)
                .build();

        Flow broadcastNewUsername = Flow.newWithName("usernameChangeBroadcaster")
                .inFields(USER_LIST)
                .build();

        Flow usernameBinderFlow = Flow.newWithName("usernameBinder")
                .inFields(USER_NAME, SESSION)
                .consumer(userList)
                .outField(USER_NAME, SESSION, USER_LIST)
                .build();

        Flow bindStatusFlow = Flow.newWithName("updateStatus")
                .inFields(SESSION, STATUS)
                .consumer(userList)
                .outField(SESSION,STATUS, USER_LIST)
                .build();

        Flow checkIfAllUsersAreReady = Flow.newWithName("checkIfAllUsersAreReady")
                .inFields(USER_LIST)
                .build();

        Flow broadcastUserStatusChange = Flow.newWithName("broadcastUserStatusChange")
                .inFields(USER_LIST)
                .build();

        Flow gameData = Flow.newWithName("gameDataHolder")
                .inFields(USER_LIST)
                .outField(GAME_DATA)
                .build();

        Flow broadcastGameData = Flow.newWithName("gameDataBroadcaster")
                .inFields(USER_LIST, GAME_DATA)
                .build();

        //client input
        Flow clientConnect = Flow.newWithName("clientConnect")
                .inFields(SESSION)
                .consumer(userList)
                .consumer(userListSessionWriter)
                .outField(USER_LIST)
                .build();

        Flow userRegistered = Flow.newWithName("userProvidedName")
                .inFields(USER_NAME, SESSION)
                .consumer(usernameBinderFlow)
                .consumer(broadcastNewUsername)
                .outField(USER_LIST)
                .build();

        Flow userChangedStatus = Flow.newWithName("userChangedStatus")
                .inFields(SESSION, STATUS)
                .consumer(bindStatusFlow)
                .consumer(checkIfAllUsersAreReady)
                .ifTrue("everyoneIsReady", Flow.newWithName("startGame")
                        .inFields(USER_LIST)
                        .consumer(gameData)
                        .consumer(broadcastGameData)
                        .build())
                .consumer(broadcastUserStatusChange)
                .build();

        FlowFormatter formatter = new FlowFormatter("->");
        FlowPathGenerator generator = new FlowPathGenerator(formatter);

//        generator.generatePaths(clientConnect).forEach(System.out::println);
//        generator.generatePaths(userRegistered).forEach(System.out::println);
        generator.generatePaths(userChangedStatus).forEach(System.out::println);
    }
}
