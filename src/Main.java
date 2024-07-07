import gui.GameUI;
import logic.GameNetwork;
import logic.GameboardLogic;

public class Main {
    public static void main(String[] args) {
        GameboardLogic boardLogic = new GameboardLogic();
        GameNetwork network = new GameNetwork();
        GameUI ui = new GameUI(boardLogic, network);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui.createAndShowGUI();
            }
        });
    }
}
