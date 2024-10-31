package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAOTest {
    private final MySQLUserDAO mySQLUserDAO = new MySQLUserDAO();
    private final String username = "username";
    private final String password = "password";
    private final String email = "email@example.com";

    @BeforeEach
    public void clearData() throws DataAccessException {
        mySQLUserDAO.clearData();
    }

    @Test
    public void testCreateUser() throws DataAccessException {
        UserData userData = new UserData(username, password, email);

        mySQLUserDAO.createUser(userData);
        UserData userDataResult = mySQLUserDAO.getUser(username, password);

        Assertions.assertNotNull(userDataResult);
    }

    @Test
    public void testCreateUserWithNullInfo() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            UserData nullUserData = new UserData(null, null, null);

            mySQLUserDAO.createUser(nullUserData);
        });
    }

    @Test
    public void testGetUser() throws DataAccessException {
        UserData userData = new UserData(username, password, email);

        mySQLUserDAO.createUser(userData);
        UserData userDataResult = mySQLUserDAO.getUser(username, password);

        Assertions.assertEquals(userDataResult.username(), username);
        Assertions.assertEquals(userDataResult.email(), email);
        Assertions.assertTrue(BCrypt.checkpw(password, userDataResult.password()));
    }

    @Test
    public void testGetUserInvalidUserInfo() throws DataAccessException {
        UserData userData = new UserData("randomUsername", password, email);

        mySQLUserDAO.createUser(userData);
        UserData userDataResult = mySQLUserDAO.getUser(username, password);

        Assertions.assertNull(userDataResult);
    }

    @Test
    public void testClearData() throws DataAccessException {
        UserData userData = new UserData(username, password, email);
        mySQLUserDAO.createUser(userData);

        mySQLUserDAO.clearData();
        UserData userDataResult = mySQLUserDAO.getUser(username, password);

        Assertions.assertNull(userDataResult);
    }
}
