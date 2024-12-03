package websocket.adapters;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class ServerMessageAdapter extends TypeAdapter<ServerMessage> {
    @Override
    public void write(JsonWriter jsonWriter, ServerMessage serverMessage) throws IOException {
        Gson gson = new Gson();

        switch(serverMessage.getServerMessageType()) {
            case LOAD_GAME -> gson.getAdapter(LoadGameMessage.class).write(jsonWriter, (LoadGameMessage) serverMessage);
            case ERROR -> gson.getAdapter(ErrorMessage.class).write(jsonWriter, (ErrorMessage) serverMessage);
            case NOTIFICATION -> gson.getAdapter(NotificationMessage.class).write(jsonWriter, (NotificationMessage) serverMessage);
        }
    }

    @Override
    public ServerMessage read(JsonReader jsonReader) throws IOException {
        ServerMessage.ServerMessageType serverMessageType = null;
        String errorMessage = null;
        ChessGame game = null;
        String message = null;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "serverMessageType" -> serverMessageType = ServerMessage.ServerMessageType.valueOf(jsonReader.nextString());
                case "errorMessage" -> errorMessage = jsonReader.nextString();
                case "game" -> game = new Gson().fromJson(jsonReader, ChessGame.class);
                case "message" -> message = jsonReader.nextString();
            }
        }

        jsonReader.endObject();

        if(serverMessageType == null) {
            return null;
        } else {
            return switch (serverMessageType) {
                case LOAD_GAME -> new LoadGameMessage(serverMessageType, game);
                case ERROR -> new ErrorMessage(serverMessageType, errorMessage);
                case NOTIFICATION -> new NotificationMessage(serverMessageType, message);
            };
        }
    }
}
