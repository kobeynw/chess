package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    // NOTE: If an auth token is received, it MUST be validated no matter what
    // NOTE: Methods that could fail need to throw a DataAccessException

    AuthData createAuth(UserData userData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void clearData() throws DataAccessException;
}
