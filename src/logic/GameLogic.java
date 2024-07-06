package logic;

import network.GameServer;
import java.io.IOException;

public class GameLogic {
    private GameServer server;
    private Player player;

    public void startServer(int port) throws IOException {
        server = new GameServer();
        server.start(port);
    }

    public void startClient(String ip, int port, String playerName) throws IOException {
        player = new Player(playerName);
        player.startConnection(ip, port);
    }

    public void sendRequest(String request) {
        player.sendPlayerRequest(request);
    }

    public String receiveRequest() throws IOException {
        return server.receiveMessage();
    }

    public void sendResponse(String response) {
        server.sendMessage(response);
    }

    public String receiveResponse() throws IOException {
        return player.receivePlayerResponse();
    }

    public void stopServer() throws IOException {
        server.stop();
    }

    public void stopClient() throws IOException {
        player.stopConnection();
    }
}
