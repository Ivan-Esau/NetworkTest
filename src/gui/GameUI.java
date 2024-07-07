package gui;

import logic.GameLogic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GameUI {
    private GameLogic logic;
    private TicTacToeBoard boardUI;
    private boolean isServer;

    public GameUI(GameLogic logic) {
        this.logic = logic;
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
                    logic.startClient(ip, 12345, playerName);
                    JOptionPane.showMessageDialog(null, "Connected to server!");
                    logic.sendRequest("Request to play a game");
                    String response = logic.receiveResponse();
                    if (response.equals("Accepted")) {
                        JOptionPane.showMessageDialog(null, "Game request accepted. Starting game...");
                        startGame();
                        waitForOpponentMove(); // Wait for the server to make the first move
                    } else {
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
                    logic.startServer(12345);
                    JOptionPane.showMessageDialog(null, "Server started. Waiting for connection...");
                    String request = logic.receiveRequest();
                    int response = JOptionPane.showConfirmDialog(null, request + "\nDo you accept?", "Game Request", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        logic.sendResponse("Accepted");
                        startGame();
                    } else {
                        logic.sendResponse("Declined");
                    }
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

        boardUI = new TicTacToeBoard(logic, isServer);
        gameFrame.add(boardUI);

        gameFrame.setVisible(true);
    }

    // Method to wait for the opponent's move
    private void waitForOpponentMove() {
        new Thread(() -> {
            try {
                String response = logic.receiveResponse();
                String[] move = response.split(",");
                int opponentRow = Integer.parseInt(move[0]);
                int opponentCol = Integer.parseInt(move[1]);
                SwingUtilities.invokeLater(() -> {
                    boardUI.updateBoard(opponentRow, opponentCol);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
