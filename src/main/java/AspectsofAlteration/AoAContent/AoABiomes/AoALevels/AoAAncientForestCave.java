package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
        import necesse.engine.GameEvents;
        import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
        import necesse.engine.localization.message.GameMessage;
        import necesse.engine.localization.message.LocalMessage;
        import necesse.engine.registries.ObjectRegistry;
        import necesse.engine.util.LevelIdentifier;
        import necesse.engine.world.WorldEntity;
        import necesse.level.maps.Level;
        import necesse.level.maps.generationModules.CaveGeneration;
        import necesse.level.maps.generationModules.GenerationTools;
        import necesse.level.maps.generationModules.PresetGeneration;

public class AoAAncientForestCave extends Level {
    public AoAAncientForestCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAAncientForestCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "aoabloomingstonetile", "rock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel();
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("crate");
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("watertile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.17F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("grasstile"), 1.0F, true);
            cg.generateOreVeins(0.08F, 5, 50, ObjectRegistry.getObjectID("air"));
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverock"), 0.005F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverocksmall"), 0.01F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("vase"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("crate"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("surfacerocksmall"), 0.1F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("aoalegendaryswordrock"), 0.0001F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {

            cg.generateOreVeins(0.25F, 10, 35, ObjectRegistry.getObjectID("aoarootresin"));
            cg.generateOreVeins(0.25F, 10, 35, ObjectRegistry.getObjectID("aoabushobject"));
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("clayrock"));
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("aoadeadlandbogrockfloritite"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            cg.generateRandomCrates(0.03F, new int[]{crate});
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }
}
