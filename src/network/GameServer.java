package network;

import java.io.*;
import java.net.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;

    // Constructor that sets the port directly with no parameters
    public GameServer() {
        this.port = 12345; // Default port, change if necessary
    }

    public void start() throws IOException {
        System.out.println("Starting server on port " + port + "...");
        serverSocket = new ServerSocket(port);
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
