package network;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ui.ServerMessageObserver;
import websocket.adapters.ServerMessageAdapter;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebsocketCommunicator extends Endpoint {
    public Session session;

    public WebsocketCommunicator(ServerMessageObserver observer, String urlString) {
        try {
            URI uri = new URI(urlString);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(ServerMessage.class, new ServerMessageAdapter());
                    Gson gson = builder.create();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    observer.notify(serverMessage);
                }
            });
        } catch (Exception ex) {
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
}
