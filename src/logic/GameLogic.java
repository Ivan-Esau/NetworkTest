package logic;

import network.GameServer;
import java.io.IOException;

public class GameLogic {
    private GameServer server;
    private Player player;
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
        player = new Player("ServerPlayer"); // Initialize player on server side
    }

    public void startClient(String ip, int port, String playerName) throws IOException {
        player = new Player(playerName);
        player.startConnection(ip, port);
    }

    public void sendRequest(String request) {
        player.sendPlayerRequest(request);
    }

    public String receiveRequest() throws IOException {
        return server.receiveMessage();
    }

    public void sendResponse(String response) {
        server.sendMessage(response);
    }

    public String receiveResponse() throws IOException {
        return player.receivePlayerResponse();
    }

    public void stopServer() throws IOException {
        server.stop();
    }

    public void stopClient() throws IOException {
        player.stopConnection();
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
            if (checkRowCol(board[i][0], board[i][1], board[i][2]) == true) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i]) == true) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return ((checkRowCol(board[0][0], board[1][1], board[2][2]) == true) || (checkRowCol(board[0][2], board[1][1], board[2][0]) == true));
    }

    private boolean checkRowCol(char c1, char c2, char c3) {
        return ((c1 != '-') && (c1 == c2) && (c2 == c3));
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
}
