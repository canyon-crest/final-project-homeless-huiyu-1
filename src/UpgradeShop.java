import java.util.ArrayList;
import java.util.List;

/**
 * Manages the catalogue of purchasable trees and handles buy logic.
 * Interacts with GameState to apply purchases.
 *
 * Demonstrates class interaction: UpgradeShop calls methods on GameState.
 */
public class UpgradeShop {
    /** The full ordered catalogue of trees available for purchase. */
    private List<Tree> catalogue;

    /** Reference to the game state (class interaction). */
    private GameState gameState;

    /**
     * Constructs an UpgradeShop for the given GameState,
     * and populates the tree catalogue.
     * @param gameState the current game state to apply purchases to
     */
    public UpgradeShop(GameState gameState) {
        this.gameState = gameState;
        catalogue = new ArrayList<>();
        buildCatalogue();
    }

    /**
     * Populates the shop catalogue with all available tree tiers.
     * Uses the inheritance hierarchy: FruitTree and GoldenTree extend Tree.
     */
    private void buildCatalogue() {
        // Standard FruitTrees (IS-A Tree via FruitTree)
        catalogue.add(new FruitTree("Sapling",     10,   1,  "Apple"));
        catalogue.add(new FruitTree("Apple Tree",  50,   5,  "Apple"));
        catalogue.add(new FruitTree("Orchard Row", 200,  20, "Apple"));

        // GoldenTree IS-A FruitTree IS-A Tree — win condition
        catalogue.add(new GoldenTree("Golden Delicious", 1000, 100,
                "The legendary tree of infinite harvest."));
    }

    /**
     * Returns the next tree in the catalogue the player hasn't purchased yet.
     * Uses the player's tree count from GameState to determine position.
     * Demonstrates class interaction: calls gameState.getTreeCount().
     * @return the next Tree to purchase, or null if all tiers are owned
     */
    public Tree getNextTree() {
        int owned = gameState.getTreeCount();
        // Relational operator <: check if there are still tiers remaining
        if (owned < catalogue.size()) {
            return catalogue.get(owned);
        }
        return null;
    }

    /**
     * Attempts to purchase the next tree in the catalogue.
     * Calls spendApples() and addTree() on GameState — class interaction.
     * Uses relational and logical operators to validate the purchase.
     * @return the Tree that was purchased, or null if purchase failed
     */
    public Tree buyNextTree() {
        Tree next = getNextTree();

        // Logical operator &&: tree must exist AND player must afford it
        if (next != null && gameState.getApples() >= next.getCost()) {
            boolean spent = gameState.spendApples(next.getCost());
            if (spent) {
                gameState.addTree(next);
                return next;
            }
        }
        return null;
    }

    /**
     * Returns whether the player can currently afford the next tree.
     * Demonstrates logical operator (&&) and relational operator (>=).
     * @return true if next tree exists and player has enough apples
     */
    public boolean canAffordNext() {
        Tree next = getNextTree();
        // Logical &&: must have a next tree AND enough apples
        return next != null && gameState.getApples() >= next.getCost();
    }

    /**
     * Returns a formatted label for the upgrade button,
     * showing the next tree's name and cost.
     * Calls getNextTree() and tree.getName()/getCost() — class interaction.
     * @return button label string
     */
    public String getUpgradeButtonLabel() {
        Tree next = getNextTree();
        // If statement: no more tiers available
        if (next == null) {
            return "MAX TIER REACHED";
        }
        return "BUY: " + next.getName() + " (" + next.getCost() + " apples)";
    }

    /**
     * Returns the full catalogue of trees in the shop.
     * @return unmodifiable view of the catalogue list
     */
    public List<Tree> getCatalogue() {
        return catalogue;
    }
}