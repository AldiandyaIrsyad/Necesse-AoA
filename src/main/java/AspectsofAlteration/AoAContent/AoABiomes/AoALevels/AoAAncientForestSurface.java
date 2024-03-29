package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.IslandGeneration;
import necesse.level.maps.presets.RandomRuinsPreset;

public class AoAAncientForestSurface extends Level {
    public AoAAncientForestSurface(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAAncientForestSurface(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0F) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("mudtile");
        int rockTile = TileRegistry.getTileID("rocktile");
        int grassTile = TileRegistry.grassID;
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), (e) -> {
            ig.generateShapedIsland(waterTile, grassTile, sandTile);
            int rivers = ig.random.getIntBetween(1, 5);

            for(int i = 0; i < rivers && (i <= 0 || !ig.random.getChance(0.4F)); ++i) {
                ig.generateRiver(waterTile, grassTile, sandTile);
            }

            ig.generateLakes(0.02F, waterTile, grassTile, sandTile);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateHeights();
        });
        GameTile mudTile = TileRegistry.getTile(TileRegistry.mudID);
        GameObject wildMushroom = ObjectRegistry.getObject(ObjectRegistry.getObjectID("wildmushroom"));
        GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.1F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
            if (level.getTileID(tileX, tileY) == grassTile) {
                if (ig.random.getChance(0.7F)) {
                    mudTile.placeTile(level, tileX, tileY);
                }

                if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.1F) && wildMushroom.canPlace(level, tileX, tileY, 0) == null) {
                    wildMushroom.placeObject(level, tileX, tileY, 0);
                }
            }

        });
        GameTile stoneTile = TileRegistry.getTile(TileRegistry.getTileID("rocktile"));
        GameObject stoneObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("rock"));
        GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.1F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
            if (level.getTileID(tileX, tileY) == grassTile) {
                if (ig.random.getChance(0.7F)) {
                    stoneTile.placeTile(level, tileX, tileY);
                }

                if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.1F) && stoneObject.canPlace(level, tileX, tileY, 0) == null) {
                    stoneObject.placeObject(level, tileX, tileY, 0);
                }
            }

        });
        GameObject cattail = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cattail"));
        GenerationTools.generateRandomVeins(this, ig.random, 0.2F, 12, 20, (level, tileX, tileY) -> {
            if (ig.random.getChance(0.3F) && cattail.canPlace(level, tileX, tileY, 0) == null) {
                cattail.placeObject(level, tileX, tileY, 0);
            }

        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), (e) -> {
            int oakTree = ObjectRegistry.getObjectID("oaktree");
            int stone = ObjectRegistry.getObjectID("surfacerocksmall");
            int spruceTree = ObjectRegistry.getObjectID("sprucetree");
            int bush = ObjectRegistry.getObjectID("aoabushobject");
            int shrubbush = ObjectRegistry.getObjectID("aoashrubbushobject");
            int grassObject = ObjectRegistry.getObjectID("aoatallancientgrass");
            int mushroomgrassObject = ObjectRegistry.getObjectID("aoamushroomgrassobject");
            ig.generateCellMapObjects(0.35F, oakTree, grassTile, 0.08F);
            ig.generateCellMapObjects(0.35F, spruceTree, grassTile, 0.12F);
            ig.generateCellMapObjects(0.35F, shrubbush, grassTile, 0.28F);
            ig.generateCellMapObjects(0.35F, bush, grassTile, 0.32F);
            ig.generateObjects(grassObject, grassTile, 0.4F);
            ig.generateObjects(mushroomgrassObject, sandTile, 0.4F);
            ig.generateObjects(stone, rockTile, 0.5F);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerock"), -1, 0.001F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerocksmall"), -1, 0.002F, false);
            ig.ensureGenerateObjects("beehive", 1, new int[]{grassTile});
            ig.generateFruitGrowerSingle("appletree", 0.02F, new int[]{grassTile});
            ig.generateFruitGrowerVeins("blueberrybush", 0.04F, 8, 10, 0.1F, new int[]{grassTile});
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03F, 6, 12, grassTile, ObjectRegistry.getObjectID("wildfiremone"), 0.2F, false);
            GameObject waterPlant = ObjectRegistry.getObject(ObjectRegistry.getObjectID("watergrass"));
            GenerationTools.generateRandomVeins(this, ig.random, 0.2F, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.3F) && waterPlant.canPlace(level, tileX, tileY, 0) == null && level.liquidManager.isFreshWater(tileX, tileY)) {
                    waterPlant.placeObject(level, tileX, tileY, 0);
                }

            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), (e) -> {
            GenerationTools.spawnRandomPreset(this, new RandomRuinsPreset(ig.random), false, false, ig.random, false, 40, 1);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig), (e) -> {
            ig.spawnMobHerds("sheep", ig.random.getIntBetween(25, 45), grassTile, 2, 6, islandSize);
            ig.spawnMobHerds("cow", ig.random.getIntBetween(15, 35), grassTile, 2, 6, islandSize);
        });
        GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, islandSize, ig));
        GenerationTools.checkValid(this);
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
    }
}
