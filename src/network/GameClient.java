package network;

import java.io.*;
import java.net.*;

public class GameClient {
    protected Socket clientSocket;
    protected PrintWriter out;
    protected BufferedReader in;
    private String serverIP;

    // Constructor that only takes the IP address
    public GameClient(String ip) {
        this.serverIP = ip;
    }

    public void startConnection(int port) throws IOException {
        System.out.println("Connecting to server...");
        clientSocket = new Socket(serverIP, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connected to server at " + serverIP);
    }

    public void sendMessage(String message) {
        out.println(message);
        System.out.println("Sent: " + message);
    }

    public String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Received: " + message);
        return message;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        System.out.println("Client disconnected");
    }

    public String getMyIP() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            return "IP-Adresse konnte nicht ermittelt werden.";
        }
    }
}
