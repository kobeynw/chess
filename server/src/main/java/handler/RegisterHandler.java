package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handlers {
    UserDAO userDao;
    AuthDAO authDao;

    public RegisterHandler(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject bodyJsonObject = serialize(req, "body");

            String username = bodyJsonObject.get("username").getAsString();
            String password = bodyJsonObject.get("password").getAsString();
            String email = bodyJsonObject.get("email").getAsString();
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);

            UserService userService = new UserService(userDao, authDao);
            RegisterResult registerResult = userService.registerService(registerRequest);

            String resultBody = new Gson().toJson(registerResult);
            res.status(200);

            return resultBody;
        } catch (BadRequestException e) {
            res.status(400);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (InfoTakenException e) {
            res.status(403);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }
}
