import javax.swing.*;
import java.awt.*;

public class GameMenu {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainContainer;

    // Game variables
    private static int apples = 0;
    private static int aps = 0;
    private static int treesOwned = 0;
    private static Timer apsTimer;

    // Tree upgrade tiers: {name, cost, apsGain}
    private static final Object[][] TREE_TIERS = {
        {"Sapling",         10,    1},
        {"Apple Tree",      50,    5},
        {"Orchard Row",     200,   20},
        {"Golden Delicious",1000,  100},
    };

    // UI Components
    private static JLabel applesLabel;
    private static JLabel apsLabel;
    private static JLabel treesLabel;
    private static JButton upgradeButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Tree Clicker - Idle Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setResizable(false);

            cardLayout = new CardLayout();
            mainContainer = new JPanel(cardLayout);

            mainContainer.add(createMenuPanel(),         "MENU");
            mainContainer.add(createInstructionsPanel(), "INSTRUCTIONS");
            mainContainer.add(createMainGamePanel(),     "MAIN_GAME");

            frame.add(mainContainer);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            startAPSTimer();
        });
    }

    // ==================== APS TIMER ====================
    private static void startAPSTimer() {
        apsTimer = new Timer(1000, e -> {
            apples += aps;
            updateMainGameDisplay();
        });
        apsTimer.start();
    }

    // ==================== UPDATE DISPLAY ====================
    private static void updateMainGameDisplay() {
        SwingUtilities.invokeLater(() -> {
            if (applesLabel != null)  applesLabel.setText("Apples: " + apples);
            if (apsLabel != null)     apsLabel.setText("APS: " + aps);
            if (treesLabel != null)   treesLabel.setText("Trees: " + treesOwned);
            if (upgradeButton != null) {
                int[] next = getNextUpgrade();
                if (next != null) {
                    String tierName = (String) TREE_TIERS[next[0]][0];
                    upgradeButton.setText("BUY: " + tierName + " (" + next[1] + " apples)");
                    upgradeButton.setEnabled(apples >= next[1]);
                } else {
                    upgradeButton.setText("MAX TIER REACHED");
                    upgradeButton.setEnabled(false);
                }
            }
        });
    }

    /**
     * Returns {tierIndex, cost, apsGain} for the cheapest tier the player
     * hasn't bought yet, or null if all tiers are owned.
     */
    private static int[] getNextUpgrade() {
        // Each tier is bought once; treesOwned tracks how many tiers purchased
        if (treesOwned >= TREE_TIERS.length) return null;
        int cost    = (int) TREE_TIERS[treesOwned][1];
        int apsGain = (int) TREE_TIERS[treesOwned][2];
        return new int[]{treesOwned, cost, apsGain};
    }

    // ==================== OPAQUE HELPER ====================
    /** Creates a solid panel whose color approximates an alpha blend over bg. */
    private static JPanel solidPanel(Color base, float alpha) {
        // Blend base over the game's forest-green background
        Color bg = new Color(34, 139, 34);
        int r = (int)(base.getRed()   * alpha + bg.getRed()   * (1 - alpha));
        int g = (int)(base.getGreen() * alpha + bg.getGreen() * (1 - alpha));
        int b = (int)(base.getBlue()  * alpha + bg.getBlue()  * (1 - alpha));
        JPanel p = new JPanel();
        p.setBackground(new Color(r, g, b));
        p.setOpaque(true);
        return p;
    }

    // ==================== MENU PANEL ====================
    private static JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 139, 34));

        // Title box
        JPanel topBox = new JPanel();
        topBox.setBackground(new Color(255, 215, 0));
        topBox.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 3));
        topBox.setMaximumSize(new Dimension(300, 100));
        topBox.setPreferredSize(new Dimension(300, 100));
        topBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TREE CLICKER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel treeEmoji = new JLabel("\uD83C\uDF33"); // 🌳
        treeEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        treeEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        topBox.add(Box.createVerticalStrut(10));
        topBox.add(treeEmoji);
        topBox.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Idle Game");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = makeButton("NEW GAME", new Color(50, 205, 50), 250, 60, 24);
        startButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Start a new game? All progress will be reset.",
                "New Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                apples = 0; aps = 0; treesOwned = 0;
                updateMainGameDisplay();
                cardLayout.show(mainContainer, "MAIN_GAME");
            }
        });

        JButton continueButton = makeButton("CONTINUE", new Color(34, 180, 34), 250, 60, 24);
        continueButton.addActionListener(e -> {
            updateMainGameDisplay();
            cardLayout.show(mainContainer, "MAIN_GAME");
        });

        JButton instructionsButton = makeButton("INSTRUCTIONS", new Color(70, 130, 180), 250, 50, 20);
        instructionsButton.addActionListener(e -> cardLayout.show(mainContainer, "INSTRUCTIONS"));

        JButton quitButton = makeButton("QUIT", new Color(220, 20, 60), 250, 50, 20);
        quitButton.addActionListener(e -> System.exit(0));

        panel.add(Box.createVerticalStrut(60));
        panel.add(topBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(startButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(continueButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(instructionsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(quitButton);

        return panel;
    }

    // ==================== MAIN GAME PANEL ====================
    private static JPanel createMainGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 139, 34));

        // --- Stats bar (top) ---
        JPanel statsPanel = solidPanel(Color.BLACK, 0.55f);
        statsPanel.setLayout(new GridLayout(1, 3));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        applesLabel = statLabel("Apples: 0");
        apsLabel    = statLabel("APS: 0");
        treesLabel  = statLabel("Trees: 0");

        statsPanel.add(applesLabel);
        statsPanel.add(apsLabel);
        statsPanel.add(treesLabel);

        // --- Center: tree display ---
        JPanel treeDisplayPanel = solidPanel(Color.BLACK, 0.35f);
        treeDisplayPanel.setLayout(new GridBagLayout());

        JLabel treeIconLabel = new JLabel("\uD83C\uDF33"); // 🌳
        treeIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 130));
        // Fallback: if emoji doesn't render, show text
        treeIconLabel.setToolTipText("Apple Tree");
        treeDisplayPanel.add(treeIconLabel);

        // --- Bottom buttons ---
        JPanel buttonPanel = solidPanel(Color.BLACK, 0.55f);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        upgradeButton = makeButton("BUY: Sapling (10 apples)", new Color(70, 130, 180), 300, 50, 16);
        upgradeButton.addActionListener(e -> {
            int[] next = getNextUpgrade();
            if (next == null) return;
            int cost    = next[1];
            int apsGain = next[2];
            String name = (String) TREE_TIERS[next[0]][0];
            if (apples >= cost) {
                apples -= cost;
                treesOwned++;
                aps += apsGain;
                updateMainGameDisplay();
                if (name.equals("Golden Delicious")) {
                    JOptionPane.showMessageDialog(frame,
                        "You grew the Golden Delicious tree!\n\nYOU WIN! \uD83C\uDF1F",
                        "Victory!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                        "Bought: " + name + "!\nAPS is now " + aps + ".");
                }
            }
        });

        JButton menuButton = makeButton("MENU", new Color(100, 100, 100), 120, 50, 16);
        menuButton.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        buttonPanel.add(upgradeButton);
        buttonPanel.add(menuButton);

        panel.add(statsPanel,      BorderLayout.NORTH);
        panel.add(treeDisplayPanel, BorderLayout.CENTER);
        panel.add(buttonPanel,     BorderLayout.SOUTH);

        return panel;
    }

    // ==================== INSTRUCTIONS PANEL ====================
    private static JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(34, 139, 34));

        JLabel titleLabel = new JLabel("HOW TO PLAY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(255, 248, 220));
        textPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        textPanel.setMaximumSize(new Dimension(600, 420));

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
            {"b", "  Sapling          \u2014  10 apples  (+1 APS)"},
            {"b", "  Apple Tree       \u2014  50 apples  (+5 APS)"},
            {"b", "  Orchard Row      \u2014 200 apples  (+20 APS)"},
            {"b", "  Golden Delicious \u2014 1000 apples (+100 APS)"},
            {"",  ""},
            {"h", "WIN CONDITION"},
            {"b", "Purchase the Golden Delicious tree to win the game!"},
        };

        for (String[] line : lines) {
            String type = line[0], text = line[1];
            JLabel lbl = new JLabel(text.isEmpty() ? " " : text);
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

        JButton backButton = makeButton("BACK TO MENU", new Color(100, 100, 100), 200, 45, 16);
        backButton.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(textPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(backButton);

        return panel;
    }

    // ==================== HELPERS ====================
    private static JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private static JButton makeButton(String text, Color bg, int w, int h, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(w, h));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }
}