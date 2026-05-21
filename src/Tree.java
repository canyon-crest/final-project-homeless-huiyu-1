/**
 * Superclass representing a generic tree in the orchard.
 * All purchasable trees extend this class.
 */
public class Tree {
    private String name;
    private int cost;
    private int apsGain;

    /**
     * Constructs a Tree with the given properties.
     * @param name    the display name of the tree
     * @param cost    the apple cost to purchase
     * @param apsGain the apples-per-second this tree contributes
     */
    public Tree(String name, int cost, int apsGain) {
        this.name    = name;
        this.cost    = cost;
        this.apsGain = apsGain;
    }

    /**
     * Returns the name of this tree.
     * @return tree name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the apple cost to purchase this tree.
     * @return purchase cost in apples
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the apples-per-second bonus this tree provides.
     * @return APS gain
     */
    public int getApsGain() {
        return apsGain;
    }

    /**
     * Returns a short description of this tree.
     * Subclasses should override to provide richer detail.
     * @return description string
     */
    public String getDescription() {
        return name + " | Cost: " + cost + " apples | +" + apsGain + " APS";
    }

    /**
     * Returns whether this tree is considered a "win" tree.
     * Base trees are never win conditions; subclasses may override.
     * @return false by default
     */
    public boolean isWinCondition() {
        return false;
    }
}