package AspectsofAlteration.AoAContent;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
        import necesse.engine.registries.RecipeTechRegistry;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.recipe.Recipe;
        import necesse.inventory.recipe.Tech;
        import necesse.level.maps.Level;

public class AoATier2ProcessingForgeObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 80000;
    public static int recipeProcessTime = 4000;

    public AoATier2ProcessingForgeObjectEntity(Level level, int x, int y) {
        super(level, "forge", x, y, 4, 4, false, false, true, new Tech[]{RecipeTechRegistry.FORGE});
    }

    public int getFuelTime(InventoryItem item) {
        return logFuelTime;
    }

    public int getProcessTime(Recipe recipe) {
        return recipeProcessTime;
    }

    public boolean shouldBeAbleToChangeKeepFuelRunning() {
        return false;
    }
}
