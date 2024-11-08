package network;

import chess.ChessGame;
import result.*;
import request.*;

public class ServerFacade {
    private final ServerCommunicator comm = new ServerCommunicator();
    private final String urlBase;

    public ServerFacade(int port) {
        this.urlBase = "http://localhost:" + port;
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String urlString = urlBase + "/user";

        return (RegisterResult) comm.doPost(urlString, registerRequest);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String urlString = urlBase + "/session";

        return (LoginResult) comm.doPost(urlString, loginRequest);
    }

    public void logout(String authToken) throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        String urlString = urlBase + "/session";

        comm.doDelete(urlString, logoutRequest);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        String urlString = urlBase + "/game";

        return (CreateGameResult) comm.doPost(urlString, createGameRequest);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        String urlString = urlBase + "/game";

        return comm.doGet(urlString, listGamesRequest);
    }

    public void playGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, teamColor, gameID);
        String urlString = urlBase + "/game";

        comm.doPut(urlString, joinGameRequest);
    }

    public void clearDatabase() throws Exception {
        String urlString = urlBase + "/db";
        comm.doDelete(urlString, null);
    }
}
