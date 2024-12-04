package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO {
    private Collection<GameData> gameDataStorage;
    private int[] gameIDs;

    public MemoryGameDAO() {
        gameDataStorage = new ArrayList<>();
        gameIDs = new int[10000];
    }

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
        int gameID = 1;

        for (int i = 0; i < gameIDs.length; i++) {
            if (gameIDs[i] != i + 1) {
                gameIDs[i] = i + 1;
                gameID = i + 1;

                break;
            }
        }

        GameData newGameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDataStorage.add(newGameData);

        return newGameData;
    }

    public void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor)
            throws DataAccessException, InfoTakenException {
        for (GameData currentGameData : gameDataStorage) {
            if (Objects.equals(gameData, currentGameData)) {
                int currentGameID = currentGameData.gameID();
                String currentWhiteUsername = currentGameData.whiteUsername();
                String currentBlackUsername = currentGameData.blackUsername();
                String currentGameName = currentGameData.gameName();
                ChessGame currentChessGame = currentGameData.game();

                gameDataStorage.remove(currentGameData);
                GameData newGameData;

                if (playerColor == ChessGame.TeamColor.BLACK && currentBlackUsername == null) {
                    newGameData = new GameData(currentGameID, currentWhiteUsername, username, currentGameName,
                            currentChessGame);
                } else if (playerColor == ChessGame.TeamColor.WHITE && currentWhiteUsername == null) {
                    newGameData = new GameData(currentGameID, username, currentBlackUsername, currentGameName,
                            currentChessGame);
                } else {
                    throw new InfoTakenException("already taken");
                }

                gameDataStorage.add(newGameData);
                return;
            }
        }

        throw new DataAccessException("Game Data Not Found");
    }

    public void removePlayer(GameData gameData, String username, ChessGame.TeamColor playerColor)
            throws DataAccessException {}

    public void updateGame(GameData gameData, ChessGame game) throws DataAccessException {}

    public void clearData() {
        gameDataStorage = new ArrayList<>();
        gameIDs = new int[10000];
    }
}
