package network;

import chess.ChessGame;
import chess.ChessMove;
import result.*;
import request.*;
import ui.ServerMessageObserver;

public class ServerFacade {
    private final HttpCommunicator httpComm;
    private final WebsocketCommunicator webComm;
    private final String httpUrlBase;
    ServerMessageObserver observer;

    public ServerFacade(String port, ServerMessageObserver observer) {
        this.httpUrlBase = "http://" + port;
        String webUrl = "ws://" + port + "/ws";
        this.observer = observer;
        this.httpComm = new HttpCommunicator();
        this.webComm = new WebsocketCommunicator(observer, webUrl);
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String urlString = httpUrlBase + "/user";

        return (RegisterResult) httpComm.doPost(urlString, registerRequest);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String urlString = httpUrlBase + "/session";

        return (LoginResult) httpComm.doPost(urlString, loginRequest);
    }

    public void logout(String authToken) throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        String urlString = httpUrlBase + "/session";

        httpComm.doDelete(urlString, logoutRequest);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        String urlString = httpUrlBase + "/game";

        return (CreateGameResult) httpComm.doPost(urlString, createGameRequest);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        String urlString = httpUrlBase + "/game";

        return httpComm.doGet(urlString, listGamesRequest);
    }

    public void playGame(String authToken, ChessGame.TeamColor teamColor, int gameID) throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, teamColor, gameID);
        String urlString = httpUrlBase + "/game";

        httpComm.doPut(urlString, joinGameRequest);
    }

    public void clearDatabase() throws Exception {
        String urlString = httpUrlBase + "/db";
        httpComm.doDelete(urlString, null);
    }

    public void connect(String authToken, int gameID) throws Exception {
        webComm.doConnect(authToken, gameID);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        webComm.doMakeMove(authToken, gameID, move);
    }

    public void leave(String authToken, int gameID) throws Exception {
        webComm.doLeave(authToken, gameID);
    }

    public void resign(String authToken, int gameID) throws Exception {
        webComm.doResign(authToken, gameID);
    }
}
