package handler;

import com.google.gson.JsonObject;
import spark.*;
import com.google.gson.Gson;

import java.util.Objects;

public abstract class Handlers {
    // TODO: Create methods to serialize and deserialize data (to be used in each sub-class)
    // NOTE: Make sure that the field names used in GSON are exactly the same as those in the Request and Result classes
    // NOTE: Utilize the fact that GSON does not serialize null fields in order to return an error message instead of
    //       the normal result fields

    public JsonObject serialize(Request req, String type) {
        Gson gson = new Gson();

        if (Objects.equals(type, "body")) {
            String body = req.body();
            return gson.fromJson(body, JsonObject.class);
        } else {
            String header = req.headers("authToken");
            return gson.fromJson(header, JsonObject.class);
        }
    }
}
