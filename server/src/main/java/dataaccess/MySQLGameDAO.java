package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() {}

    public Collection<GameData> getGamesList() {
        Collection<GameData> gamesList = new ArrayList<>();

        // return all gameData entries

        return gamesList;
    }

    public GameData getGame(String gameName) {
        // check all gameData
        // if gameData gameName equals argument gameName, return gameData

        return null;
    }

    public GameData getGame(int gameID) {
        // check all gameData
        // if gameData gameID equals argument gameID, return gameData

        return null;
    }

    public GameData createNewGame(String gameName) {
        // create a new gameData with gameName set to argument gameName
        // get the primary key gameID and create a new GameData object with gameID set to it

        return newGameData;
    }

    public void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor)
            throws DataAccessException, InfoTakenException {
        Int gameID = gameData.gameID();

        // check all gameData
        // if gameID matches argument gameData gameID, update the info with remaining argument values

        throw new DataAccessException("Game Data Not Found");
    }

    public void clearData() {
        // delete all gameData entries
    }
}
