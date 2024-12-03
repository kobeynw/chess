package websocket.adapters;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import websocket.commands.*;

import java.io.IOException;

public class GameCommandAdapter extends TypeAdapter<UserGameCommand> {
    @Override
    public void write(JsonWriter jsonWriter, UserGameCommand gameCommand) throws IOException {
        Gson gson = new Gson();

        switch(gameCommand.getCommandType()) {
            case CONNECT -> gson.getAdapter(ConnectCommand.class).write(jsonWriter, (ConnectCommand) gameCommand);
            case MAKE_MOVE -> gson.getAdapter(MakeMoveCommand.class).write(jsonWriter, (MakeMoveCommand) gameCommand);
            case LEAVE -> gson.getAdapter(LeaveGameCommand.class).write(jsonWriter, (LeaveGameCommand) gameCommand);
            case RESIGN -> gson.getAdapter(ResignCommand.class).write(jsonWriter, (ResignCommand) gameCommand);
        }
    }

    @Override
    public UserGameCommand read(JsonReader jsonReader) throws IOException {
        UserGameCommand.CommandType commandType = null;
        String authToken = null;
        int gameID = 0;
        ChessMove move = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "commandType" -> commandType = UserGameCommand.CommandType.valueOf(jsonReader.nextString());
                case "authToken" -> authToken = jsonReader.nextString();
                case "gameID" -> gameID = jsonReader.nextInt();
                case "move" -> move = new Gson().fromJson(jsonReader, ChessMove.class);
            }
        }

        jsonReader.endObject();

        if(commandType == null) {
            return null;
        } else {
            return switch (commandType) {
                case CONNECT -> new ConnectCommand(commandType, authToken, gameID);
                case MAKE_MOVE -> new MakeMoveCommand(commandType, authToken, gameID, move);
                case LEAVE -> new LeaveGameCommand(commandType, authToken, gameID);
                case RESIGN -> new ResignCommand(commandType, authToken, gameID);
            };
        }
    }
}