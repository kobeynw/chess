package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private Collection<GameData> gameDataStorage = new ArrayList<>();

    public MemoryGameDAO() {}

    public Collection<GameData> getGamesList() {
        return gameDataStorage;
    }

    public GameData getGame(String gameName) {
        for (GameData gameData : gameDataStorage) {
            if (Objects.equals(gameData.gameName(), gameName)) {
                return gameData;
            }
        }

        return null;
    }

    public GameData getGame(int gameID) {
        for (GameData gameData : gameDataStorage) {
            if (Objects.equals(gameData.gameID(), gameID)) {
                return gameData;
            }
        }

        return null;
    }

    public GameData createNewGame(String gameName) {
        int gameID = 1234;
        // TODO: make the gameID a unique identifier similar to authTokens using UUID
        return new GameData(gameID, "", "", gameName, new ChessGame());
    }

    public void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor) {
        for (GameData currentGameData : gameDataStorage) {
            if (Objects.equals(gameData, currentGameData)) {
                int currentGameID = currentGameData.gameID();
                String currentWhiteUsername = currentGameData.whiteUsername();
                String currentBlackUsername = currentGameData.blackUsername();
                String currentGameName = currentGameData.gameName();
                ChessGame currentChessGame = currentGameData.game();

                gameDataStorage.remove(currentGameData);
                GameData newGameData;

                if (playerColor == ChessGame.TeamColor.BLACK) {
                    newGameData = new GameData(currentGameID, currentWhiteUsername, username, currentGameName,
                            currentChessGame);
                } else {
                    newGameData = new GameData(currentGameID, username, currentBlackUsername, currentGameName,
                            currentChessGame);
                }

                gameDataStorage.add(newGameData);
            }
        }
    }

    public void clearData() {
        gameDataStorage = new ArrayList<>();
    }
}
