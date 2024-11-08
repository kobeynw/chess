package network;

import request.*;
import result.*;

public class ServerFacade {
    private final ServerCommunicator comm = new ServerCommunicator();
    private final String urlBase = "http://localhost:8080";

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String urlString = urlBase + "/user";

        return (RegisterResult) comm.doPost(urlString, registerRequest);
    }
}
