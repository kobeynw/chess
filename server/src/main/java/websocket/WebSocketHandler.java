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
            connections.add("", session, 0);
            sendMessage(false, "", ServerMessage.ServerMessageType.ERROR, ex.getMessage(), null, false);
            connections.remove("");
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
            connections.add(username, session, 0);
            String errorString = "Error: Session Halted";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
            connections.remove(username);
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData != null) {
            return authData.username();
        } else {
            throw new DataAccessException("Error: Unauthorized");
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
        int gameID = command.getGameID();
        connections.add(username, session, gameID);

        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();

        String broadcastString;
        if (teamColor != null) {
            broadcastString = String.format("%s connected to the game as %s.", username, teamColor);
        } else {
            broadcastString = String.format("%s connected to the game as an observer.", username);
        }

        sendMessage(false, username, ServerMessage.ServerMessageType.LOAD_GAME, null, game, false);
        sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null, false);
    }

    private void makeMove(String username, MakeMoveCommand command)
            throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        ChessGame game = gameData.game();
        ChessMove move = command.getMove();
        ChessPiece.PieceType piece = game.getBoard().getPiece(move.getStartPosition()).getPieceType();
        ChessPosition toPosition = move.getEndPosition();

        if (!gameData.whiteUsername().equals(username) && !gameData.blackUsername().equals(username)) {
            String errorString = "Error: Observer cannot make moves.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
            return;
        }

        boolean isWhiteTurn = game.getTeamTurn().equals(ChessGame.TeamColor.WHITE) && gameData.whiteUsername().equals(username);
        boolean isBlackTurn = game.getTeamTurn().equals(ChessGame.TeamColor.BLACK) && gameData.blackUsername().equals(username);
        if (!isWhiteTurn && !isBlackTurn) {
            String errorString = "Error: Not your turn.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
            return;
        }

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
                sendMessage(true, username, ServerMessage.ServerMessageType.LOAD_GAME, null, game, true);
                sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, moveString, null, false);

                if (game.isInCheck(oppositeColor)) {
                    String checkString = String.format("%s is in check!", oppositeColor);
                    sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, checkString, null, true);
                } else if (game.isInCheckmate(oppositeColor)) {
                    String checkmateString = String.format("Checkmate! %s wins!", username);
                    sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, checkmateString, null, true);

                    game.setFinished(true);
                    gameDao.updateGame(gameData, game);
                } else if (game.isInStalemate(oppositeColor)) {
                    String stalemateString = "Stalemate! It's a draw!";
                    sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, stalemateString, null, true);

                    game.setFinished(true);
                    gameDao.updateGame(gameData, game);
                }
            } catch (InvalidMoveException e) {
                String errorString = "Error: Invalid move.";
                sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
            }
        } else {
            String errorString = "Error: Game already finished.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
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

        sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null, false);

        connections.remove(username);
    }

    private void resign(String username, ResignCommand command)
            throws IOException, DataAccessException {
        int gameID = command.getGameID();
        GameData gameData = gameDao.getGame(gameID);
        ChessGame currentGame = gameData.game();

        if (!gameData.whiteUsername().equals(username) && !gameData.blackUsername().equals(username)) {
            String errorString = "Error: Observer cannot resign.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
            return;
        }

        if (!currentGame.isFinished()) {
            currentGame.setFinished(true);
            gameDao.updateGame(gameData, currentGame);

            String rootString = "You resigned from the game";
            sendMessage(false, username, ServerMessage.ServerMessageType.NOTIFICATION, rootString, null, false);

            String broadcastString = String.format("%s resigned from the game.", username);
            sendMessage(true, username, ServerMessage.ServerMessageType.NOTIFICATION, broadcastString, null, false);
        } else {
            String errorString = "Error: Game already finished.";
            sendMessage(false, username, ServerMessage.ServerMessageType.ERROR, errorString, null, false);
        }
    }

    private void sendMessage(boolean isBroadcast, String username, ServerMessage.ServerMessageType type, String message,
                             ChessGame game, boolean toRoot) throws IOException {
        if (type.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            NotificationMessage notificationMessage = new NotificationMessage(type, message);
            String messageString = new Gson().toJson(notificationMessage);
            if (isBroadcast) {
                connections.broadcast(username, messageString, toRoot);
            } else {
                connections.sendToRootClient(username, messageString);
            }
        } else if (type.equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGameMessage loadGameMessage = new LoadGameMessage(type, game);
            String messageString = new Gson().toJson(loadGameMessage);
            if (isBroadcast) {
                connections.broadcast(username, messageString, toRoot);
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
