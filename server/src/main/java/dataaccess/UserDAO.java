package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData getUser(String username, String password);

    void createUser(UserData userData);

    void clearData();
}
