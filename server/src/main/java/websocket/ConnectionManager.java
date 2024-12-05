package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, int gameID) {
        var connection = new Connection(username, session, gameID);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void sendToRootClient(String rootUsername, String message) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.username.equals(rootUsername)) {
                    c.send(message);
                }
            }
        }
    }

    private int getRootGameID(String rootUsername) {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (rootUsername != null) {
                    if (c.username.equals(rootUsername)) {
                        return c.gameID;
                    }
                }
            }
        }

        return 0;
    }

    public void broadcast(String rootUsername, String message, boolean toRoot) throws IOException {
        int rootGameID = getRootGameID(rootUsername);

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID == rootGameID) {
                    if (toRoot) {
                        c.send(message);
                    } else if (!c.username.equals(rootUsername)) {
                        c.send(message);
                    }
                }
            }
        }
    }
}
