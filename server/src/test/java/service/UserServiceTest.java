package service;

import dataaccess.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

public class UserServiceTest {
    private static final Server server = new Server();

    @BeforeAll
    public static void startServer() {
        server.run(8080);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterService()
            throws UnauthorizedException, InfoTakenException, BadRequestException, DataAccessException {
        String usernameExpected = "username";
        String password = "password";
        String email = "email@example.com";

        LoginRequest loginRequest = new LoginRequest(usernameExpected, password);
        RegisterRequest registerRequest = new RegisterRequest(usernameExpected, password, email);
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        RegisterResult registerResult = userService.registerService(registerRequest);
        LoginResult loginResult = userService.loginService(loginRequest);

        String usernameOutput = registerResult.username();
        String authTokenOutput = registerResult.authToken();

        Assertions.assertEquals(usernameOutput, usernameExpected);
        Assertions.assertNotNull(authTokenOutput);
    }

    @Test
    public void testLoginServiceWithoutRegister() {
        String usernameExpected = "username";
        String password = "password";

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            LoginRequest loginRequest = new LoginRequest(usernameExpected, password);
            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

            LoginResult loginResult = userService.loginService(loginRequest);
        });
    }
}
