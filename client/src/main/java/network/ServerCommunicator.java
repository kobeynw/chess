package network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import result.*;
import request.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerCommunicator {
    private HttpURLConnection configurePostConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");

        connection.connect();

        return connection;
    }

    public Object doPost(String urlString, Object request) throws Exception {
        HttpURLConnection connection = configurePostConnection(urlString);

        try(OutputStream requestBody = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(request);
            requestBody.write(jsonBody.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            if (request.getClass() == CreateGameRequest.class) {
                return null;
            }

            try (InputStream responseBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                JsonObject jsonResponse = new Gson().fromJson(inputStreamReader, JsonObject.class);

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
            try (InputStream errorBody = connection.getErrorStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(errorBody);
                JsonObject jsonError = new Gson().fromJson(inputStreamReader, JsonObject.class);

                String errorMsg = jsonError.get("message").getAsString();
                throw new Exception(errorMsg);
            }
        }
    }
}
