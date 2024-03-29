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
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
        import java.awt.Rectangle;
        import java.util.concurrent.atomic.AtomicInteger;
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
        import necesse.engine.registries.TileRegistry;
        import necesse.engine.util.LevelIdentifier;
        import necesse.engine.world.WorldEntity;
        import necesse.inventory.lootTable.LootTable;
        import necesse.inventory.lootTable.LootTablePresets;
        import necesse.level.maps.generationModules.CaveGeneration;
        import necesse.level.maps.generationModules.GenerationTools;
        import necesse.level.maps.generationModules.PresetGeneration;
        import necesse.level.maps.presets.Preset;
        import necesse.level.maps.presets.RandomCaveChestRoom;
        import necesse.level.maps.presets.caveRooms.CaveRuins;
        import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMinePreset;
        import necesse.level.maps.presets.set.ChestRoomSet;
        import necesse.level.maps.presets.set.FurnitureSet;
        import necesse.level.maps.presets.set.WallSet;

public class AoAAncientForestDeepCave extends ForestCaveLevel {
    public AoAAncientForestDeepCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAAncientForestDeepCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        int deepRockTile = TileRegistry.getTileID("deeprocktile");
        CaveGeneration cg = new CaveGeneration(this, "deeprocktile", "deeprock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel(0.44F, 4, 3, 6);
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            this.liquidManager.calculateShores();
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("watertile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.17F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("grasstile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("aoacyanmosstile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("aoamagentamosstile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07F, 2, 7.0F, 20.0F, 3.0F, 8.0F, TileRegistry.getTileID("aoagoldenmosstile"), 1.0F, true);

            cg.generateOreVeins(0.08F, 5, 50, ObjectRegistry.getObjectID("air"));
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverock"), 0.005F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverocksmall"), 0.01F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("vase"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("crate"), 0.02F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("surfacerocksmall"), 0.1F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("aoadeadlandbogrockfloritite"));
            cg.generateOreVeins(0.40F, 10, 15, ObjectRegistry.getObjectID("aoacyanmossplantgrassobject"));
            cg.generateOreVeins(0.40F, 10, 15, ObjectRegistry.getObjectID("aoamagentamossplantgrassobject"));
            cg.generateOreVeins(0.40F, 10, 15, ObjectRegistry.getObjectID("aoagoldenmossplantgrassobject"));
            cg.generateOreVeins(0.25F, 10, 35, ObjectRegistry.getObjectID("aoarootresin"));
            cg.generateOreVeins(0.25F, 10, 35, ObjectRegistry.getObjectID("aoabushobject"));
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("clayrock"));
            cg.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("copperoredeeprock"));
            cg.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoredeeprock"));
            cg.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoredeeprock"));
            cg.generateOreVeins(0.25F, 5, 10, ObjectRegistry.getObjectID("obsidianrock"));
            cg.generateOreVeins(0.2F, 3, 6, ObjectRegistry.getObjectID("tungstenoredeeprock"));
            cg.generateOreVeins(0.05F, 3, 6, ObjectRegistry.getObjectID("lifequartzdeeprock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            int abandonedMineCount = cg.random.getIntBetween(2, 3);

            for(int i = 0; i < abandonedMineCount; ++i) {
                Rectangle abandonedMineRec = AbandonedMinePreset.generateAbandonedMineOnLevel(this, cg.random, presets.getOccupiedSpace());
                if (abandonedMineRec != null) {
                    presets.addOccupiedSpace(abandonedMineRec);
                }
            }

            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);

            for(int ix = 0; ix < chestRoomAmount; ++ix) {
                Preset chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepCaveChest, chestRoomRotation, new ChestRoomSet[]{ChestRoomSet.deepStone, ChestRoomSet.obsidian});
                chestRoom.replaceTile(TileRegistry.deepStoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.deepStoneFloorID, TileRegistry.deepStoneBrickFloorID}));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }

            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);

            for(int ixx = 0; ixx < caveRuinsCount; ++ixx) {
                WallSet wallSet = (WallSet)cg.random.getOneOf(new WallSet[]{WallSet.deepStone, WallSet.obsidian});
                FurnitureSet furnitureSet = (FurnitureSet)cg.random.getOneOf(new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.spruce});
                String floorStringID = (String)cg.random.getOneOf(new String[]{"deepstonefloor", "deepstonebrickfloor"});
                Preset room = ((CaveRuins.CaveRuinGetter)cg.random.getOneOf(CaveRuins.caveRuinGetters)).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.basicDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }

            cg.generateRandomCrates(0.03F, new int[]{ObjectRegistry.getObjectID("crate")});
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.basicDeepCrate;
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "deepcave", "biome", this.biome.getLocalization());
    }
}
