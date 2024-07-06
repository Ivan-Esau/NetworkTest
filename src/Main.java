import gui.GameUI;
import logic.GameLogic;

public class Main {
    public static void main(String[] args) {
        GameLogic logic = new GameLogic();
        GameUI ui = new GameUI(logic);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui.createAndShowGUI();
            }
        });
    }
}
