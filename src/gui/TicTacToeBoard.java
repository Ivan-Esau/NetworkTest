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
    private boolean isServer;

    public TicTacToeBoard(GameLogic logic, boolean isServer) {
        this.logic = logic;
        this.isServer = isServer;
        setLayout(new GridLayout(3, 3));
        initializeButtons();
        if (!isServer) {
            disableBoard();
            new Thread(() -> waitForOpponentMove()).start(); // Start waiting for the server's first move
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
                        if (logic.isCurrentPlayerX() == isServer) { // Ensure correct player's turn
                            if (logic.placeMark(row, col)) {
                                buttons[row][col].setText(String.valueOf(logic.getCurrentPlayer()));
                                if (logic.checkForWin()) {
                                    System.out.println("Player " + logic.getCurrentPlayer() + " wins!");
                                    JOptionPane.showMessageDialog(null, "Player " + logic.getCurrentPlayer() + " wins!");
                                    resetBoard();
                                } else if (logic.isBoardFull()) {
                                    System.out.println("The game is a tie!");
                                    JOptionPane.showMessageDialog(null, "The game is a tie!");
                                    resetBoard();
                                } else {
                                    logic.changePlayer();
                                    try {
                                        logic.sendRequest(row + "," + col);
                                        System.out.println("Sent move: " + row + "," + col);
                                        disableBoard();
                                        new Thread(() -> waitForOpponentMove()).start(); // Wait for the opponent's move in a new thread
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                }
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
    private void waitForOpponentMove() {
        try {
            String response = logic.receiveResponse();
            System.out.println("Received move: " + response);
            if (response.equals("START_GAME")) {
                System.out.println("Game started on client side");
                SwingUtilities.invokeLater(() -> enableBoard()); // Enable board for client after game starts
            } else {
                String[] move = response.split(",");
                int opponentRow = Integer.parseInt(move[0]);
                int opponentCol = Integer.parseInt(move[1]);
                System.out.println("Opponent moved to (" + opponentRow + ", " + opponentCol + ")");
                SwingUtilities.invokeLater(() -> {
                    updateBoard(opponentRow, opponentCol);
                    enableBoard();
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the board with the opponent's move
    public void updateBoard(int row, int col) {
        logic.handleOpponentMove(row, col);
        buttons[row][col].setText(String.valueOf(logic.getCurrentPlayer()));
        if (logic.checkForWin()) {
            System.out.println("Player " + logic.getCurrentPlayer() + " wins!");
            JOptionPane.showMessageDialog(null, "Player " + logic.getCurrentPlayer() + " wins!");
            resetBoard();
        } else if (logic.isBoardFull()) {
            System.out.println("The game is a tie!");
            JOptionPane.showMessageDialog(null, "The game is a tie!");
            resetBoard();
        } else {
            logic.changePlayer();
        }
    }
}
