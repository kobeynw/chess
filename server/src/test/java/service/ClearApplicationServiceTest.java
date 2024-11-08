package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

public class ClearApplicationServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearApplicationService clearService = new ClearApplicationService(userDAO, authDAO, gameDAO);
    private final String username = "username";
    private final String password = "password";

    private String getAuth() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        RegisterRequest registerRequest = new RegisterRequest(username, password,
                "email@example.com");
        RegisterResult r = userService.registerService(registerRequest);
        return userService.loginService(new LoginRequest(username, password)).authToken();
    }

    @Test
    public void testClearApplication() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = getAuth();

        CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
        CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

        ClearApplicationResult clearResult = clearService.clearApplication();

        Assertions.assertNull(userDAO.getUser(username, password));
        Assertions.assertTrue(gameDAO.getGamesList().isEmpty());
        Assertions.assertNull(authDAO.getAuth(authTokenOutput));
    }

    @Test
    public void testClearApplicationWithNoDataToClear() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = getAuth();

        ClearApplicationResult clearResult = clearService.clearApplication();

        Assertions.assertNull(userDAO.getUser(username, password));
        Assertions.assertTrue(gameDAO.getGamesList().isEmpty());
        Assertions.assertNull(authDAO.getAuth(authTokenOutput));
    }
}
