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
import java.util.Objects;

public class ServerCommunicator {
//    public void doGet(String urlString) throws IOException {
//        URL url = new URL(urlString);
//
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        connection.setReadTimeout(5000);
//        connection.setRequestMethod("GET");
//
//        // Set HTTP request headers, if necessary
//        // connection.addRequestProperty("Accept", "text/html");
//        // connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");
//
//        connection.connect();
//
//        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//            // Get HTTP response headers, if necessary
//            // Map<String, List<String>> headers = connection.getHeaderFields();
//
//            // OR
//
//            //connection.getHeaderField("Content-Length");
//
//            InputStream responseBody = connection.getInputStream();
//            // Read and process response body from InputStream ...
//        } else {
//            // SERVER RETURNED AN HTTP ERROR
//
//            InputStream responseBody = connection.getErrorStream();
//            // Read and process error response body from InputStream ...
//        }
//    }

    private HttpURLConnection configureConnection(String urlString, String method, String auth) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod(method);
        connection.addRequestProperty("Content-Type", "application/json");

        if (Objects.equals(method, "POST") || Objects.equals(method, "DELETE")) {
            connection.setDoOutput(true);
        }
        if (auth != null) {
            connection.addRequestProperty("authorization", auth);
        }

        connection.connect();

        return connection;
    }

    public void doDelete(String urlString, LogoutRequest logoutRequest) throws Exception {
        HttpURLConnection connection = configureConnection(urlString, "DELETE", logoutRequest.authToken());

        try(OutputStream requestBody = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(logoutRequest);
            requestBody.write(jsonBody.getBytes());
        }

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            try (InputStream errorBody = connection.getErrorStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(errorBody);
                JsonObject jsonError = new Gson().fromJson(inputStreamReader, JsonObject.class);

                String errorMsg = jsonError.get("message").getAsString();
                throw new Exception(errorMsg);
            }
        }
    }

    public Object doPost(String urlString, Object request) throws Exception {
        HttpURLConnection connection = configureConnection(urlString, "POST", null);

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
