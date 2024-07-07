package network;

import java.io.*;
import java.net.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final int PORT = 12345; // Fixed port number

    public GameServer() {
        // Constructor with no arguments
    }

    public void start() throws IOException {
        System.out.println("Starting server...");
        serverSocket = new ServerSocket(PORT);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Client connected");
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

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
        System.out.println("Server stopped");
    }
}
