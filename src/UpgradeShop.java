import java.util.ArrayList;
import java.util.List;

/**
 * Manages the catalogue of purchasable trees and handles buy logic.
 * Each tree tier can be purchased multiple times; cost scales up with each purchase.
 * Interacts with GameState to apply purchases.
 *
 * Demonstrates class interaction: UpgradeShop calls methods on GameState.
 */
public class UpgradeShop {
    /** The full ordered catalogue of tree tiers. */
    private List<Tree> catalogue;

    /**
     * How many of each tier the player has bought.
     * Index matches the catalogue list.
     */
    private int[] purchaseCounts;

    /** Reference to the game state (class interaction). */
    private GameState gameState;

    /** Cost scaling factor: each purchase raises cost by 15%. */
    private static final double COST_SCALE = 1.15;

    /**
     * Constructs an UpgradeShop for the given GameState
     * and populates the tree catalogue.
     * @param gameState the current game state to apply purchases to
     */
    public UpgradeShop(GameState gameState) {
        this.gameState = gameState;
        catalogue      = new ArrayList<>();
        buildCatalogue();
        purchaseCounts = new int[catalogue.size()];
    }

    /**
     * Populates the shop catalogue with all available tree tiers.
     * Uses the inheritance hierarchy: FruitTree and GoldenTree extend Tree.
     */
    private void buildCatalogue() {
        catalogue.add(new FruitTree("Sapling",     10,   1,  "Apple"));
        catalogue.add(new FruitTree("Apple Tree",  50,   5,  "Apple"));
        catalogue.add(new FruitTree("Orchard Row", 200,  20, "Apple"));
        catalogue.add(new GoldenTree("Golden Delicious", 1000, 100,
                "The legendary tree of infinite harvest."));
    }

    /**
     * Resets all purchase counts back to zero (called on new game).
     */
    public void reset() {
        // Loop: clear every tier's count
        for (int i = 0; i < purchaseCounts.length; i++) {
            purchaseCounts[i] = 0;
        }
    }

    /**
     * Returns the current scaled cost for a given tier index.
     * Cost increases by 15% for each copy already owned.
     * Uses Math.pow for compound scaling.
     * @param tierIndex index into the catalogue list
     * @return current purchase cost in apples
     */
    public int getScaledCost(int tierIndex) {
        // Relational operator: guard invalid index
        if (tierIndex < 0 || tierIndex >= catalogue.size()) {
            return Integer.MAX_VALUE;
        }
        int baseCost = catalogue.get(tierIndex).getCost();
        int owned    = purchaseCounts[tierIndex];
        // Compound scaling: baseCost * 1.15^owned, rounded to nearest int
        return (int) Math.round(baseCost * Math.pow(COST_SCALE, owned));
    }

    /**
     * Returns how many of a given tier the player has purchased.
     * @param tierIndex index into the catalogue list
     * @return number of times this tier has been bought
     */
    public int getPurchaseCount(int tierIndex) {
        // Relational: bounds check before array access
        if (tierIndex < 0 || tierIndex >= purchaseCounts.length) {
            return 0;
        }
        return purchaseCounts[tierIndex];
    }

    /**
     * Attempts to purchase one copy of the tree at the given tier index.
     * Calls spendApples() and addTree() on GameState — class interaction.
     * Uses relational and logical operators to validate the purchase.
     * @param tierIndex index into the catalogue list
     * @return the Tree that was purchased, or null if purchase failed
     */
    public Tree buyTree(int tierIndex) {
        // Logical &&: index must be valid AND player must afford it
        if (tierIndex < 0 || tierIndex >= catalogue.size()) {
            return null;
        }

        int  cost = getScaledCost(tierIndex);
        Tree tree = catalogue.get(tierIndex);

        // Relational >=: check affordability
        if (gameState.getApples() >= cost) {
            boolean spent = gameState.spendApples(cost);
            if (spent) {
                purchaseCounts[tierIndex]++;
                gameState.addTree(tree);
                return tree;
            }
        }
        return null;
    }

    /**
     * Returns whether the player can currently afford the given tier.
     * @param tierIndex index into the catalogue list
     * @return true if the player has enough apples for this tier
     */
    public boolean canAfford(int tierIndex) {
        // Logical &&: valid index AND enough apples
        return tierIndex >= 0
            && tierIndex < catalogue.size()
            && gameState.getApples() >= getScaledCost(tierIndex);
    }

    /**
     * Returns a formatted HTML button label for a given tier,
     * showing name, APS gain, scaled cost, and owned count.
     * @param tierIndex index into the catalogue list
     * @return HTML label string for use in a JButton
     */
    public String getButtonLabel(int tierIndex) {
        if (tierIndex < 0 || tierIndex >= catalogue.size()) {
            return "???";
        }
        Tree tree  = catalogue.get(tierIndex);
        int  cost  = getScaledCost(tierIndex);
        int  owned = purchaseCounts[tierIndex];
        return "<html><b>" + tree.getName() + "</b>"
             + "  [+" + tree.getApsGain() + " APS]"
             + "<br>Cost: " + cost + " apples"
             + "  &nbsp; Owned: " + owned + "</html>";
    }

    /**
     * Returns the full catalogue of tree tiers.
     * @return the catalogue list
     */
    public List<Tree> getCatalogue() {
        return catalogue;
    }

    /**
     * Returns the number of tiers in the catalogue.
     * @return catalogue size
     */
    public int getTierCount() {
        return catalogue.size();
    }
}