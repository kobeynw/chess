package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private Collection<UserData> userDataStorage = new ArrayList<>();

    public MemoryUserDAO() {}

    public UserData getUser(String username, String password) {
        for (UserData userData : userDataStorage) {
            if (Objects.equals(userData.username(), username) && Objects.equals(userData.password(), password)) {
                return userData;
            }
        }

        return null;
    }

    public void createUser(UserData userData) {
        userDataStorage.add(userData);
    }

    public void clearData() {
        userDataStorage = new ArrayList<>();
    }
}
