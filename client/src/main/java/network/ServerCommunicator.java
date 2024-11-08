package network;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.GameData;
import result.*;
import request.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ServerCommunicator {
    private HttpURLConnection configureConnection(String urlString, String method, String auth) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod(method);
        connection.addRequestProperty("Content-Type", "application/json");

        if (!Objects.equals(method, "GET")) {
            connection.setDoOutput(true);
        }
        if (auth != null) {
            connection.addRequestProperty("authorization", auth);
        }

        connection.connect();

        return connection;
    }

    private void handleErrorMsg(HttpURLConnection connection) throws Exception {
        try (InputStream errorBody = connection.getErrorStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(errorBody);
            JsonObject jsonError = new Gson().fromJson(inputStreamReader, JsonObject.class);

            String errorMsg = jsonError.get("message").getAsString();
            throw new Exception(errorMsg);
        }
    }

    public void doPut(String urlString, JoinGameRequest joinGameRequest) throws Exception {
        HttpURLConnection connection = configureConnection(urlString, "PUT", joinGameRequest.authToken());

        try(OutputStream requestBody = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(joinGameRequest);
            requestBody.write(jsonBody.getBytes());
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            handleErrorMsg(connection);
        }
    }

    public ListGamesResult doGet(String urlString, ListGamesRequest listGamesRequest) throws Exception {
        HttpURLConnection connection = configureConnection(urlString, "GET", listGamesRequest.authToken());

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                JsonObject jsonResponse = new Gson().fromJson(inputStreamReader, JsonObject.class);

                JsonArray jsonArray = jsonResponse.get("games").getAsJsonArray();
                Collection<GameData> games = new ArrayList<>();

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject gameObj = jsonArray.get(i).getAsJsonObject();
                    int gameID = gameObj.get("gameID").getAsInt();
                    String gameName = gameObj.get("gameName").getAsString();
                    String white = null;
                    String black = null;

                    if (gameObj.get("whiteUsername") != null) {
                        white = gameObj.get("whiteUsername").getAsString();
                    }
                    if (gameObj.get("blackUsername") != null) {
                        black = gameObj.get("blackUsername").getAsString();
                    }

                    games.add(new GameData(gameID, white, black, gameName, null));
                }

                return new ListGamesResult(games);
            }
        } else {
            handleErrorMsg(connection);
            return null;
        }
    }

    public void doDelete(String urlString, LogoutRequest logoutRequest) throws Exception {
        HttpURLConnection connection;
        if (logoutRequest == null) {
            connection = configureConnection(urlString, "DELETE", null);
        } else {
            connection = configureConnection(urlString, "DELETE", logoutRequest.authToken());
            try(OutputStream requestBody = connection.getOutputStream()) {
                var jsonBody = new Gson().toJson(logoutRequest);
                requestBody.write(jsonBody.getBytes());
            }
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            handleErrorMsg(connection);
        }
    }

    public Object doPost(String urlString, Object request) throws Exception {
        HttpURLConnection connection;
        if (request.getClass() == CreateGameRequest.class) {
            String authToken = ((CreateGameRequest) request).authToken();
            connection = configureConnection(urlString, "POST", authToken);
        } else {
            connection = configureConnection(urlString, "POST", null);
        }

        try(OutputStream requestBody = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(request);
            requestBody.write(jsonBody.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                JsonObject jsonResponse = new Gson().fromJson(inputStreamReader, JsonObject.class);

                if (request.getClass() == CreateGameRequest.class) {
                    int gameID = jsonResponse.get("gameID").getAsInt();
                    return new CreateGameResult(gameID);
                }

                String username = jsonResponse.get("username").getAsString();
                String authToken = jsonResponse.get("authToken").getAsString();

                if (request.getClass() == RegisterRequest.class) {
                    return new RegisterResult(username, authToken);
                } else if (request.getClass() == LoginRequest.class) {
                    return new LoginResult(username, authToken);
                } else {
                    return null;
                }
            }
        } else {
            handleErrorMsg(connection);
            return null;
        }
    }
}
