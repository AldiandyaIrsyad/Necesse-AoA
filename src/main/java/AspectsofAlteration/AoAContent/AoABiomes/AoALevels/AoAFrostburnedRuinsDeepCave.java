package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

import java.util.concurrent.atomic.AtomicInteger;
        import java.util.concurrent.atomic.AtomicInteger;
        import necesse.engine.GameEvents;
        import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
        import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
        import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
        import necesse.engine.localization.message.GameMessage;
        import necesse.engine.localization.message.LocalMessage;
        import necesse.engine.registries.ObjectRegistry;
        import necesse.engine.registries.TileRegistry;
        import necesse.engine.util.LevelIdentifier;
        import necesse.engine.world.WorldEntity;
        import necesse.inventory.lootTable.LootTable;
        import necesse.inventory.lootTable.LootTablePresets;
        import necesse.level.gameTile.GameTile;
        import necesse.level.maps.generationModules.CaveGeneration;
        import necesse.level.maps.generationModules.GenerationTools;
        import necesse.level.maps.generationModules.PresetGeneration;
        import necesse.level.maps.presets.Preset;
        import necesse.level.maps.presets.RandomCaveChestRoom;
        import necesse.level.maps.presets.caveRooms.CaveRuins;
        import necesse.level.maps.presets.set.ChestRoomSet;
        import necesse.level.maps.presets.set.FurnitureSet;
        import necesse.level.maps.presets.set.WallSet;

public class AoAFrostburnedRuinsDeepCave extends SnowCaveLevel {
    public AoAFrostburnedRuinsDeepCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAFrostburnedRuinsDeepCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "deepsnowrocktile", "deepsnowrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel(0.44F, 4, 3, 6);
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GameTile deepIceTile = TileRegistry.getTile("deepicetile");
            GenerationTools.generateRandomSmoothVeins(this, cg.random, 0.06F, 2, 7.0F, 20.0F, 3.0F, 8.0F, (level, tileX, tileY) -> {
                deepIceTile.placeTile(level, tileX, tileY);
                level.setObject(tileX, tileY, 0);
            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverock"), 0.005F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("deepsnowcaverocksmall"), 0.01F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeepsnowrock"));
            cg.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoredeepsnowrock"));
            cg.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeepsnowrock"));
            cg.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("tungstenoredeepsnowrock"));
            cg.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeepsnowrock"));
            cg.generateOreVeins(0.17F, 3, 6, ObjectRegistry.getObjectID("glacialoredeepsnowrock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);

            for(int i = 0; i < chestRoomAmount; ++i) {
                Preset chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepSnowCaveChest, chestRoomRotation, new ChestRoomSet[]{ChestRoomSet.deepStone, ChestRoomSet.deepSnowStone});
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID}));
                chestRoom.replaceTile(TileRegistry.deepSnowStoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.deepSnowStoneFloorID, TileRegistry.deepSnowStoneBrickFloorID}));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }

            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);

            for(int ix = 0; ix < caveRuinsCount; ++ix) {
                WallSet wallSet = (WallSet)cg.random.getOneOf(new WallSet[]{WallSet.deepStone, WallSet.deepSnowStone});
                FurnitureSet furnitureSet = (FurnitureSet)cg.random.getOneOf(new FurnitureSet[]{FurnitureSet.pine, FurnitureSet.spruce});
                String floorStringID = (String)cg.random.getOneOf(new String[]{"deepsnowstonefloor", "deepsnowstonebrickfloor"});
                Preset room = ((CaveRuins.CaveRuinGetter)cg.random.getOneOf(CaveRuins.caveRuinGetters)).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.snowDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }

            cg.generateRandomCrates(0.03F, new int[]{ObjectRegistry.getObjectID("snowcrate")});
        });
        GenerationTools.checkValid(this);
    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.snowDeepCrate;
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
    }
}
