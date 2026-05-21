/**
 * A GoldenTree is a legendary FruitTree that triggers the win condition.
 * Extends FruitTree, continuing the inheritance hierarchy:
 * GoldenTree IS-A FruitTree IS-A Tree.
 */
public class GoldenTree extends FruitTree {
    private String legendText;

    /**
     * Constructs a GoldenTree with a legend inscription.
     * @param name       display name
     * @param cost       apple cost to purchase
     * @param apsGain    apples-per-second contribution
     * @param legendText a special flavour-text message shown on purchase
     */
    public GoldenTree(String name, int cost, int apsGain, String legendText) {
        super(name, cost, apsGain, "Golden Apple");
        this.legendText = legendText;
    }

    /**
     * Returns the legend text for this tree.
     * @return legend inscription string
     */
    public String getLegendText() {
        return legendText;
    }

    /**
     * GoldenTree is always the win condition.
     * Overrides the base Tree default of false.
     * @return true always
     */
    @Override
    public boolean isWinCondition() {
        return true;
    }

    /**
     * Returns a gilded description including the legend text.
     * Uses the relational operator != to guard against null legend text.
     * @return description string
     */
    @Override
    public String getDescription() {
        // Uses != null check (relational) and ! (logical NOT) for guard
        String suffix = (legendText != null && !legendText.isEmpty())
                ? " | \"" + legendText + "\""
                : "";
        return super.getDescription() + suffix;
    }
}