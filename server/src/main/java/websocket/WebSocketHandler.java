package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;

import model.GameData;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.adapters.GameCommandAdapter;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UserGameCommand.class, new GameCommandAdapter());
            Gson gson = builder.create();
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            int gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            ChessGame.TeamColor teamColor = getTeamColor(username, gameID);

            saveSession(gameID, session);

            System.out.println(command.getCommandType());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, teamColor, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception ex) {
            ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.ERROR;
            sendMessage(session.getRemote(), new ErrorMessage(messageType, "Error: " + ex.getMessage()));

            System.out.println(ex.getMessage());
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        return authData.username();
    }

    private ChessGame.TeamColor getTeamColor(String username, int gameID) throws DataAccessException {
        GameData gameData = gameDao.getGame(gameID);
        if (gameData != null) {
            if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
                return ChessGame.TeamColor.BLACK;
            } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
                return ChessGame.TeamColor.WHITE;
            }
        }

        return null;
    }

    private void connect(Session session, String username, ChessGame.TeamColor teamColor, ConnectCommand command) throws IOException, DataAccessException {
        connections.add(username, session);

        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();

        String broadcastString;
        if (teamColor != null) {
            broadcastString = String.format("%s connected to the game as %s.", username, teamColor);
        } else {
            broadcastString = String.format("%s connected to the game as an observer.", username);
        }

        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        String rootMessage = new Gson().toJson(loadGameMessage);
        connections.sendToRootClient(username, rootMessage);

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, broadcastString);
        String broadcastMessage = new Gson().toJson(notificationMessage);
        connections.broadcast(username, broadcastMessage);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        // TODO: implement makeMove
        // MAKE_MOVE received
        // -> verify move validity
        // -> update game and database
        // -> send a LOAD_GAME message to all clients
        // -> send a NOTIFICATION to all other clients with what move was made
        // -> If the move results in check, checkmate, or stalemate, send a NOTIFICATION message to all clients
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
        // TODO: implement leaveGame
        // LEAVE received
        // -> update game and database
        // -> send a NOTIFICATION to other clients with who left the game
    }

    private void resign(Session session, String username, ResignCommand command) {
        // TODO: implement resign
        // RESIGN received
        // -> game is marked as over (no more moves can be made at this point): update game and database
        // -> send a NOTIFICATION to all clients with who resigned
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) {
        // TODO: implement sendMessage
    }

    private void saveSession(int gameID, Session session) {
        // TODO: implement saveSession
    }
}
