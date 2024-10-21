package service;

import dataaccess.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import server.Server;

public class UserServiceTest {
    private static final Server server = new Server();
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
    private final String username = "username";
    private final String password = "password";
    private final String email = "email@example.com";

    @BeforeAll
    public static void startServer() {
        server.run(8080);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterService() throws InfoTakenException, BadRequestException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);

        RegisterResult registerResult = userService.registerService(registerRequest);

        String usernameOutput = registerResult.username();
        String authTokenOutput = registerResult.authToken();

        Assertions.assertEquals(usernameOutput, username);
        Assertions.assertNotNull(authTokenOutput);
    }

    @Test public void testRegisterServiceUserAlreadyExists() throws UnauthorizedException, InfoTakenException,
            BadRequestException, DataAccessException {
        Assertions.assertThrows(InfoTakenException.class, () -> {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);

            RegisterResult registerResult = userService.registerService(registerRequest);
            RegisterResult registerResultDuplicate = userService.registerService(registerRequest);
        });
    }

    @Test
    public void testLoginService() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult = userService.registerService(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = userService.loginService(loginRequest);

        String usernameOutput = loginResult.username();
        String authTokenOutput = loginResult.authToken();

        Assertions.assertEquals(usernameOutput, username);
        Assertions.assertNotNull(authTokenOutput);
    }

    @Test
    public void testLoginServiceWithoutRegister() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            LoginRequest loginRequest = new LoginRequest(username, password);

            LoginResult loginResult = userService.loginService(loginRequest);
        });
    }

    @Test
    public void testLogoutService() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult registerResult = userService.registerService(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = userService.loginService(loginRequest);

        String authTokenOutput = loginResult.authToken();

        Assertions.assertNotNull(memoryAuthDAO.getAuth(authTokenOutput));

        LogoutRequest logoutRequest = new LogoutRequest(authTokenOutput);
        LogoutResult logoutResult = userService.logoutService(logoutRequest);

        Assertions.assertNull(memoryAuthDAO.getAuth(authTokenOutput));
    }

    @Test
    public void testLogoutServiceWithIncorrectAuthToken() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = userService.registerService(registerRequest);

            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = userService.loginService(loginRequest);

            String authTokenOutput = loginResult.authToken();
            String incorrectAuthToken = "RandomCharacters";

            Assertions.assertNotNull(memoryAuthDAO.getAuth(authTokenOutput));

            LogoutRequest logoutRequest = new LogoutRequest(incorrectAuthToken);
            LogoutResult logoutResult = userService.logoutService(logoutRequest);
        });
    }
}
