package logic;

import network.GameServer;
import java.io.IOException;

public class GameLogic {
    private GameServer server;
    private Player clientPlayer;
    private Player serverPlayer;
    private char[][] board;
    private char currentPlayer;

    public GameLogic() {
        board = new char[3][3];
        currentPlayer = 'X';
        initializeBoard();
    }

    public void startServer(int port) throws IOException {
        server = new GameServer();
        server.start(port);
        serverPlayer = new Player("ServerPlayer");
        serverPlayer.startConnection("localhost", port);
    }

    public void startClient(String ip, int port, String playerName) throws IOException {
        clientPlayer = new Player(playerName);
        clientPlayer.startConnection(ip, port);
    }

    public void sendRequest(String request) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(request);
        } else if (serverPlayer != null) {
            server.sendMessage(request);
        }
    }

    public String receiveRequest() throws IOException {
        return server.receiveMessage();
    }

    public void sendResponse(String response) throws IOException {
        if (clientPlayer != null) {
            clientPlayer.sendPlayerRequest(response);
        } else if (serverPlayer != null) {
            server.sendMessage(response);
        }
    }

    public String receiveResponse() throws IOException {
        if (clientPlayer != null) {
            return clientPlayer.receivePlayerResponse();
        } else {
            return server.receiveMessage();
        }
    }

    public void stopServer() throws IOException {
        server.stop();
        serverPlayer.stopConnection();
    }

    public void stopClient() throws IOException {
        clientPlayer.stopConnection();
    }

    public void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkForWin() {
        return (checkRows() || checkColumns() || checkDiagonals());
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != '-' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        if (board[0][0] != '-' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }
        if (board[0][2] != '-' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }
        return false;
    }

    public void changePlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public boolean placeMark(int row, int col) {
        if ((row >= 0) && (row < 3)) {
            if ((col >= 0) && (col < 3)) {
                if (board[row][col] == '-') {
                    board[row][col] = currentPlayer;
                    return true;
                }
            }
        }
        return false;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean isCurrentPlayerX() {
        return currentPlayer == 'X';
    }

    // New method to set the current player explicitly
    public void setCurrentPlayer(char player) {
        currentPlayer = player;
    }

    // New method to handle opponent's move
    public void handleOpponentMove(int row, int col, char player) {
        setCurrentPlayer(player);
        placeMark(row, col);
        changePlayer();
    }
}
