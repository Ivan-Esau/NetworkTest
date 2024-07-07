package logic;

import network.GameServer;
import network.GameClient;
import java.io.IOException;

public class GameNetwork {
    private GameServer server;
    private GameClient client;
    private Player clientPlayer;
    private Player serverPlayer;

    public GameNetwork() {}

    public void startServer() throws IOException {
        if (server != null) {
            stopServer();
        }
        server = new GameServer();
        server.start();
        connectLocalClient();
    }

    public void startClient(String ip, String playerName) throws IOException {
        if (server != null) {
            stopServer();
        }
        clientPlayer = new Player(playerName, ip);
        clientPlayer.startConnection(12345);
    }

    private void connectLocalClient() throws IOException {
        client = new GameClient("localhost");
        client.startConnection(12345);
    }

    public void sendRequest(String request) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(request);
        } else if (client != null) {
            client.sendMessage(request);
        }
    }

    public String receiveRequest() throws IOException {
        return server.receiveMessage();
    }

    public void sendResponse(String response) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(response);
        } else if (client != null) {
            client.sendMessage(response);
        }
    }

    public String receiveResponse() throws IOException {
        if (clientPlayer != null) {
            return clientPlayer.receivePlayerResponse();
        } else if (client != null) {
            return client.receiveMessage();
        } else {
            return server.receiveMessage();
        }
    }

    public void stopServer() throws IOException {
        if (server != null) {
            server.stop();
            server = null;
        }
        if (client != null) {
            client.stopConnection();
            client = null;
        }
    }

    public void stopClient() throws IOException {
        if (clientPlayer != null) {
            clientPlayer.stopConnection();
            clientPlayer = null;
        }
    }
}
