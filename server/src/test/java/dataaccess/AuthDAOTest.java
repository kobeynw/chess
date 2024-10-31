package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthDAOTest {
    private final MySQLAuthDAO mySQLAuthDAO = new MySQLAuthDAO();
    private final String username = "username";
    private final String password = "password";
    private final String email = "email@example.com";

    @BeforeEach
    public void clearData() throws DataAccessException {
        mySQLAuthDAO.clearData();
    }

    @Test
    public void testCreateAuth() throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        AuthData authDataResult = mySQLAuthDAO.createAuth(userData);

        Assertions.assertEquals(authDataResult.username(), username);
        Assertions.assertNotNull(authDataResult.authToken());
    }

    @Test
    public void testCreateAuthWithNullUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            UserData userData = new UserData(null, password, email);
            AuthData authDataResult = mySQLAuthDAO.createAuth(userData);
        });
    }

    @Test
    public void testGetAuth() throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        AuthData authData = mySQLAuthDAO.createAuth(userData);

        String authToken = authData.authToken();
        AuthData authDataResult = mySQLAuthDAO.getAuth(authToken);

        Assertions.assertEquals(authDataResult.username(), username);
        Assertions.assertNotNull(authDataResult.authToken());
    }

    @Test
    public void testGetAuthWithNoMatch() throws DataAccessException {
        String randomAuthToken = "random";
        AuthData authDataResult = mySQLAuthDAO.getAuth(randomAuthToken);

        Assertions.assertNull(authDataResult);
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        AuthData authData = mySQLAuthDAO.createAuth(userData);

        boolean authDataDeleted = mySQLAuthDAO.deleteAuth(authData);

        Assertions.assertTrue(authDataDeleted);
    }

    @Test
    public void testDeleteAuthWithNoAuthData() throws DataAccessException {
        AuthData randomAuthData = new AuthData("randomAuth", "randomUser");
        boolean authDataDeleted = mySQLAuthDAO.deleteAuth(randomAuthData);

        Assertions.assertFalse(authDataDeleted);
    }

    @Test
    public void testClearData() throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        AuthData authData = mySQLAuthDAO.createAuth(userData);

        mySQLAuthDAO.clearData();
        AuthData authDataResult = mySQLAuthDAO.getAuth(authData.authToken());

        Assertions.assertNull(authDataResult);
    }
}
