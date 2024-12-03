package handler;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    GameDAO gameDao;
    AuthDAO authDao;

    public WebSocketHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public void onMessage(Session session, String msg) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UserGameCommand.class, new GameCommandAdapter());
            Gson gson = builder.create();
            UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);

            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            ServerMessage.ServerMessageType msgType = ServerMessage.ServerMessageType.ERROR;
            sendMessage(session.getRemote(), new ErrorMessage(msgType, "Error: Unauthorized"));
        } catch (Exception ex) {
            ServerMessage.ServerMessageType msgType = ServerMessage.ServerMessageType.ERROR;
            sendMessage(session.getRemote(), new ErrorMessage(msgType, "Error: " + ex.getMessage()));
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        return authData.username();
    }

    private void connect(Session session, String username, ConnectCommand command) throws UnauthorizedException {
        // TODO: implement connect
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        // TODO: implement makeMove
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
        // TODO: implement leaveGame
    }

    private void resign(Session session, String username, ResignCommand command) {
        // TODO: implement resign
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage msg) {
        // TODO: implement sendMessage
    }

    private void saveSession(int gameID, Session session) {
        // TODO: implement saveSession
    }

    private static class GameCommandAdapter extends TypeAdapter<UserGameCommand> {
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
}
