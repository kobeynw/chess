package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    UserDAO userDao;
    AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult registerService(RegisterRequest registerRequest)
            throws InfoTakenException, BadRequestException, DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        UserData existingUserData = validateUser(new LoginRequest(username, password));

        if (existingUserData != null) {
            throw new InfoTakenException("already taken");
        }

        UserData newUserData = new UserData(username, password, email);
        userDao.createUser(newUserData);

        AuthData authData = authDao.createAuth(newUserData);

        return new RegisterResult(authData.username(), authData.authToken());
    }

    public LoginResult loginService(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {
        UserData userData = validateUser(loginRequest);

        if (userData == null) {
            throw new UnauthorizedException("unauthorized");
        }

        AuthData authData = authDao.createAuth(userData);

        return new LoginResult(authData.username(), authData.authToken());
    }

    public LogoutResult logoutService(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        String authToken = logoutRequest.authToken();
        AuthData authData = authDao.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException("unauthorized");
        }

        if (!authDao.deleteAuth(authData)) {
            throw new DataAccessException("Auth Token not found");
        }

        return new LogoutResult();
    }

    private UserData validateUser(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        return userDao.getUser(username, password);
    }
}
