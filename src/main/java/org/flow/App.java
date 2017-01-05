package org.flow;

import seda.Flow;
import seda.FlowFormatter;
import seda.FlowPathGenerator;
import seda.SedaType;

import static org.flow.App.AppType.*;

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

        //logic and state
        Flow userList = Flow.newWithName("getUserList")
                .outField(USER_LIST)
                .build();

        Flow sendUserListToUser = Flow.newWithName("sendUserListToUser")
                .inFields(User.SESSION, USER_LIST)
                .build();

        Flow broadcastUserList = Flow.newWithName("broadcastUserList")
                .inFields(USER_LIST)
                .build();

        Flow updateUsername = Flow.newWithName("updateUsername")
                .inFields(User.NAME, User.SESSION)
                .consumer(userList)
                .outField(User.NAME, User.SESSION, USER_LIST)
                .build();

        Flow updateStatus = Flow.newWithName("updateStatus")
                .inFields(User.SESSION, User.STATUS)
                .consumer(userList)
                .outField(User.SESSION,User.STATUS, USER_LIST)
                .build();

        Flow checkIfAllUsersAreReady = Flow.newWithName("checkIfAllUsersAreReady")
                .inFields(USER_LIST)
                .build();

        Flow gameData = Flow.newWithName("gameData")
                .outField(Game.DATA)
                .build();

        Flow broadcastGameData = Flow.newWithName("broadcastGameData")
                .inFields(USER_LIST, Game.DATA)
                .build();

        Flow clearUserReadyFlagForAll = Flow.newWithName("clearUserReady")
                .inFields(USER_LIST)
                .build();

        Flow sendUserWinningMessage = Flow.newWithName("sendUserWinningMessage")
                .inFields(User.SESSION)
                .build();

        Flow broadcastWinnerUsername = Flow.newWithName("broadcastWinner")
                .inFields(USER_LIST, User.NAME)
                .build();

        Flow checkIfUserAlreadyNamed = Flow.newWithName("checkIfUserHasUsername")
                .inFields(USER_LIST, User.NAME, User.SESSION)
                .build();

        Flow removeUserFromList = Flow.newWithName("removeUserFromList")
                .inFields(User.SESSION)
                .consumer(userList)
                .outField(User.SESSION, USER_LIST)
                .build();

        //client input
        Flow clientConnect = Flow.newWithName("clientConnect")
                .inFields(User.SESSION)
                .consumer(userList)
                .consumer(sendUserListToUser)
                .outField(USER_LIST)
                .build();

        Flow userProvidedName = Flow.newWithName("userProvidedName")
                .inFields(User.NAME, User.SESSION)
                .consumer(userList)
                .consumer(checkIfUserAlreadyNamed)
                .ifTrue("usernameNotPresent", Flow.newWithName("bindUsernameAndBroadcastChangeSeq")
                        .inFields(User.NAME, User.SESSION)
                        .consumer(updateUsername)
                        .consumer(broadcastUserList)
                        .outField(User.NAME, User.SESSION, USER_LIST)
                        .build())
                .build();

        Flow userChangedStatus = Flow.newWithName("userChangedStatus")
                .inFields(User.SESSION, User.STATUS)
                .consumer(updateStatus)
                .consumer(checkIfAllUsersAreReady)
                .ifTrue("everyoneIsReady", Flow.newWithName("startGame")
                        .inFields(USER_LIST)
                        .consumer(gameData)
                        .consumer(broadcastGameData)
                        .consumer(clearUserReadyFlagForAll)
                        .build())
                .consumer(broadcastUserList)
                .build();

        Flow getUsernameBySession = Flow.newWithName("getUsername")
                .inFields(User.SESSION)
                .consumer(userList)
                .outField(User.SESSION, User.NAME)
                .build();

        Flow userClickedCell = Flow.newWithName("userClickedCell")
                .inFields(Game.CELL_ID, User.SESSION)
                .ifTrue("cellNotAssigned", Flow.newWithName("assignCellToUser")
                    .inFields(Game.CELL_ID, User.SESSION)
                    .consumer(gameData)
                    .consumer(userList)
                    .ifTrue("userMarkedAllCells", Flow.newWithName("userWonGame")
                        .inFields(User.SESSION, USER_LIST)
                        .consumer(sendUserWinningMessage)
                        .consumer(getUsernameBySession)
                        .consumer(broadcastWinnerUsername)
                    .build())
                    .consumer(broadcastGameData)
                    .outField(Game.CELL_ID, User.SESSION, Game.DATA).build())
                .build();

        Flow userDisconnected = Flow.newWithName("userDisconnected")
                .inFields(User.SESSION)
                .consumer(removeUserFromList)
                .ifTrue("userIsRemoved", Flow.newWithName("userRemoved")
                        .inFields(USER_LIST)
                        .consumer(broadcastUserList)
                        .build())
                .build();

        FlowFormatter formatter = new FlowFormatter("->");
        FlowPathGenerator generator = new FlowPathGenerator(formatter);

        generator.generatePaths(clientConnect).forEach(System.out::println);
        generator.generatePaths(userDisconnected).forEach(System.out::println);
        generator.generatePaths(userProvidedName).forEach(System.out::println);
        generator.generatePaths(userChangedStatus).forEach(System.out::println);
        generator.generatePaths(userClickedCell).forEach(System.out::println);
    }
}
