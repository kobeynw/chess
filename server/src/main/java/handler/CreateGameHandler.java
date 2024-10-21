package handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handlers {
    GameDAO gameDao;
    AuthDAO authDao;

    public CreateGameHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject headerJsonObject = serialize(req, "headers");
            JsonObject bodyJsonObject = serialize(req, "body");

            if (headerJsonObject.get("authToken") == null || bodyJsonObject.get("gameName") == null) {
                throw new BadRequestException("bad request");
            }

            String authToken = headerJsonObject.get("authToken").getAsString();
            String gameName = bodyJsonObject.get("gameName").getAsString();

            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);

            GameService gameService = new GameService(gameDao, authDao);
            CreateGameResult createGameResult = gameService.createGameService(createGameRequest);

            String resultBody = new Gson().toJson(createGameResult);
            res.status(200);

            return resultBody;
        } catch (BadRequestException e) {
            res.status(400);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (UnauthorizedException e) {
            res.status(401);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }
}
