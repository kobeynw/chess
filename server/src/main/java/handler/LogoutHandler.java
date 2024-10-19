package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handlers {
    UserDAO userDao;
    AuthDAO authDao;

    public LogoutHandler(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject headerJsonObject = serialize(req, "headers");

            String authToken = headerJsonObject.get("authToken").getAsString();
            LogoutRequest logoutRequest = new LogoutRequest(authToken);

            UserService userService = new UserService(userDao, authDao);
            LogoutResult logoutResult = userService.logoutService(logoutRequest);

            String resultBody = new Gson().toJson(logoutResult);
            res.status(200);

            return resultBody;
        } catch (UnauthorizedException e) {
            res.status(401);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }
}
