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

    /** Fractional apple accumulator for sub-second APS ticks. */
    private double appleAccumulator = 0.0;

    /**
     * Resets the game to its initial state: no apples, no APS, no trees.
     */
    public void reset() {
        apples           = 0;
        aps              = 0;
        appleAccumulator = 0.0;
        purchasedTrees.clear();
    }

    /**
     * Adds a fractional apple amount each sub-second tick.
     * Whole apples are flushed to the integer count as they accumulate,
     * giving smooth continuous income rather than once-per-second jumps.
     * @param amount fractional apples to add this tick (e.g. aps / 20.0)
     */
    public void tickApples(double amount) {
        appleAccumulator += amount;
        // Flush any whole apples that have accumulated
        if (appleAccumulator >= 1.0) {
            int whole = (int) appleAccumulator;
            apples           += whole;
            appleAccumulator -= whole;
        }
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
     * Returns a summary string listing each unique tree name and how many
     * of that type the player owns.
     * Demonstrates list traversal with a for loop and string building.
     * @return newline-separated "Name x Count" entries, or "None" if empty
     */
    public String getOwnedTreeSummary() {
        // Logical operator ||: check both null and empty
        if (purchasedTrees == null || purchasedTrees.isEmpty()) {
            return "None";
        }

        // Use a LinkedHashMap to preserve insertion order while counting
        java.util.LinkedHashMap<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (Tree t : purchasedTrees) {
            // Logical operator !: key not yet present, initialise to 0
            if (!counts.containsKey(t.getName())) {
                counts.put(t.getName(), 0);
            }
            counts.put(t.getName(), counts.get(t.getName()) + 1);
        }

        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (sb.length() != 0) sb.append("\n");
            sb.append(entry.getKey()).append(" x").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * Returns how many trees of a given name the player owns.
     * Traverses the purchased list with a for loop and relational comparison.
     * @param name the tree name to count
     * @return count of matching trees owned
     */
    public int getCountOfTree(String name) {
        int count = 0;
        // Loop: traverse all purchased trees
        for (Tree t : purchasedTrees) {
            // Relational via .equals(): name match
            if (t.getName().equals(name)) {
                count++;
            }
        }
        return count;
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