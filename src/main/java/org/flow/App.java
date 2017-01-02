package org.flow;

import seda.Flow;
import seda.FlowFormatter;
import seda.FlowPathGenerator;
import seda.SedaType;

import static org.flow.App.AppType.*;

public class App {

    enum AppType implements SedaType {
        SESSION, USER_LIST, USER_NAME, STATUS, GAME_DATA, CELL_ID
    }

    public static void main(String[] args) {

        //logic and state
        Flow userList = Flow.newWithName("getUserList")
                .outField(USER_LIST)
                .build();

        Flow userListSessionWriter = Flow.newWithName("notifyUser")
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

        Flow gameData = Flow.newWithName("gameData")
                .outField(GAME_DATA)
                .build();

        Flow broadcastGameData = Flow.newWithName("broadcastGameData")
                .inFields(USER_LIST, GAME_DATA)
                .build();

        Flow clearUserReadyFlagForAll = Flow.newWithName("clearUserReady")
                .inFields(USER_LIST)
                .build();

        Flow assignCellToUser = Flow.newWithName("assignCellToUser")
                .inFields(CELL_ID, SESSION)
                .consumer(gameData)
                .outField(CELL_ID, SESSION, GAME_DATA)
                .build();

        Flow sendUserWinningMessage = Flow.newWithName("sendUserWinningMessage")
                .inFields(SESSION)
                .build();

        Flow broadcastWinnerUsername = Flow.newWithName("broadcastWinner")
                .inFields(USER_LIST, USER_NAME)
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
                        .consumer(clearUserReadyFlagForAll)
                        .build())
                .consumer(broadcastUserStatusChange)
                .build();

        Flow resetGame = Flow.newWithName("resetGame")
                .consumer(gameData)
                .outField(GAME_DATA)
                .build();

        Flow getUsernameBySession = Flow.newWithName("getUsername")
                .inFields(SESSION)
                .consumer(userList)
                .outField(SESSION, USER_NAME)
                .build();

        Flow userClickedCell = Flow.newWithName("userClickedCell")
                .inFields(CELL_ID, SESSION)
                .consumer(assignCellToUser)
                .consumer(userList)
                .ifTrue("userMarkedAllCells", Flow.newWithName("userWonGame")
                        .inFields(SESSION, USER_LIST)
                        .consumer(sendUserWinningMessage)
                        .consumer(getUsernameBySession)
                        .consumer(broadcastWinnerUsername)
                        .consumer(resetGame)
                        .build())
                .consumer(broadcastGameData)
                .build();

        FlowFormatter formatter = new FlowFormatter("->");
        FlowPathGenerator generator = new FlowPathGenerator(formatter);

        generator.generatePaths(clientConnect).forEach(System.out::println);
        generator.generatePaths(userRegistered).forEach(System.out::println);
        generator.generatePaths(userChangedStatus).forEach(System.out::println);
        generator.generatePaths(userClickedCell).forEach(System.out::println);
    }
}
