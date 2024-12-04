package websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;

import model.GameData;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.adapters.GameCommandAdapter;
import websocket.commands.*;
import websocket.messages.*;

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
    public void onMessage(Session session, String message) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(UserGameCommand.class, new GameCommandAdapter());
        Gson gson = builder.create();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

        String username = null;
        try {
            username = getUsername(command.getAuthToken());
        } catch (DataAccessException ex) {
            // TODO: print unauthorized error
            ex.printStackTrace();
        }

        try {
            int gameID = command.getGameID();
            ChessGame.TeamColor teamColor = getTeamColor(username, gameID);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, teamColor, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(username, teamColor, (LeaveGameCommand) command);
                case RESIGN -> resign(username, (ResignCommand) command);
            }
        } catch (Exception ex) {
            ServerMessage.ServerMessageType messageType = ServerMessage.ServerMessageType.ERROR;
            ErrorMessage errorMessage = new ErrorMessage(messageType, "Error: " + ex.getMessage());
            String rootMessage = new Gson().toJson(errorMessage);
            connections.sendToRootClient(username, rootMessage);
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData != null) {
            return authData.username();
        } else {
            throw new DataAccessException("Unauthorized");
        }
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

    private void connect(Session session, String username, ChessGame.TeamColor teamColor, ConnectCommand command)
            throws IOException, DataAccessException {
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

        sendMessage(false, username, ServerMessage.ServerMessageType.LOAD_GAME, null, game);
        sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null);
    }

    private void makeMove(String username, MakeMoveCommand command)
            throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();
        ChessMove move = command.getMove();
        ChessPiece.PieceType piece = game.getBoard().getPiece(move.getStartPosition()).getPieceType();
        ChessPosition toPosition = move.getEndPosition();

        if (!game.isFinished()) {
            try {
                ChessGame.TeamColor currentColor = game.getTeamTurn();
                ChessGame.TeamColor oppositeColor = ChessGame.TeamColor.WHITE;
                if (currentColor.equals(ChessGame.TeamColor.WHITE)) {
                    oppositeColor = ChessGame.TeamColor.BLACK;
                }

                game.makeMove(move);
                game.setTeamTurn(oppositeColor);
                gameDao.updateGame(gameData, game);

                String moveString = String.format("%s moved a %s to %s.", username, piece, toPosition);
                sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, moveString, null);
                sendMessage(true, null, ServerMessage.ServerMessageType.LOAD_GAME, null, game);

                if (game.isInCheck(oppositeColor)) {
                    String checkString = String.format("%s is in check!", oppositeColor);
                    sendMessage(true, null, ServerMessage.ServerMessageType.NOTIFICATION, checkString, null);
                } else if (game.isInCheckmate(oppositeColor)) {
                    String checkmateString = String.format("Checkmate! %s wins!", username);
                    sendMessage(true, null, ServerMessage.ServerMessageType.NOTIFICATION, checkmateString, null);

                    game.setFinished(true);
                    gameDao.updateGame(gameData, game);
                } else if (game.isInStalemate(oppositeColor)) {
                    String stalemateString = "Stalemate! It's a draw!";
                    sendMessage(true, null, ServerMessage.ServerMessageType.NOTIFICATION, stalemateString, null);

                    game.setFinished(true);
                    gameDao.updateGame(gameData, game);
                }
            } catch (InvalidMoveException e) {
                String errorString = "Invalid move.";
                sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null);
            }
        } else {
            String errorString = "Game already finished.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null);
        }
    }

    private void leaveGame(String username, ChessGame.TeamColor teamColor, LeaveGameCommand command)
            throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);

        String broadcastString;
        if (teamColor != null) {
            gameDao.removePlayer(gameData, username, teamColor);
            broadcastString = String.format("%s left the game (%s is now available).", username, teamColor);
        } else {
            broadcastString = String.format("%s stopped observing the game.", username);
        }

        sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null);

        connections.remove(username);
    }

    private void resign(String username, ResignCommand command)
            throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        ChessGame currentGame = gameData.game();

        currentGame.setFinished(true);
        gameDao.updateGame(gameData, currentGame);

        String rootString = "You resigned from the game";
        sendMessage(false, username, ServerMessage.ServerMessageType.LOAD_GAME, rootString, null);

        String broadcastString = String.format("%s resigned from the game.", username);
        sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null);
    }

    private void sendMessage(boolean isBroadcast, String username, ServerMessage.ServerMessageType type, String message, ChessGame game)
            throws IOException {
        if (type.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            NotificationMessage notificationMessage = new NotificationMessage(type, message);
            String messageString = new Gson().toJson(notificationMessage);
            if (isBroadcast) {
                connections.broadcast(username, messageString);
            } else {
                connections.sendToRootClient(username, messageString);
            }
        } else if (type.equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(type, game);
            String messageString = new Gson().toJson(loadGameMessage);
            if (isBroadcast) {
                connections.broadcast(username, messageString);
            } else {
                connections.sendToRootClient(username, messageString);
            }
        } else if (type.equals(ServerMessage.ServerMessageType.ERROR)) {
            ErrorMessage errorMessage = new ErrorMessage(type, message);
            String messageString = new Gson().toJson(errorMessage);
            connections.sendToRootClient(username, messageString);
        }
    }
}
