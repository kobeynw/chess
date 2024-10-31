package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MySQLGameDAO implements GameDAO {
    public MySQLGameDAO() {}

    public Collection<GameData> getGamesList() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id, white_username, black_username, game_name, game FROM Game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        int gameIDResult = result.getInt("game_id");
                        String whiteUsernameResult = result.getString("white_username");
                        String blackUsernameResult = result.getString("black_username");
                        String gameNameResult = result.getString("game_name");
                        String gameResult = result.getString("game");

                        ChessGame chessGame = new Gson().fromJson(gameResult, ChessGame.class);

                        gamesList.add(new GameData(gameIDResult, whiteUsernameResult, blackUsernameResult,
                                gameNameResult, chessGame));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select GameData From Database: %s", e.getMessage()));
        }

        return gamesList;
    }

    public GameData fetchGameData(PreparedStatement preparedStatement) throws SQLException {
        try (var result = preparedStatement.executeQuery()) {
            if (result.next()) {
                int gameIDResult = result.getInt("game_id");
                String whiteUsernameResult = result.getString("white_username");
                String blackUsernameResult = result.getString("black_username");
                String gameNameResult = result.getString("game_name");
                String gameResult = result.getString("game");

                ChessGame chessGame = new Gson().fromJson(gameResult, ChessGame.class);

                return new GameData(gameIDResult, whiteUsernameResult, blackUsernameResult,
                        gameNameResult, chessGame);
            }

            return null;
        }
    }

    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id, white_username, black_username, game_name, game " +
                    "FROM Game WHERE game_name=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gameName);
                GameData gameDataResult = fetchGameData(preparedStatement);

                if (gameDataResult != null && Objects.equals(gameDataResult.gameName(), gameName)) {
                    return gameDataResult;
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select GameData From Database: %s", e.getMessage()));
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id, white_username, black_username, game_name, game " +
                    "FROM Game WHERE game_id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                GameData gameDataResult = fetchGameData(preparedStatement);

                if (gameDataResult != null && Objects.equals(gameDataResult.gameID(), gameID)) {
                    return gameDataResult;
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select GameData From Database: %s", e.getMessage()));
        }
    }

    public GameData createNewGame(String gameName) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        String gameString = new Gson().toJson(chessGame, ChessGame.class);
        int gameID = 0;

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO Game (game_name, game) VALUES (?, ?)";

            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, gameString);

                preparedStatement.executeUpdate();

                var result = preparedStatement.getGeneratedKeys();
                if (result.next()) {
                    gameID = result.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Update GameData in Database: %s", e.getMessage()));
        }

        if (gameID > 0) {
            return new GameData(gameID, null, null, gameName, chessGame);
        } else {
            throw new DataAccessException("Unable to Retrieve GameID from Database");
        }
    }

    public void addPlayer(GameData gameData, String username, ChessGame.TeamColor playerColor)
            throws DataAccessException, InfoTakenException {
        int gameID = gameData.gameID();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "";
            if (playerColor == ChessGame.TeamColor.BLACK) {
                statement = "UPDATE Game SET black_username = ? WHERE game_id = ? AND black_username IS NULL";
            } else {
                statement = "UPDATE Game SET white_username = ? WHERE game_id = ? AND white_username IS NULL";
            }

            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, gameID);

                int updatedRowsCount = preparedStatement.executeUpdate();

                if (updatedRowsCount == 0) {
                    throw new InfoTakenException("already taken");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Update GameData in Database: %s", e.getMessage()));
        }
    }

    public void clearData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE Game")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Delete GameData From Database: %s", e.getMessage()));
        }
    }
}
