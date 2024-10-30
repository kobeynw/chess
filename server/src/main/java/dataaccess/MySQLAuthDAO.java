package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO {
    public MySQLAuthDAO() {}

    public AuthData createAuth(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        String username = userData.username();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, auth_token FROM Auth WHERE username=? AND auth_token=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, authToken);

                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        throw new DataAccessException("Duplicate Auth Token");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select AuthData From Database: %s", e.getMessage()));
        }

        return new AuthData(authToken, username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, auth_token FROM Auth WHERE auth_token=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        String usernameResult = result.getString("username");
                        String authTokenResult = result.getString("auth_token");

                        return new AuthData(authTokenResult, usernameResult);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select AuthData From Database: %s", e.getMessage()));
        }

        return null;
    }

    public boolean deleteAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM Auth WHERE auth_token=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);

                int deletedRowCount = preparedStatement.executeUpdate();

                return deletedRowCount == 1;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select AuthData From Database: %s", e.getMessage()));
        }
    }

    public void clearData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE Auth")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Delete AuthData From Database: %s", e.getMessage()));
        }
    }
}
