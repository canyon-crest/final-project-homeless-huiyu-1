import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    /** One buy button per shop tier, stored so refreshHud() can update them. */
    private JButton[] tierButtons;

    /** Floating "+N" label shown briefly on tree click. */
    private JLabel clickPopLabel;

    /** The tree icon label — stored so we can animate it on click. */
    private JLabel treeIcon;

    /**
     * Constructs a ScreenManager and initialises the main window.
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
     * Refreshes all HUD labels and shop buttons from current game state.
     * Calls gameState methods — class interaction with GameState.
     * Also calls upgradeShop methods for each button label/state.
     */
    public void refreshHud() {
        SwingUtilities.invokeLater(() -> {
            if (applesLabel != null) applesLabel.setText("Apples: " + gameState.getApples());
            if (apsLabel    != null) apsLabel.setText("APS: " + gameState.getAps());
            if (treesLabel  != null) treesLabel.setText("Trees: " + gameState.getTreeCount());

            // Loop: update every tier button's label and enabled state
            if (tierButtons != null) {
                for (int i = 0; i < tierButtons.length; i++) {
                    tierButtons[i].setText(upgradeShop.getButtonLabel(i));
                    tierButtons[i].setEnabled(upgradeShop.canAfford(i));
                }
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
                upgradeShop.reset();
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

        // --- Center: clickable tree icon ---
        JPanel treePanel = UIFactory.makeSolidPanel(Color.BLACK, 0.35f);
        treePanel.setLayout(new GridBagLayout());

        // Stack the tree icon and the pop label using a layered approach
        JPanel treeStack = new JPanel();
        treeStack.setOpaque(false);
        treeStack.setLayout(new OverlayLayout(treeStack));

        // Floating "+N" pop label (hidden until clicked)
        clickPopLabel = new JLabel("");
        clickPopLabel.setFont(new Font("Arial", Font.BOLD, 28));
        clickPopLabel.setForeground(new Color(255, 220, 50));
        clickPopLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clickPopLabel.setVerticalAlignment(SwingConstants.CENTER);
        clickPopLabel.setAlignmentX(0.5f);
        clickPopLabel.setAlignmentY(0.2f); // float above the tree
        clickPopLabel.setVisible(false);
        
        File imgFile = new File("tree.png");
        


        if (imgFile.exists()) {
        	System.out.println("Java is looking for resources inside: " + getClass().getResource("/"));
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image resized = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            treeIcon = new JLabel(new ImageIcon(resized));
        } else {
        	System.out.println("Java is looking for resources inside: " + getClass().getResource("/"));
            System.err.println("File still not found. Absolute path tried: " + imgFile.getAbsolutePath());
        }
        
        
//        treeIcon.setOpaque(true);
//        treeIcon.setBackground(Color.RED);
//        treeIcon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        treeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 130));
        treeIcon.setHorizontalAlignment(SwingConstants.CENTER);
        treeIcon.setVerticalAlignment(SwingConstants.CENTER);
        treeIcon.setAlignmentX(100f);
        treeIcon.setAlignmentY(100f);
        treeIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        treeIcon.setToolTipText("Click to harvest apples!");

        // Click listener: award apples, bounce the icon, show pop label
        treeIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handleTreeClick();
            }
        });
        
        frame.add(treeIcon);

        treeStack.add(clickPopLabel);
        treeStack.add(treeIcon);
        treePanel.add(treeStack);

        // --- Bottom: shop tier buttons + menu ---
        JPanel buttonPanel = UIFactory.makeSolidPanel(Color.BLACK, 0.55f);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setLayout(new BorderLayout(8, 0));

        // One buy button per tier, laid out in a horizontal row
        int tierCount = upgradeShop.getTierCount();
        tierButtons   = new JButton[tierCount];
        JPanel tierRow = new JPanel(new GridLayout(1, tierCount, 8, 0));
        tierRow.setOpaque(false);

        // Loop: create a button for each catalogue tier
        for (int i = 0; i < tierCount; i++) {
            final int tierIndex = i; // capture for lambda
            JButton btn = UIFactory.makeButton(
                upgradeShop.getButtonLabel(i),
                new Color(70+10*i, 130-10*i, 180), 160, 120, 12
            );
            btn.setEnabled(upgradeShop.canAfford(i));
            btn.addActionListener(e -> handleTierClick(tierIndex));
            tierButtons[i] = btn;
            tierRow.add(btn);
        }

        JButton menuBtn = UIFactory.makeButton("MENU", new Color(100, 100, 100), 80, 55, 14);
        menuBtn.addActionListener(e -> showScreen(MENU));

        buttonPanel.add(tierRow,  BorderLayout.CENTER);
        buttonPanel.add(menuBtn,  BorderLayout.EAST);

        panel.add(statsPanel,  BorderLayout.NORTH);
        panel.add(treePanel,   BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Handles a manual click on the tree icon.
     * Awards apples via GameState.clickTree(), bounces the tree icon,
     * and briefly shows a floating "+N" label.
     * Demonstrates class interaction: calls gameState.clickTree().
     */
    private void handleTreeClick() {

//        File imgFile = new File("greentree.png");
//        
//        JLabel treeIcon2 = new JLabel();
//        if (imgFile.exists()) {
//            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
//            Image resized = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//            treeIcon2 = new JLabel(new ImageIcon(resized));
//        } else {
//        	System.out.println("Java is looking for resources inside: " + getClass().getResource("/"));
//            System.err.println("File still not found. Absolute path tried: " + imgFile.getAbsolutePath());
//        }
//        treeIcon2.setHorizontalAlignment(SwingConstants.CENTER);
//        treeIcon2.setVerticalAlignment(SwingConstants.CENTER);
//        treeIcon2.setAlignmentX(100f);
//        treeIcon2.setAlignmentY(100f);
//        treeIcon2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        treeIcon2.setToolTipText("Click to harvest apples!");

//        frame.add(treeIcon);
        int earned = gameState.clickTree();
        refreshHud();

        // Show the "+N" pop label briefly
        clickPopLabel.setText("+" + earned);
        clickPopLabel.setVisible(true);

        // Bounce animation: scale up then back down via font size steps
        Timer bounceTimer = new Timer(30, null);
        final int[] step = {0};
        final int[] sizes = {150, 160, 170, 160, 150, 150};
        bounceTimer.addActionListener(e -> {
            if (step[0] < sizes.length) {
            	treeIcon.setSize(sizes[step[0]], sizes[step[0]]);
//                treeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, sizes[step[0]]));
                step[0]++;
            } else {
                // Reset to base size and stop
            	treeIcon.setSize(150,150);
//                treeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 130));
                bounceTimer.stop();
            }
        });
        bounceTimer.start();

        // Hide the pop label after 600ms
        Timer fadeTimer = new Timer(600, e -> clickPopLabel.setVisible(false));
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    /**
     * Handles the logic when the player clicks a specific tier's buy button.
     * Delegates purchase to UpgradeShop.buyTree(tierIndex), then checks win condition.
     * Calls multiple methods on UpgradeShop and GameState — class interaction.
     * @param tierIndex the catalogue index of the tier that was clicked
     */
    private void handleTierClick(int tierIndex) {
        Tree purchased = upgradeShop.buyTree(tierIndex);

        // If statement: purchase fails when player can't afford it
        if (purchased == null) {
            int needed = upgradeShop.getScaledCost(tierIndex);
            JOptionPane.showMessageDialog(frame,
                "Not enough apples!\nYou need: " + needed
                + "\nYou have: " + gameState.getApples());
            return;
        }
        
        if(upgradeShop.getCount(tierIndex)==1) {
            switch (tierIndex) {
            case 0:
            	break;
            case 1:
            	//
//                File imgFile = new File("src/pinktree.png");
//                
//                JLabel treeIcon2 = new JLabel();
//                if (imgFile.exists()) {
//                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
//                    Image resized = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//                    treeIcon2 = new JLabel(new ImageIcon(resized));
//                } else {
//                    System.err.println("File still not found. Absolute path tried: " + imgFile.getAbsolutePath());
//                }
//                treeIcon2.setHorizontalAlignment(SwingConstants.CENTER);
//                treeIcon2.setVerticalAlignment(SwingConstants.CENTER);
//                treeIcon2.setAlignmentX(100f);
//                treeIcon2.setAlignmentY(100f);
//                treeIcon2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                treeIcon2.setToolTipText("Click to harvest apples!");
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            }
        	
        }

        refreshHud();

        // If statement: check win condition via polymorphism
        if (purchased.isWinCondition() && upgradeShop.getCount(tierIndex) == 1) {
            String legend = ((GoldenTree) purchased).getLegendText();
            JOptionPane.showMessageDialog(frame,
                "You grew the Golden Delicious!\n\n\"" + legend + "\"\n\nYOU WIN! \uD83C\uDF1F",
                "Victory!", JOptionPane.INFORMATION_MESSAGE);
        }
        // No dialog for normal purchases — the button label updates are feedback enough
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
            {"b", "  Honeycrisp       —  100 apples      (+10 APS)"},
            {"b", "  Granny Smith     —  450 apples      (+50 APS)"},
            {"b", "  Fuji             —  25000 apples    (+300 APS)"},
            {"b", "  Gala             —  90000 apples    (+1000 APS)"},
            {"b", "  Ambrosia         —  500000 apples   (+8000 APS)"},
            {"b", "  Golden Delicious —  1000000 apples  (+100 APS)"},
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