package network;

import request.*;
import result.*;

public class ServerFacade {
    private final ServerCommunicator comm = new ServerCommunicator();
    private final String urlBase = "http://localhost:8080";

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

//    public JoinGameResult playGame(String authToken, ChessGame.TeamColor teamColor, int gameID) {
//        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, teamColor, gameID);
//    }
//
//    public JoinGameResult observeGame(String authToken, int gameID) {
//        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE, gameID);
//    }
}
