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
    public void testRegisterService() {
        String usernameExpected = "username";
        String password = "password";
        String email = "email@example.com";

        LoginRequest loginRequest = new LoginRequest(usernameExpected, password);
        RegisterRequest registerRequest = new RegisterRequest(usernameExpected, password, email);
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        try {
            RegisterResult registerResult = userService.registerService(registerRequest);
            LoginResult loginResult = userService.loginService(loginRequest);

            String usernameOutput = registerResult.username();
            String authTokenOutput = registerResult.authToken();

            Assertions.assertEquals(usernameOutput, usernameExpected);
            Assertions.assertNotNull(authTokenOutput);
        } catch (UnauthorizedException | InfoTakenException | BadRequestException | DataAccessException e) {
            System.out.printf("{ \"message\": \"Error: %s\" }%n", e.getMessage());
        }
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
