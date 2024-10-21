package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;

public class GameService extends Services {
    GameDAO gameDao;
    AuthDAO authDao;

    public GameService(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public CreateGameResult createGameService(CreateGameRequest createGameRequest)
            throws BadRequestException, UnauthorizedException, DataAccessException {
        String gameName = createGameRequest.gameName();
        String authToken = createGameRequest.authToken();

        GameData existingGameData = gameDao.getGame(gameName);
        AuthData authData = authDao.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException("unauthorized");
        } else if (existingGameData != null) {
            throw new BadRequestException("Duplicate Game Name");
        }

        GameData newGameData = gameDao.createNewGame(gameName);

        return new CreateGameResult(newGameData.gameID());
    }

    public JoinGameResult joinGameService(JoinGameRequest joinGameRequest)
            throws BadRequestException, UnauthorizedException, InfoTakenException, DataAccessException {
        String authToken = joinGameRequest.authToken();
        ChessGame.TeamColor playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();

        GameData existingGameData = gameDao.getGame(gameID);
        AuthData authData = authDao.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException("unauthorized");
        } else if (existingGameData == null) {
            throw new BadRequestException("bad request");
        }

        String username = authData.username();

        gameDao.addPlayer(existingGameData, username, playerColor);

        return new JoinGameResult();
    }
}
