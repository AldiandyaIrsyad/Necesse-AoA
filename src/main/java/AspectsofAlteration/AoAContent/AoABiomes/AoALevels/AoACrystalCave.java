package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;

public class AoACrystalCave extends ForestCaveLevel {
    public AoACrystalCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoACrystalCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "rocktile", "rock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel();
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("crate");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("surfacerock"), 0.05F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("vase"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("crate"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("aoacrystalcluster"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("aoacrystalclustersmall"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("surfacerocksmall"), 0.1F);
            cg.generateOreVeins(0.08F, 5, 50, ObjectRegistry.getObjectID("air"));
            cg.generateOreVeins(0.25F, 10, 35, ObjectRegistry.getObjectID("aoabushobject"));
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("clayrock"));
            cg.generateOreVeins(0.45F, 3, 6, ObjectRegistry.getObjectID("copperorerock"));
            cg.generateOreVeins(0.35F, 3, 6, ObjectRegistry.getObjectID("ironorerock"));
            cg.generateOreVeins(0.1F, 3, 6, ObjectRegistry.getObjectID("goldorerock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));

    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }
}
