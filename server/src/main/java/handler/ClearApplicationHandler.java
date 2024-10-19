package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import result.ClearApplicationResult;
import service.ClearApplicationService;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler extends Handlers {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;

    public ClearApplicationHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            ClearApplicationService clearService = new ClearApplicationService(userDao, authDao, gameDao);
            ClearApplicationResult clearResult = clearService.clearApplication();

            String resultBody = new Gson().toJson(clearResult);
            res.status(200);

            return resultBody;
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }
}
