package dataaccess;

import model.UserData;

public class MySQLUserDAO {
    public MySQLUserDAO() {}

    public UserData getUser(String username, String password) {
        // check all userData
        // if both userData username and password are equal to the arguments, return that userData

        return null;
    }

    public void createUser(UserData userData) {
        // add argument userData to database
    }

    public void clearData() {
        // delete all userData from database
    }
}
