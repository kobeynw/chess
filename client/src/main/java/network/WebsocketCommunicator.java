package network;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ui.ServerMessageObserver;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    public Session session;

    public WebsocketCommunicator(ServerMessageObserver observer, String urlString) {
        try {
            URI uri = new URI(urlString);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(ServerMessage.class, new ServerMessageAdapter());
                    Gson gson = builder.create();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    observer.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            observer.notify(errorMessage);
        }
    }

    public void doConnect(String authToken, int gameID) throws Exception {
        ConnectCommand connectCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
    }

    public void doMakeMove(String authToken, int gameID, ChessMove move) throws Exception {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
    }

    public void doLeave(String authToken, int gameID) throws Exception {
        LeaveGameCommand leaveCommand = new LeaveGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
    }

    public void doResign(String authToken, int gameID) throws Exception {
        ResignCommand resignCommand = new ResignCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    private static class ServerMessageAdapter extends TypeAdapter<ServerMessage> {
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
}
