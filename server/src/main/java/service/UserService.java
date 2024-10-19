package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;

public class UserService extends Services {
    // NOTE: Implements login, logout, and register services
    // NOTE: Utilize the Services parent class to validate auth tokens

    public UserService() {}

    public LoginResult loginService(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = validateUser(loginRequest);

        MemoryAuthDAO authDao = new MemoryAuthDAO();
        AuthData authData = authDao.createAuth(userData);

        String username = authData.username();
        String authToken = authData.authToken();

        return new LoginResult(username, authToken);
    }

    private UserData validateUser(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        MemoryUserDAO userDao = new MemoryUserDAO();
        UserData userData = userDao.getUser(username, password);

        if (userData == null) {
            throw new DataAccessException("Invalid User");
        } else {
            return userData;
        }
    }
}
