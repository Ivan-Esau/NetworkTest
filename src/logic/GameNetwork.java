package logic;

import network.GameServer;
import java.io.IOException;

public class GameNetwork {
    private GameServer server;
    private Player clientPlayer;
    private Player serverPlayer;

    public void startServer() throws IOException {
        server = new GameServer();
        server.start();
        serverPlayer = new Player("ServerPlayer", "localhost");
        serverPlayer.startConnection(12345);
    }

    public void startClient(String ip, String playerName) throws IOException {
        clientPlayer = new Player(playerName, ip);
        clientPlayer.startConnection(12345);
    }

    public void sendRequest(String request) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(request);
        } else if (serverPlayer != null) {
            server.sendMessage(request);
        }
    }

    public String receiveRequest() throws IOException {
        return server.receiveMessage();
    }

    public void sendResponse(String response) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(response);
        } else if (serverPlayer != null) {
            server.sendMessage(response);
        }
    }

    public String receiveResponse() throws IOException {
        if (clientPlayer != null) {
            return clientPlayer.receivePlayerResponse();
        } else {
            return server.receiveMessage();
        }
    }

    public void stopServer() throws IOException {
        server.stop();
        serverPlayer.stopConnection();
    }

    public void stopClient() throws IOException {
        clientPlayer.stopConnection();
    }
}
