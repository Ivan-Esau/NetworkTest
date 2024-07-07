package gui;

import logic.GameBoard;
import logic.GameNetwork;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Initialisiere die GameBoard und GameNetwork Objekte
        GameBoard gameBoard = new GameBoard();
        GameNetwork gameNetwork = new GameNetwork();

        // Erstelle die GameUI und zeige die GUI an
        GameUI gameUI = new GameUI(gameBoard, gameNetwork);
        gameUI.createAndShowGUI();

        // Starten Sie den Server für beide Spieler direkt beim Start und verbinden den localhost-Client
        try {
            gameNetwork.startServer();
            System.out.println("Server started on port 12345");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler beim Starten des Servers: " + e.getMessage());
        }
    }
}
