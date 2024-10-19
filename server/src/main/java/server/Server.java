package server;

import dataaccess.*;
import spark.*;
import handler.*;

public class Server {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;

    public Server() {
        // SPECIFY DATA ACCESS OBJECT TYPE (Memory or Database)
        userDao = new MemoryUserDAO();
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        registerEndpoints();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void registerEndpoints() {
        Spark.post("/user", (req, res) -> (new RegisterHandler(userDao, authDao)).handleRequest(req, res));
        Spark.post("/session", (req, res) -> (new LoginHandler(userDao, authDao)).handleRequest(req, res));
        Spark.delete("/session", (req, res) -> (new LogoutHandler(userDao, authDao)).handleRequest(req, res));
        Spark.delete("/db", (req, res) -> (new ClearApplicationHandler(userDao, authDao, gameDao)).handleRequest(req, res));
    }
}
