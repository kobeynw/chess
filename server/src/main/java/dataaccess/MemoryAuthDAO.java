package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private Collection<AuthData> authDataStorage = new ArrayList<>();

    public MemoryAuthDAO() {}

    public AuthData createAuth(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());

        if (authDataStorage.contains(authData)) {
            throw new DataAccessException("Duplicate Auth Token");
        }

        authDataStorage.add(authData);

        return authData;
    }

    public AuthData getAuth(String authToken) {
        for (AuthData authData : authDataStorage) {
            if (Objects.equals(authData.authToken(), authToken)) {
                return authData;
            }
        }

        return null;
    }

    public boolean deleteAuth(AuthData authData) {
        for (AuthData currentAuthData : authDataStorage) {
            if (Objects.equals(currentAuthData, authData)) {
                authDataStorage.remove(authData);
                return true;
            }
        }

        return false;
    }

    public void clearData() {
        authDataStorage = new ArrayList<>();
    }
}
