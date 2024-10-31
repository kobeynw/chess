package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import request.*;
import result.*;
import model.GameData;

import java.util.Collection;

public class GameServiceTest {
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
    private final GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);

    private String registerAndLogin() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password",
                "email@example.com");
        RegisterResult registerResult = userService.registerService(registerRequest);
        LoginResult loginResult = userService.loginService(new LoginRequest("username", "password"));

        return loginResult.authToken();
    }

    @Test
    public void testCreateGameService() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = registerAndLogin();

        CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
        CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

        Assertions.assertFalse(memoryGameDAO.getGamesList().isEmpty());
        Assertions.assertEquals(createGameResult.gameID(), 1);
    }

    @Test
    public void testCreateGameServiceWithDuplicateGameName() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            String authTokenOutput = registerAndLogin();

            CreateGameRequest createGameRequest1 = new CreateGameRequest(authTokenOutput, "My Game");
            CreateGameResult createGameResult1 = gameService.createGameService(createGameRequest1);

            CreateGameRequest createGameRequest2 = new CreateGameRequest(authTokenOutput, "My Game");
            CreateGameResult createGameResult2 = gameService.createGameService(createGameRequest2);
        });
    }

    @Test
    public void testJoinGameService() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = registerAndLogin();

        CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
        CreateGameResult createGameResult = gameService.createGameService(createGameRequest);
        int gameID = createGameResult.gameID();

        JoinGameRequest joinGameRequest = new JoinGameRequest(authTokenOutput, ChessGame.TeamColor.WHITE, gameID);
        JoinGameResult joinGameResult = gameService.joinGameService(joinGameRequest);

        String newPlayerUsername = memoryGameDAO.getGame(gameID).whiteUsername();

        Assertions.assertEquals(newPlayerUsername, "username");
    }

    @Test
    public void testJoinGameServiceWithNoMatchingGameInfo() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            String authTokenOutput = registerAndLogin();

            CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
            CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

            JoinGameRequest joinGameRequest = new JoinGameRequest(authTokenOutput, ChessGame.TeamColor.WHITE, 1234);
            JoinGameResult joinGameResult = gameService.joinGameService(joinGameRequest);
        });
    }

    @Test
    public void testListGamesService() throws BadRequestException, DataAccessException, UnauthorizedException,
            InfoTakenException {
        String authTokenOutput = registerAndLogin();

        CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
        CreateGameResult createGameResult = gameService.createGameService(createGameRequest);
        int gameID = createGameResult.gameID();

        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenOutput);
        ListGamesResult listGamesResult = gameService.listGamesService(listGamesRequest);

        Collection<GameData> gamesListOutput = listGamesResult.games();

        Assertions.assertEquals(gamesListOutput.size(), 1);
        Assertions.assertEquals(gamesListOutput.iterator().next().gameID(), gameID);
    }

    @Test
    public void testListGamesServiceWithInvalidAuthToken() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            String authTokenOutput = registerAndLogin();

            CreateGameRequest createGameRequest = new CreateGameRequest(authTokenOutput, "My Game");
            CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

            ListGamesRequest listGamesRequest = new ListGamesRequest("RandomCharacters");
            ListGamesResult listGamesResult = gameService.listGamesService(listGamesRequest);
        });
    }
}
