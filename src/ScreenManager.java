import javax.swing.*;
import java.awt.*;

/**
 * Builds and manages all game screens (panels) and handles navigation
 * between them via CardLayout.
 *
 * Interacts with GameState, UpgradeShop, and UIFactory — demonstrating
 * class interaction across all student-designed classes.
 */
public class ScreenManager {
    // CardLayout constants
    public static final String MENU        = "MENU";
    public static final String MAIN_GAME   = "MAIN_GAME";
    public static final String INSTRUCTIONS = "INSTRUCTIONS";

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    /** Game logic references (class interaction). */
    private GameState gameState;
    private UpgradeShop upgradeShop;

    /** HUD labels updated each tick. */
    private JLabel applesLabel;
    private JLabel apsLabel;
    private JLabel treesLabel;
    private JButton upgradeButton;

    /**
     * Constructs a ScreenManager and initializes the main window.
     * @param gameState   the shared game state
     * @param upgradeShop the shop managing tree purchases
     */
    public ScreenManager(GameState gameState, UpgradeShop upgradeShop) {
        this.gameState   = gameState;
        this.upgradeShop = upgradeShop;

        frame = new JFrame("Tree Clicker - Idle Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);

        cardLayout     = new CardLayout();
        mainContainer  = new JPanel(cardLayout);

        // Build and register all screens
        mainContainer.add(buildMenuPanel(),         MENU);
        mainContainer.add(buildInstructionsPanel(), INSTRUCTIONS);
        mainContainer.add(buildMainGamePanel(),     MAIN_GAME);

        frame.add(mainContainer);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Navigates to the named screen.
     * @param screenName one of the screen constants defined in this class
     */
    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    /**
     * Refreshes all HUD labels from the current GameState.
     * Calls gameState methods — class interaction with GameState.
     * Also calls upgradeShop methods for button label/state.
     */
    public void refreshHud() {
        SwingUtilities.invokeLater(() -> {
            if (applesLabel  != null) applesLabel.setText("Apples: " + gameState.getApples());
            if (apsLabel     != null) apsLabel.setText("APS: " + gameState.getAps());
            if (treesLabel   != null) treesLabel.setText("Trees: " + gameState.getTreeCount());

            if (upgradeButton != null) {
                // Calls UpgradeShop for label and affordability — class interaction
                upgradeButton.setText(upgradeShop.getUpgradeButtonLabel());
                upgradeButton.setEnabled(upgradeShop.canAffordNext());
            }
        });
    }

    /**
     * Returns the main JFrame.
     * @return the game window
     */
    public JFrame getFrame() {
        return frame;
    }

    // ==================== MENU PANEL ====================

    /**
     * Builds and returns the main menu panel.
     * Uses UIFactory for styled components — class interaction.
     * @return the configured menu JPanel
     */
    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIFactory.FOREST_GREEN);

        // Title box
        JPanel topBox = new JPanel();
        topBox.setBackground(new Color(255, 215, 0));
        topBox.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 3));
        topBox.setMaximumSize(new Dimension(300, 100));
        topBox.setPreferredSize(new Dimension(300, 100));
        topBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));

        JLabel emojiLabel = new JLabel("\uD83C\uDF33");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("TREE CLICKER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topBox.add(Box.createVerticalStrut(10));
        topBox.add(emojiLabel);
        topBox.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Idle Game");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // New Game button — resets state then navigates
        JButton newGameBtn = UIFactory.makeButton("NEW GAME", new Color(50, 205, 50), 250, 60, 24);
        newGameBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Start a new game? All progress will be reset.",
                "New Game", JOptionPane.YES_NO_OPTION);
            // Relational: confirm == YES_OPTION
            if (confirm == JOptionPane.YES_OPTION) {
                gameState.reset();
                refreshHud();
                showScreen(MAIN_GAME);
            }
        });

        JButton continueBtn = UIFactory.makeButton("CONTINUE", new Color(34, 180, 34), 250, 60, 24);
        continueBtn.addActionListener(e -> {
            refreshHud();
            showScreen(MAIN_GAME);
        });

        JButton instructionsBtn = UIFactory.makeButton("INSTRUCTIONS", new Color(70, 130, 180), 250, 50, 20);
        instructionsBtn.addActionListener(e -> showScreen(INSTRUCTIONS));

        JButton quitBtn = UIFactory.makeButton("QUIT", new Color(220, 20, 60), 250, 50, 20);
        quitBtn.addActionListener(e -> System.exit(0));

        panel.add(Box.createVerticalStrut(60));
        panel.add(topBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(newGameBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(continueBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(instructionsBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(quitBtn);

        return panel;
    }

    // ==================== MAIN GAME PANEL ====================

    /**
     * Builds and returns the main gameplay panel.
     * HUD labels and the upgrade button are stored as instance fields
     * so refreshHud() can update them each tick.
     * @return the configured gameplay JPanel
     */
    private JPanel buildMainGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIFactory.FOREST_GREEN);

        // --- Stats bar (top) ---
        JPanel statsPanel = UIFactory.makeSolidPanel(Color.BLACK, 0.55f);
        statsPanel.setLayout(new GridLayout(1, 3));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        applesLabel = UIFactory.makeStatLabel("Apples: 0");
        apsLabel    = UIFactory.makeStatLabel("APS: 0");
        treesLabel  = UIFactory.makeStatLabel("Trees: 0");

        statsPanel.add(applesLabel);
        statsPanel.add(apsLabel);
        statsPanel.add(treesLabel);

        // --- Center: tree icon ---
        JPanel treePanel = UIFactory.makeSolidPanel(Color.BLACK, 0.35f);
        treePanel.setLayout(new GridBagLayout());

        JLabel treeIcon = new JLabel("\uD83C\uDF33");
        treeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 130));
        treePanel.add(treeIcon);

        // --- Bottom: buttons ---
        JPanel buttonPanel = UIFactory.makeSolidPanel(Color.BLACK, 0.55f);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        upgradeButton = UIFactory.makeButton(
            upgradeShop.getUpgradeButtonLabel(),
            new Color(70, 130, 180), 320, 50, 16
        );
        upgradeButton.addActionListener(e -> handleUpgradeClick());

        JButton menuBtn = UIFactory.makeButton("MENU", new Color(100, 100, 100), 120, 50, 16);
        menuBtn.addActionListener(e -> showScreen(MENU));

        buttonPanel.add(upgradeButton);
        buttonPanel.add(menuBtn);

        panel.add(statsPanel,  BorderLayout.NORTH);
        panel.add(treePanel,   BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Handles the logic when the player clicks the upgrade/buy button.
     * Delegates purchase to UpgradeShop, then checks win condition.
     * Calls multiple methods on UpgradeShop and GameState — class interaction.
     */
    private void handleUpgradeClick() {
        Tree purchased = upgradeShop.buyNextTree();

        // If statement: purchase may fail if not enough apples
        if (purchased == null) {
            JOptionPane.showMessageDialog(frame,
                "Not enough apples!\nYou need: " + upgradeShop.getNextTree().getCost()
                + "\nYou have: " + gameState.getApples());
            return;
        }

        refreshHud();

        // If statement: check win condition via polymorphism
        if (purchased.isWinCondition()) {
            // Calls getLegendText() — only available on GoldenTree (polymorphism)
            String legend = ((GoldenTree) purchased).getLegendText();
            JOptionPane.showMessageDialog(frame,
                "You grew the Golden Delicious!\n\n\"" + legend + "\"\n\nYOU WIN! \uD83C\uDF1F",
                "Victory!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Show most productive tree — calls GameState's algorithmic method
            Tree best = gameState.getMostProductiveTree();
            String bestName = (best != null) ? best.getName() : "none";
            JOptionPane.showMessageDialog(frame,
                "Bought: " + purchased.getName() + "!\n"
                + purchased.getDescription() + "\n\n"
                + "Best tree so far: " + bestName + "\n"
                + "Trees owned: " + gameState.getOwnedTreeSummary());
        }
    }

    // ==================== INSTRUCTIONS PANEL ====================

    /**
     * Builds and returns the instructions panel.
     * Uses UIFactory for header styling — class interaction.
     * Uses a for loop to iterate over instruction lines.
     * @return the configured instructions JPanel
     */
    private JPanel buildInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIFactory.FOREST_GREEN);

        JLabel title = UIFactory.makeHeaderLabel("HOW TO PLAY");

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(255, 248, 220));
        textPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        textPanel.setMaximumSize(new Dimension(600, 420));

        // Instruction lines: {"type", "text"} — "h" = header, "b" = body
        String[][] lines = {
            {"h", "OBJECTIVE"},
            {"b", "Grow your orchard and collect as many apples as possible!"},
            {"",  ""},
            {"h", "HOW TO PLAY"},
            {"b", "1. You start with 0 apples and 0 APS."},
            {"b", "2. Buy a tree upgrade to start generating apples per second."},
            {"b", "3. Each tier costs more but gives far more APS."},
            {"b", "4. Apples accumulate automatically every second."},
            {"",  ""},
            {"h", "UPGRADE TIERS"},
            {"b", "  Sapling          —  10 apples  (+1 APS)"},
            {"b", "  Apple Tree       —  50 apples  (+5 APS)"},
            {"b", "  Orchard Row      — 200 apples  (+20 APS)"},
            {"b", "  Golden Delicious — 1000 apples (+100 APS)"},
            {"",  ""},
            {"h", "WIN CONDITION"},
            {"b", "Purchase the Golden Delicious tree to win the game!"},
        };

        // Loop: iterate over all instruction lines to build the panel
        for (String[] line : lines) {
            String type = line[0];
            String text = line[1];

            JLabel lbl = new JLabel(text.isEmpty() ? " " : text);

            // If statement: style differently based on type
            if (type.equals("h")) {
                lbl.setFont(new Font("Arial", Font.BOLD, 15));
                lbl.setForeground(new Color(139, 69, 19));
            } else {
                lbl.setFont(new Font("Monospaced", Font.PLAIN, 13));
                lbl.setForeground(new Color(60, 30, 10));
            }

            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            textPanel.add(lbl);
            textPanel.add(Box.createVerticalStrut(3));
        }

        JButton backBtn = UIFactory.makeButton("BACK TO MENU", new Color(100, 100, 100), 200, 45, 16);
        backBtn.addActionListener(e -> showScreen(MENU));

        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(textPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(backBtn);

        return panel;
    }
}