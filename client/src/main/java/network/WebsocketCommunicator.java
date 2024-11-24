package network;

import ui.ServerMessageObserver;

public class WebsocketCommunicator {
    ServerMessageObserver observer;

    public WebsocketCommunicator(ServerMessageObserver observer) {
        this.observer = observer;
    }
}
