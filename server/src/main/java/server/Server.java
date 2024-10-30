package server;

import dataaccess.*;
import spark.*;
import handler.*;

public class Server {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;
    DatabaseManager dbManager = new DatabaseManager();  // SPECIFIC TO MYSQL IMPLEMENTATION

    public Server() {
        try {
            // SPECIFY DATA ACCESS OBJECT TYPE (Memory or Database)
            dbManager.configureDatabase();
            userDao = new MySQLUserDAO(dbManager);
            authDao = new MySQLAuthDAO(dbManager);
            gameDao = new MySQLGameDAO(dbManager);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        registerEndpoints();

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

        Spark.post("/game", (req, res) -> (new CreateGameHandler(gameDao, authDao)).handleRequest(req, res));
        Spark.put("/game", (req, res) -> (new JoinGameHandler(gameDao, authDao)).handleRequest(req, res));
        Spark.get("/game", (req, res) -> (new ListGamesHandler(gameDao, authDao)).handleRequest(req, res));

        Spark.delete("/db", (req, res) -> (new ClearApplicationHandler(userDao, authDao, gameDao)).handleRequest(req, res));
    }
}
