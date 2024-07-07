package gui;

import logic.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TicTacToeBoard extends JPanel {
    private GameLogic logic;
    private JButton[][] buttons;
    private boolean isMyTurn;
    private char mySymbol;
    private char opponentSymbol;
    private GameUI parentUI; // Reference to the parent UI

    public TicTacToeBoard(GameLogic logic, boolean isServer, GameUI parentUI) {
        this.logic = logic;
        this.isMyTurn = isServer; // Initialize the turn based on whether this is the server or client
        this.mySymbol = isServer ? 'X' : 'O'; // Server is always 'X', Client is always 'O'
        this.opponentSymbol = isServer ? 'O' : 'X'; // Opponent symbol is opposite
        this.parentUI = parentUI; // Initialize the parent UI
        setLayout(new GridLayout(3, 3));
        initializeButtons();
        if (!isMyTurn) {
            disableBoard();
            new Thread(createWaitForOpponentMoveRunnable()).start(); // Start waiting for the opponent's first move
        } else {
            enableBoard(); // Enable the board for the player to start the game
        }
    }

    private void initializeButtons() {
        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("-");
                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isMyTurn && logic.placeMark(row, col)) { // Ensure correct player's turn and valid move
                            buttons[row][col].setText(String.valueOf(mySymbol));
                            try {
                                logic.sendRequest(row + "," + col + "," + mySymbol);
                                System.out.println("Sent move: " + row + "," + col + "," + mySymbol);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            if (logic.checkForWin()) {
                                System.out.println("Player " + mySymbol + " wins!");
                                JOptionPane.showMessageDialog(null, "Player " + mySymbol + " wins!");
                                resetBoard();
                            } else if (logic.isBoardFull()) {
                                System.out.println("The game is a tie!");
                                JOptionPane.showMessageDialog(null, "The game is a tie!");
                                resetBoard();
                            } else {
                                logic.changePlayer();
                                disableBoard();
                                isMyTurn = false; // Change turn after making a move
                                new Thread(createWaitForOpponentMoveRunnable()).start(); // Wait for the opponent's move in a new thread
                            }
                        }
                    }
                });
                add(buttons[i][j]);
            }
        }
    }

    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void enableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(true);
            }
        }
    }

    private void resetBoard() {
        logic.initializeBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("-");
            }
        }
    }

    // Method to wait for the opponent's move
    private Runnable createWaitForOpponentMoveRunnable() {
        return () -> {
            try {
                while (true) {
                    String response = logic.receiveResponse();
                    if (response == null) {
                        System.out.println("Connection lost or server closed. Returning to menu...");
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Connection lost. Returning to menu.");
                            parentUI.returnToMenu(); // Return to menu using parent UI
                        });
                        return;
                    }
                    System.out.println("Received move: " + response);
                    if (response.equals("START_GAME")) {
                        SwingUtilities.invokeLater(this::enableBoard);
                    } else {
                        String[] move = response.split(",");
                        int opponentRow = Integer.parseInt(move[0]);
                        int opponentCol = Integer.parseInt(move[1]);
                        char opponentSymbol = move[2].charAt(0);
                        System.out.println("Opponent moved to (" + opponentRow + ", " + opponentCol + ") with symbol " + opponentSymbol);
                        SwingUtilities.invokeLater(() -> {
                            updateBoard(opponentRow, opponentCol, opponentSymbol);
                            if (logic.checkForWin()) {
                                System.out.println("Player " + opponentSymbol + " wins!");
                                JOptionPane.showMessageDialog(null, "Player " + opponentSymbol + " wins!");
                                resetBoard();
                            } else if (logic.isBoardFull()) {
                                System.out.println("The game is a tie!");
                                JOptionPane.showMessageDialog(null, "The game is a tie!");
                                resetBoard();
                            } else {
                                logic.changePlayer();
                                isMyTurn = true; // Change turn after receiving opponent's move
                                enableBoard(); // Enable the board for the player's turn
                            }
                        });
                        break; // Exit loop after handling the opponent's move
                    }
                }
            } catch (IOException e) {
                System.out.println("Error receiving move: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Connection error. Returning to menu.");
                    parentUI.returnToMenu(); // Return to menu using parent UI
                });
            }
        };
    }

    // Method to update the board with the opponent's move
    public void updateBoard(int row, int col, char symbol) {
        logic.handleOpponentMove(row, col, symbol);
        buttons[row][col].setText(String.valueOf(symbol));
    }
}
