import java.util.ArrayList;
import java.util.List;

/**
 * Holds all runtime game data: apple count, APS, and the list of
 * trees the player has purchased.
 *
 * Demonstrates HAS-A composition: GameState has a List of Tree objects.
 */
public class GameState {
    private int apples;
    private int aps;

    /** The list of trees purchased by the player (composition / has-a). */
    private List<Tree> purchasedTrees;

    /**
     * Constructs a fresh GameState with zeroed values.
     */
    public GameState() {
        purchasedTrees = new ArrayList<>();
        reset();
    }

    /**
     * Resets the game to its initial state: no apples, no APS, no trees.
     */
    public void reset() {
        apples = 0;
        aps    = 0;
        purchasedTrees.clear();
    }

    /**
     * Adds the given number of apples to the player's total.
     * @param amount number of apples to add (must be >= 0)
     */
    public void addApples(int amount) {
        // Relational operator >=: guard against negative amounts
        if (amount >= 0) {
            apples += amount;
        }
    }

    /**
     * Attempts to spend the given number of apples.
     * Uses a relational operator (<) to check affordability.
     * @param amount number of apples to spend
     * @return true if the transaction succeeded, false if insufficient apples
     */
    public boolean spendApples(int amount) {
        if (apples < amount) {
            return false;
        }
        apples -= amount;
        return true;
    }

    /**
     * Records a newly purchased tree, adding its APS to the running total.
     * Calls tree.getApsGain() — class interaction with Tree.
     * @param tree the Tree that was purchased
     */
    public void addTree(Tree tree) {
        purchasedTrees.add(tree);
        aps += tree.getApsGain();
    }

    /**
     * Returns the number of trees the player has purchased.
     * @return count of purchased trees
     */
    public int getTreeCount() {
        return purchasedTrees.size();
    }

    /**
     * Calculates the total APS by traversing the purchased tree list.
     * Demonstrates list traversal with a for-each loop.
     * This recomputes from scratch rather than trusting the cached value,
     * useful for verification or after a reset.
     * @return recalculated total APS
     */
    public int recalculateAps() {
        int total = 0;
        // Loop: traverse the purchased tree list
        for (Tree t : purchasedTrees) {
            total += t.getApsGain();
        }
        return total;
    }

    /**
     * Finds the most productive tree the player owns (highest APS gain).
     * Demonstrates algorithmic complexity: linear search with comparison.
     * Returns null if no trees have been purchased yet.
     * @return the Tree with the highest apsGain, or null if none owned
     */
    public Tree getMostProductiveTree() {
        // Guard: no trees purchased yet
        if (purchasedTrees.isEmpty()) {
            return null;
        }

        Tree best = purchasedTrees.get(0);

        // Loop through all trees to find the highest APS contributor
        for (int i = 1; i < purchasedTrees.size(); i++) {
            Tree current = purchasedTrees.get(i);
            // Relational operator >: compare APS values
            if (current.getApsGain() > best.getApsGain()) {
                best = current;
            }
        }
        return best;
    }

    /**
     * Returns a summary string listing all purchased tree names.
     * Demonstrates list traversal and string building with a for loop.
     * @return comma-separated list of owned tree names, or "None" if empty
     */
    public String getOwnedTreeSummary() {
        // Logical operator ||: check both null and empty
        if (purchasedTrees == null || purchasedTrees.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < purchasedTrees.size(); i++) {
            // Logical operator !: not the first element, so prepend comma
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(purchasedTrees.get(i).getName());
        }
        return sb.toString();
    }

    /**
     * Registers a manual click on the tree, awarding 1 apple plus a bonus
     * equal to 10% of the current APS (minimum 1 total).
     * Demonstrates relational operator (>) for bonus calculation.
     * @return the number of apples awarded for this click
     */
    public int clickTree() {
        // Bonus = 10% of APS, but always at least 1 apple per click
        int bonus = (int)(aps * 0.1);
        int earned = (bonus > 0) ? 1 + bonus : 1;
        apples += earned;
        return earned;
    }

    // ---- Standard getters ----

    /**
     * Returns the current apple count.
     * @return apples
     */
    public int getApples() { return apples; }

    /**
     * Returns the current apples-per-second rate.
     * @return aps
     */
    public int getAps() { return aps; }

    /**
     * Returns the full list of purchased trees.
     * @return list of Tree objects
     */
    public List<Tree> getPurchasedTrees() { return purchasedTrees; }
}