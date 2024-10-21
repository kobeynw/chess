package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;

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
}
