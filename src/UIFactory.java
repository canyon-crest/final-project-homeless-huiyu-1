import javax.swing.*;
import java.awt.*;

/**
 * Provides static factory methods for creating consistently styled
 * Swing UI components used throughout the game.
 *
 * Interacts with no game-logic classes directly, but its components
 * are wired up by ScreenManager.
 */
public class UIFactory {

    /** Background color used across all game panels. */
    public static final Color FOREST_GREEN = new Color(34, 139, 34);

    /**
     * Creates a styled JButton with uniform appearance.
     * @param text     the button label
     * @param bg       background color
     * @param width    preferred and maximum width in pixels
     * @param height   preferred and maximum height in pixels
     * @param fontSize font size for the label
     * @return a configured JButton
     */
    public static JButton makeButton(String text, Color bg, int width, int height, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(width, height));
        btn.setPreferredSize(new Dimension(width, height));
        return btn;
    }

    /**
     * Creates a white, bold stats label for the HUD display.
     * @param text initial text for the label
     * @return a configured JLabel styled for the stats bar
     */
    public static JLabel makeStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    /**
     * Creates an opaque JPanel whose color simulates a semi-transparent
     * dark overlay on the forest-green background.
     * Uses a relational operator (<=) to clamp the alpha value.
     * @param base  the base color to blend (typically Color.BLACK)
     * @param alpha blend factor between 0.0 (invisible) and 1.0 (opaque)
     * @return a configured JPanel with the blended background color
     */
    public static JPanel makeSolidPanel(Color base, float alpha) {
        // Clamp alpha to valid range using relational operators
        if (alpha <= 0f) alpha = 0f;
        if (alpha >= 1f) alpha = 1f;

        // Blend base color over the game's forest-green background
        int r = (int)(base.getRed()   * alpha + FOREST_GREEN.getRed()   * (1 - alpha));
        int g = (int)(base.getGreen() * alpha + FOREST_GREEN.getGreen() * (1 - alpha));
        int b = (int)(base.getBlue()  * alpha + FOREST_GREEN.getBlue()  * (1 - alpha));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(r, g, b));
        panel.setOpaque(true);
        return panel;
    }

    /**
     * Creates a section-header JLabel styled with gold bold text.
     * @param text the header text to display
     * @return a configured JLabel for use as a section header
     */
    public static JLabel makeHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 36));
        lbl.setForeground(new Color(255, 215, 0)); // Gold
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}