package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import result.ClearApplicationResult;

public class ClearApplicationService {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;

    public ClearApplicationService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public ClearApplicationResult clearApplication() throws DataAccessException {
        userDao.clearData();
        authDao.clearData();
        gameDao.clearData();

        return new ClearApplicationResult();
    }

}
