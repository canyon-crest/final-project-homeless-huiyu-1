import javax.swing.*;

/**
 * Entry point for Tree Clicker.
 * Wires together GameState, UpgradeShop, and ScreenManager,
 * then starts the APS timer.
 */
public class GameMenu {

    /**
     * Launches the game on the Swing Event Dispatch Thread.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create shared game state
            GameState gameState = new GameState();

            // Create shop with reference to state (class interaction)
            UpgradeShop upgradeShop = new UpgradeShop(gameState);

            // Create screen manager — builds and displays all panels
            ScreenManager screenManager = new ScreenManager(gameState, upgradeShop);

            // APS timer: every second, add APS to apples and refresh HUD
            Timer apsTimer = new Timer(1000, e -> {
                gameState.addApples(gameState.getAps());
                screenManager.refreshHud();
            });
            apsTimer.start();
        });
    }
}