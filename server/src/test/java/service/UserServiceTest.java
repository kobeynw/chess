package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import request.LoginRequest;
import result.LoginResult;
import server.Server;

public class UserServiceTest {
    private static final Server server = new Server();

    @BeforeAll
    public static void startServer() {
        server.run(8080);
    }

    @Test
    public void testLoginService() {
        String usernameExpected = "username";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest(usernameExpected, password);
        UserService userService = new UserService();

        try {
            LoginResult loginResult = userService.loginService(loginRequest);

            String usernameOutput = loginResult.username();
            String authTokenOutput = loginResult.authToken();

            Assertions.assertEquals(usernameOutput, usernameExpected);
            Assertions.assertNotNull(authTokenOutput);
        } catch (DataAccessException e) {
            System.out.printf("{ \"message\": \"Error: %s\" }%n", e.getMessage());
        }
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }
}
