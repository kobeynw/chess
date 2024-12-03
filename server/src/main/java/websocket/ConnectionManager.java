package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
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

    public void broadcast(String usernameToExclude, String message) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (usernameToExclude != null && !c.username.equals(usernameToExclude)) {
                    c.send(message);
                }
            }
        }
    }
}
