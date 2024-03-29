package AspectsofAlteration.AoAContent;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.entity.particle.Particle;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

import java.awt.*;

public class AoATier2ExtractorObjectEntity extends ProcessingTechInventoryObjectEntity {
    public AoATier2ExtractorObjectEntity(Level level, int x, int y) {
        super(level, "cheesepress", x, y, 4, 4, new Tech[]{RecipeTechRegistry.getTech("aoatier1extractor")});
    }

    public void clientTick() {
        super.clientTick();
        if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
            int startHeight = 24 + GameRandom.globalRandom.nextInt(16);
            this.getLevel().entityManager.addParticle((float)(this.getX() * 32 + GameRandom.globalRandom.nextInt(32)), (float)(this.getY() * 32 + 32), Particle.GType.COSMETIC).color(new Color(148, 148, 148)).heightMoves((float)startHeight, (float)(startHeight + 20)).lifeTime(1000);
        }

    }

    public int getProcessTime() {
        return 800;
    }
}
