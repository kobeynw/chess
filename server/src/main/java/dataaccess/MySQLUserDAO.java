package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class MySQLUserDAO implements UserDAO {
    public MySQLUserDAO() {}

    public UserData getUser(String username, String password) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM User WHERE username=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);

                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        String usernameResult = result.getString("username");
                        String passwordResult = result.getString("password");
                        String emailResult = result.getString("email");

                        if (BCrypt.checkpw(password, passwordResult)) {
                            return new UserData(usernameResult, passwordResult, emailResult);
                        }
                    }

                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Select UserData From Database: %s", e.getMessage()));
        }
    }

    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        String clearTextPassword = userData.password();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        String email = userData.email();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Update UserData in Database: %s", e.getMessage()));
        }
    }

    public void clearData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE User")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to Delete UserData From Database: %s", e.getMessage()));
        }
    }
}
