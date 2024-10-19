package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class LoginHandler extends Handlers {
    // Basic Outline:
    // Deserialize JSON request body to Java request object
    // Call service class to perform the requested function, passing it the Java request object
    // Receive Java response object
    // Serialize java response object to JSON
    // Send HTTP response back to client with appropriate status code and response body

    // NOTE: utilize the serialization/deserialization from the Handlers parent class

    public LoginHandler() {}

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject bodyJsonObject = serialize(req, "body");

            String username = bodyJsonObject.get("username").getAsString();
            String password = bodyJsonObject.get("password").getAsString();
            LoginRequest loginRequest = new LoginRequest(username, password);

            UserService userService = new UserService();
            LoginResult loginResult = userService.loginService(loginRequest);

            String resultBody = new Gson().toJson(loginResult);
            res.status(200);

            return resultBody;
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }
}
