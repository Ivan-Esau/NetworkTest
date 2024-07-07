package logic;

import network.GameClient;
import java.io.IOException;

public class Player extends GameClient {
    private String playerName;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public void sendPlayerRequest(String request) {
        sendMessage(request);
    }

    public String receivePlayerResponse() throws IOException {
        return receiveMessage();
    }
}
