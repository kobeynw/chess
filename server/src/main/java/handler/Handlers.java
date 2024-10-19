package handler;

import com.google.gson.JsonObject;
import spark.*;
import com.google.gson.Gson;

import java.util.Objects;

public abstract class Handlers {
    public JsonObject serialize(Request req, String type) {
        Gson gson = new Gson();

        if (Objects.equals(type, "body")) {
            String body = req.body();
            return gson.fromJson(body, JsonObject.class);
        } else {
            String header = req.headers("authorization");
            return gson.fromJson(String.format("{authToken: %s}", header), JsonObject.class);
        }
    }
}
