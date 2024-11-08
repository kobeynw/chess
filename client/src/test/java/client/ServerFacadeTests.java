package client;

import chess.ChessGame;
import model.GameData;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;
import result.*;

import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;
    private final String username = "user";
    private final String password = "pass";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    private String getAuth() throws Exception {
        LoginResult result = serverFacade.login(username, password);
        return result.authToken();
    }

    @Test
    @Order(1)
    public void registerTest() throws Exception {
        RegisterResult registerResult = serverFacade.register(username, password, "email");
        String authToken = registerResult.authToken();

        Assertions.assertNotNull(authToken);
        Assertions.assertInstanceOf(String.class, authToken);
    }

    @Test
    @Order(2)
    public void registerTestWithDuplicateUsername() {
        Assertions.assertThrows(Exception.class, () -> {
            RegisterResult registerResult = serverFacade.register(username, password, "email");
        });
    }

    @Test
    @Order(3)
    public void loginTest() throws Exception {
        LoginResult loginResult = serverFacade.login(username, password);
        String username = loginResult.username();
        String authToken = loginResult.authToken();

        Assertions.assertNotNull(username);
        Assertions.assertNotNull(authToken);
        Assertions.assertInstanceOf(String.class, authToken);
        Assertions.assertEquals(username, "user");
    }

    @Test
    @Order(4)
    public void loginTestWithUserNotRegistered() {
        Assertions.assertThrows(Exception.class, () -> {
            LoginResult loginResult = serverFacade.login("user1", "pass1");
        });
    }

    @Test
    @Order(5)
    public void createGameTest() throws Exception {
        CreateGameResult createGameResult = serverFacade.createGame(getAuth(), "game1");
        int gameID = createGameResult.gameID();

        Assertions.assertEquals(gameID, 1);
    }

    @Test
    @Order(6)
    public void createGameTestDuplicateGameName() {
        Assertions.assertThrows(Exception.class, () -> {
            CreateGameResult createGameResult = serverFacade.createGame(getAuth(), "game1");
        });
    }

    @Test
    @Order(7)
    public void listGamesTest() throws Exception {
        ListGamesResult listGamesResult = serverFacade.listGames(getAuth());
        Collection<GameData> games = listGamesResult.games();

        for (GameData game : games) {
            Assertions.assertEquals(game.gameID(), 1);
            Assertions.assertEquals(game.gameName(), "game1");
            Assertions.assertNull(game.blackUsername());
            Assertions.assertNull(game.whiteUsername());
        }
    }

    @Test
    @Order(8)
    public void listGamesTestInvalidAuthToken() {
        Assertions.assertThrows(Exception.class, () -> {
            ListGamesResult listGamesResult = serverFacade.listGames("randomString");
        });
    }

    @Test
    @Order(7)
    public void playGameTest() throws Exception {
        serverFacade.playGame(getAuth(), ChessGame.TeamColor.BLACK, 1);

        ListGamesResult listGamesResult = serverFacade.listGames(getAuth());
        Collection<GameData> games = listGamesResult.games();

        for (GameData game : games) {
            Assertions.assertEquals(game.gameID(), 1);
            Assertions.assertEquals(game.gameName(), "game1");
            Assertions.assertEquals(game.blackUsername(), username);
            Assertions.assertNull(game.whiteUsername());
        }
    }

    @Test
    @Order(8)
    public void playGameTestWithInvalidGameID() {
        Assertions.assertThrows(Exception.class, () -> {
            serverFacade.playGame(getAuth(), ChessGame.TeamColor.BLACK, 100);
        });
    }

    @Test
    @Order(9)
    public void logoutTest() throws Exception {
        serverFacade.logout(getAuth());
    }

    @Test
    @Order(10)
    public void logoutTestDuplicateLogout() {
        Assertions.assertThrows(Exception.class, () -> {
            String authToken = getAuth();
            serverFacade.logout(authToken);
            serverFacade.logout(authToken);
        });
    }
}
