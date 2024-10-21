package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import request.JoinGameRequest;
import result.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class JoinGameHandler extends Handlers {
    GameDAO gameDao;
    AuthDAO authDao;

    public JoinGameHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Object handleRequest(Request req, Response res) {
        try {
            JsonObject headerJsonObject = serialize(req, "headers");
            JsonObject bodyJsonObject = serialize(req, "body");

            if (headerJsonObject.get("authToken") == null || bodyJsonObject.get("playerColor") == null ||
                    bodyJsonObject.get("gameID") == null) {
                throw new BadRequestException("bad request");
            }

            String authToken = headerJsonObject.get("authToken").getAsString();
            String playerColorString = bodyJsonObject.get("playerColor").getAsString();
            String gameIDString = bodyJsonObject.get("gameID").getAsString();

            JoinGameRequest joinGameRequest = getJoinRequest(authToken, playerColorString, gameIDString);

            GameService gameService = new GameService(gameDao, authDao);
            JoinGameResult joinGameResult = gameService.joinGameService(joinGameRequest);

            String resultBody = new Gson().toJson(joinGameResult);
            res.status(200);

            return resultBody;
        } catch (BadRequestException e) {
            res.status(400);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (UnauthorizedException e) {
            res.status(401);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (InfoTakenException e) {
            res.status(403);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        } catch (DataAccessException e) {
            res.status(500);

            return String.format("{ \"message\": \"Error: %s\" }", e.getMessage());
        }
    }

    private JoinGameRequest getJoinRequest(String authToken, String colorString, String gameIDString) {
        int gameID = Integer.parseInt(gameIDString);

        ChessGame.TeamColor playerColor;
        if (Objects.equals(colorString, "BLACK")) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            playerColor = ChessGame.TeamColor.WHITE;
        }

        return new JoinGameRequest(authToken, playerColor, gameID);
    }
}
