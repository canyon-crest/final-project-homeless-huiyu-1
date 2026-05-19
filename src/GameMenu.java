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

            // Tick 20x per second: award aps/20 apples each tick for smooth continuous income
            final int TICK_MS = 50;
            final int TICKS_PER_SEC = 1000 / TICK_MS;
            Timer apsTimer = new Timer(TICK_MS, e -> {
                gameState.tickApples((double) gameState.getAps() / TICKS_PER_SEC);
                screenManager.refreshHud();
            });
            apsTimer.start();
        });
    }
}