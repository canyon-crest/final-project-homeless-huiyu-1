/**
 * A FruitTree is a Tree that produces a named fruit.
 * Extends Tree with fruit-type information.
 * Represents the "is-a" relationship: a FruitTree IS-A Tree.
 */
public class FruitTree extends Tree {
    private String fruitType;

    /**
     * Constructs a FruitTree with a specific fruit type.
     * @param name      the display name of the tree
     * @param cost      the apple cost to purchase
     * @param apsGain   the apples-per-second this tree contributes
     * @param fruitType the type of fruit this tree produces (e.g. "Apple")
     */
    public FruitTree(String name, int cost, int apsGain, String fruitType) {
        super(name, cost, apsGain);
        this.fruitType = fruitType;
    }

    /**
     * Returns the type of fruit this tree produces.
     * @return fruit type string
     */
    public String getFruitType() {
        return fruitType;
    }

    /**
     * Returns whether this tree produces apples specifically.
     * Uses a relational operator (==) via .equals().
     * @return true if fruitType is "Apple"
     */
    public boolean isAppleTree() {
        return fruitType.equals("Apple");
    }

    /**
     * Returns an enriched description including the fruit type.
     * @return description string
     */
    @Override
    public String getDescription() {
        // Uses logical operator (&&) to build a richer label
        String extra = (isAppleTree() && getApsGain() >= 5) ? " [High Yield]" : "";
        return super.getDescription() + " | Fruit: " + fruitType + extra;
    }
}