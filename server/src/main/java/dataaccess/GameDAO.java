package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGamesList();

    GameData getGame(String gameName);

    GameData getGame(int gameID);

    GameData createNewGame(String gameName);

    void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor);

    void clearData();
}
