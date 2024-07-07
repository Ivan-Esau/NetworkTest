package logic;

import network.GameClient;
import java.io.IOException;

public class Player extends GameClient {
    private String playerName;

    public Player(String playerName, String ip) {
        super(ip);
        this.playerName = playerName;
    }

    public void sendPlayerRequest(String request) {
        sendMessage(request);
    }

    public String receivePlayerResponse() throws IOException {
        return receiveMessage();
    }
}
