package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGamesList() throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData createNewGame(String gameName) throws DataAccessException;

    void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor)
            throws DataAccessException, InfoTakenException;

    void removePlayer(GameData gameData, String username, ChessGame.TeamColor playerColor) throws DataAccessException;

    void updateGame(GameData gameData, ChessGame game) throws DataAccessException;

    void clearData() throws DataAccessException;
}
