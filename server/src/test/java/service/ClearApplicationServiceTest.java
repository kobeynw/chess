package service;

import dataaccess.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import request.*;
import result.*;
import server.Server;

public class ClearApplicationServiceTest {
    private static final Server server = new Server();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final ClearApplicationService clearService = new ClearApplicationService(userDAO, authDAO, gameDAO);

    @BeforeAll
    public static void startServer() {
        server.run(8080);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    private String registerAndLogin() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String username = "username";
        String password = "password";
        String email = "email@example.com";

        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult = userService.registerService(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = userService.loginService(loginRequest);

        return loginResult.authToken();
    }

    @Test
    public void testClearApplication() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = registerAndLogin();

        CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
        CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

        ClearApplicationResult clearResult = clearService.clearApplication();

        Assertions.assertNull(userDAO.getUser("username", "password"));
        Assertions.assertTrue(gameDAO.getGamesList().isEmpty());
        Assertions.assertNull(authDAO.getAuth(authTokenOutput));
    }
}
