package gui;

import logic.GameboardLogic;
import logic.GameNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GameUI {
    private GameboardLogic boardLogic;
    private GameNetwork network;
    private TicTacToeBoard boardUI;
    private boolean isServer;

    public GameUI(GameboardLogic boardLogic, GameNetwork network) {
        this.boardLogic = boardLogic;
        this.network = network;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Network Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel ipLabel = new JLabel("IP Address:");
        ipLabel.setBounds(10, 20, 80, 25);
        panel.add(ipLabel);

        JTextField ipText = new JTextField(20);
        ipText.setBounds(100, 20, 165, 25);
        panel.add(ipText);

        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 50, 165, 25);
        panel.add(nameText);

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(10, 80, 150, 25);
        panel.add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ip = ipText.getText();
                String playerName = nameText.getText();
                try {
                    network.startClient(ip, playerName);
                    System.out.println("Client connected to server at " + ip);
                    JOptionPane.showMessageDialog(null, "Connected to server!");
                    network.sendRequest("Request to play a game");
                    String response = network.receiveResponse();
                    if (response.equals("Accepted")) {
                        System.out.println("Game request accepted by server");
                        JOptionPane.showMessageDialog(null, "Game request accepted. Starting game...");
                        startGame();
                    } else {
                        System.out.println("Game request declined by server");
                        JOptionPane.showMessageDialog(null, "Game request declined.");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        JButton startServerButton = new JButton("Start Server");
        startServerButton.setBounds(10, 110, 150, 25);
        panel.add(startServerButton);

        startServerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    isServer = true;
                    network.startServer();
                    System.out.println("Server started on port 12345");
                    JOptionPane.showMessageDialog(null, "Server started. Waiting for connection...");
                    new Thread(() -> {
                        try {
                            String request = network.receiveRequest();
                            System.out.println("Received game request from client: " + request);
                            int response = JOptionPane.showConfirmDialog(null, request + "\nDo you accept?", "Game Request", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                network.sendResponse("Accepted");
                                System.out.println("Accepted game request from client");
                                SwingUtilities.invokeLater(() -> startGame());
                                network.sendResponse("START_GAME");
                            } else {
                                network.sendResponse("Declined");
                                System.out.println("Declined game request from client");
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }).start();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("Tic Tac Toe");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(300, 300);

        boardUI = new TicTacToeBoard(boardLogic, network, isServer, this); // Pass the GameUI instance to TicTacToeBoard
        gameFrame.add(boardUI);

        gameFrame.setVisible(true);
    }

    // Method to return to the main menu
    public void returnToMenu() {
        createAndShowGUI();
    }
}
