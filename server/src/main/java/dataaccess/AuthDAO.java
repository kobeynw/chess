package dataaccess;

public interface AuthDAO {
    // NOTE: If an auth token is received, it MUST be validated no matter what
    // NOTE: Methods that could fail need to throw a DataAccessException
    // TODO: Add AuthData (registration)
    // TODO: Delete AuthData by auth token (logout)
    // TODO: Find AuthData (logout, list games, create game, join game)
    // TODO: Delete AuthData (clear application) (used to delete all authData in database)
}
