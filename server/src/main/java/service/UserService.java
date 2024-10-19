package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;

public class UserService extends Services {
    // NOTE: Implements login, logout, and register services
    // NOTE: Utilize the Services parent class to validate auth tokens

    public UserService() {}

    public LoginResult loginService(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {
        UserData userData = validateUser(loginRequest);

        if (userData == null) {
            throw new UnauthorizedException("unauthorized");
        }

        MemoryAuthDAO authDao = new MemoryAuthDAO();
        AuthData authData = authDao.createAuth(userData);

        String username = authData.username();
        String authToken = authData.authToken();

        return new LoginResult(username, authToken);
    }

    private UserData validateUser(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        MemoryUserDAO userDao = new MemoryUserDAO();

        return userDao.getUser(username, password);
    }
}
