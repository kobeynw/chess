package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.JoinGameResult;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class ListGamesHandler extends Handlers {
    GameDAO gameDao;
    AuthDAO authDao;

    public ListGamesHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject headerJsonObject = serialize(req, "headers");

            String authToken = headerJsonObject.get("authToken").getAsString();

            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

            GameService gameService = new GameService(gameDao, authDao);
            ListGamesResult listGamesResult = gameService.listGamesService(listGamesRequest);

            String resultBody = new Gson().toJson(listGamesResult);
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
