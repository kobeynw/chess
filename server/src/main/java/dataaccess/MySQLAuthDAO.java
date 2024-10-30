package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO {
    private final DatabaseManager dbManager;

    public MySQLAuthDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public AuthData createAuth(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());

        // if database contains authData throw new DataAccessException("Duplicate Auth Token");

        // otherwise add authData to the database

        return authData;
    }

    public AuthData getAuth(String authToken) {
        // check all authData
        // if an authData entry has the same authToken as the argument authToken, return it

        return null;
    }

    public boolean deleteAuth(AuthData authData) {
        // check all authData
        // if an authData entry is the same as the argument authToken, remove it from the database and return true

        return false;
    }

    public void clearData() {
        // delete all authData entries
    }
}
