package org.flow;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.flow.core.*;
import org.flow.core.SedaType;

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
        FlowPathGenerator generator = new FlowPathGenerator(formatter);

        Data userList = Data.createNew("userList", USER_LIST, SESSION, NAME, STATUS);

        Action sendUserListToClient = Action.createNew("sendUserListToClient", FieldSet.of(USER_LIST, SESSION), FieldSet.empty())
                .use("bind status to user", USER_LIST, SESSION)
                .build();

        Action clientConnected = Action.createNew("clientConnected", FieldSet.of(SESSION))
                .read(userList, USER_LIST, SESSION)
                .execute(sendUserListToClient)
                .build();

        Action broadcastUserList = Action.createNew("broadcastUserList")
                .read(userList, USER_LIST)
                .use("bind status to user", USER_LIST)
                .build();

        Action clientProvidedName = Action.createNew("clientProvidedName", FieldSet.of(SESSION, NAME))
                .update(userList, SESSION, NAME)
                .execute(broadcastUserList)
                .build();

        Action updatePlayerStatus = Action.createNew("updatePlayerStatus", FieldSet.of(SESSION, STATUS))
                .update(userList, SESSION, STATUS)
                .build();

        Data game = Data.createNew("game", USER_LIST, DATA, CELL_ID);

        Action startGame = Action.createNew("startGame", FieldSet.of(USER_LIST))
                .update(game, USER_LIST)
                .build();

        LoggingFlow flow = new LoggingFlow("start", Lists.newArrayList(), Maps.newHashMap());
        Action.createNew("clientUpdatedStatus", FieldSet.of(SESSION, STATUS))
                .withFlow(flow)
                .read(userList, USER_LIST)
                .execute(updatePlayerStatus)
                .executeIf("Is the last player ready", startGame)
                .execute(broadcastUserList)
                .build();

        generator.generatePaths(flow).forEach(System.out::println);
    }
}
