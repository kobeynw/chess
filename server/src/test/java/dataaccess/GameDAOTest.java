package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class GameDAOTest {
    private final MySQLGameDAO mySQLGameDAO = new MySQLGameDAO();

    @BeforeEach
    public void clearData() throws DataAccessException {
        mySQLGameDAO.clearData();
    }

    @Test
    public void testCreateNewGame() throws DataAccessException {
        String gameName = "New Game 1";
        ChessGame chessGame = new ChessGame();

        GameData gameDataResult = mySQLGameDAO.createNewGame(gameName);
        GameData expectedGameData = new GameData(1, null, null, gameName, chessGame);

        Assertions.assertEquals(gameDataResult.gameID(), expectedGameData.gameID());
        Assertions.assertEquals(gameDataResult.gameName(), expectedGameData.gameName());
        Assertions.assertNull(gameDataResult.blackUsername());
        Assertions.assertNull(gameDataResult.whiteUsername());

        Assertions.assertEquals(gameDataResult.game().getBoard(), expectedGameData.game().getBoard());
        Assertions.assertEquals(gameDataResult.game().getTeamTurn(), expectedGameData.game().getTeamTurn());
    }

    @Test
    public void testCreateNewGameWithNullName() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            GameData gameDataResult = mySQLGameDAO.createNewGame(null);
        });
    }

    @Test
    public void testGetGameByID() throws DataAccessException {
        String gameName = "New Game 1";
        ChessGame chessGame = new ChessGame();
        GameData gameData = mySQLGameDAO.createNewGame(gameName);
        int gameID = gameData.gameID();

        GameData gameDataResult = mySQLGameDAO.getGame(gameID);
        GameData expectedGameData = new GameData(1, null, null, gameName, chessGame);

        Assertions.assertEquals(gameDataResult.gameID(), expectedGameData.gameID());
        Assertions.assertEquals(gameDataResult.gameName(), expectedGameData.gameName());
        Assertions.assertNull(gameDataResult.blackUsername());
        Assertions.assertNull(gameDataResult.whiteUsername());

        Assertions.assertEquals(gameDataResult.game().getBoard(), expectedGameData.game().getBoard());
        Assertions.assertEquals(gameDataResult.game().getTeamTurn(), expectedGameData.game().getTeamTurn());
    }

    @Test
    public void testGetGameByIDWithInvalidID() throws DataAccessException {
        String gameName = "New Game 1";
        GameData gameData = mySQLGameDAO.createNewGame(gameName);
        int invalidGameID = 2;

        GameData gameDataResult = mySQLGameDAO.getGame(invalidGameID);

        Assertions.assertNull(gameDataResult);
    }

    @Test
    public void testGetGameByGameName() throws DataAccessException {
        String gameName = "New Game 1";
        ChessGame chessGame = new ChessGame();
        GameData gameData = mySQLGameDAO.createNewGame(gameName);

        GameData gameDataResult = mySQLGameDAO.getGame(gameName);
        GameData expectedGameData = new GameData(1, null, null, gameName, chessGame);

        Assertions.assertEquals(gameDataResult.gameID(), expectedGameData.gameID());
        Assertions.assertEquals(gameDataResult.gameName(), expectedGameData.gameName());
        Assertions.assertNull(gameDataResult.blackUsername());
        Assertions.assertNull(gameDataResult.whiteUsername());

        Assertions.assertEquals(gameDataResult.game().getBoard(), expectedGameData.game().getBoard());
        Assertions.assertEquals(gameDataResult.game().getTeamTurn(), expectedGameData.game().getTeamTurn());
    }

    @Test
    public void testGetGameByGameNameWithInvalidGameName() throws DataAccessException {
        String gameName = "New Game 1";
        GameData gameData = mySQLGameDAO.createNewGame(gameName);
        String invalidGameName = "Game 2";

        GameData gameDataResult = mySQLGameDAO.getGame(invalidGameName);

        Assertions.assertNull(gameDataResult);
    }

    @Test
    public void testAddPlayer() throws DataAccessException, InfoTakenException {
        String gameName = "New Game 1";
        String username = "username1";
        GameData gameData = mySQLGameDAO.createNewGame(gameName);

        mySQLGameDAO.addPlayer(gameData, username, ChessGame.TeamColor.WHITE);
        GameData gameDataResult = mySQLGameDAO.getGame(gameName);

        Assertions.assertEquals(gameDataResult.whiteUsername(), username);
        Assertions.assertNull(gameDataResult.blackUsername());
    }

    @Test
    public void testAddPlayerWithBlackAndWhiteTaken() {
        Assertions.assertThrows(InfoTakenException.class, () -> {
            String gameName = "New Game 1";
            String whiteUsername = "username1";
            String blackUsername = "username2";
            String blackUsernameDuplicate = "username3";
            GameData gameData = mySQLGameDAO.createNewGame(gameName);

            mySQLGameDAO.addPlayer(gameData, whiteUsername, ChessGame.TeamColor.WHITE);
            mySQLGameDAO.addPlayer(gameData, blackUsername, ChessGame.TeamColor.BLACK);

            mySQLGameDAO.addPlayer(gameData, blackUsernameDuplicate, ChessGame.TeamColor.BLACK);
        });
    }

    @Test
    public void testGetGamesList() throws DataAccessException {
        String gameName = "New Game 1";
        String gameName2 = "New Game 2";
        GameData gameData = mySQLGameDAO.createNewGame(gameName);
        GameData gameData2 = mySQLGameDAO.createNewGame(gameName2);

        Collection<GameData> gamesList = mySQLGameDAO.getGamesList();

        Assertions.assertEquals(gamesList.size(), 2);
    }

    @Test
    public void testGetGamesListWithNoGames() throws DataAccessException {
        Collection<GameData> gamesList = mySQLGameDAO.getGamesList();

        Assertions.assertTrue(gamesList.isEmpty());
    }

    @Test
    public void testClearData() throws DataAccessException {
        String gameName = "New Game 1";
        GameData gameData = mySQLGameDAO.createNewGame(gameName);

        mySQLGameDAO.clearData();
        GameData gameDataResult = mySQLGameDAO.getGame(gameName);

        Assertions.assertNull(gameDataResult);
    }
}
