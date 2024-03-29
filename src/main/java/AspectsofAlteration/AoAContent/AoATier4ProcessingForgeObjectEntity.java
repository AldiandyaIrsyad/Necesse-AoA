package AspectsofAlteration.AoAContent;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public class AoATier4ProcessingForgeObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 20000;
    public static int recipeProcessTime = 200;

    public AoATier4ProcessingForgeObjectEntity(Level level, int x, int y) {
        super(level, "forge", x, y, 4, 4, false, false, true, new Tech[]{RecipeTechRegistry.FORGE,RecipeTechRegistry.getTech("aoafusion")});
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
