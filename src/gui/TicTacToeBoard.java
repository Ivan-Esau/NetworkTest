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
                        if (logic.placeMark(row, col)) {
                            buttons[row][col].setText(String.valueOf(logic.getCurrentPlayer()));
                            if (logic.checkForWin()) {
                                JOptionPane.showMessageDialog(null, "Player " + logic.getCurrentPlayer() + " wins!");
                                resetBoard();
                            } else if (logic.isBoardFull()) {
                                JOptionPane.showMessageDialog(null, "The game is a tie!");
                                resetBoard();
                            } else {
                                logic.changePlayer();
                                try {
                                    logic.sendRequest(row + "," + col);
                                    disableBoard();
                                    String response = logic.receiveResponse();
                                    enableBoard();
                                    String[] move = response.split(",");
                                    int opponentRow = Integer.parseInt(move[0]);
                                    int opponentCol = Integer.parseInt(move[1]);
                                    logic.placeMark(opponentRow, opponentCol);
                                    buttons[opponentRow][opponentCol].setText(String.valueOf(logic.getCurrentPlayer()));
                                    if (logic.checkForWin()) {
                                        JOptionPane.showMessageDialog(null, "Player " + logic.getCurrentPlayer() + " wins!");
                                        resetBoard();
                                    } else if (logic.isBoardFull()) {
                                        JOptionPane.showMessageDialog(null, "The game is a tie!");
                                        resetBoard();
                                    } else {
                                        logic.changePlayer();
                                    }
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
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
}
