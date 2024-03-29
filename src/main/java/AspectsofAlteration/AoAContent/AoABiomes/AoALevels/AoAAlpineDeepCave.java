package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.generationModules.*;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.RandomLootAreaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class AoAAlpineDeepCave extends ForestCaveLevel {
    public AoAAlpineDeepCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAAlpineDeepCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
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
