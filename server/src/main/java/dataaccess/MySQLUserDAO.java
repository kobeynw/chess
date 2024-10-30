package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class MySQLUserDAO implements UserDAO {
    private final DatabaseManager dbManager;

    public MySQLUserDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public UserData getUser(String username, String password) {
        // check all userData
        // if both userData username and password are equal to the arguments, return that userData

        boolean samePassword = BCrypt.checkpw(password, hashedPassword);

        return null;
    }

    public void createUser(UserData userData) {
        String username = userData.username();
        String clearTextPassword = userData.password();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        String email = userData.email();

        // add argument userData to database
    }

    public void clearData() {
        // delete all userData from database
    }
}
