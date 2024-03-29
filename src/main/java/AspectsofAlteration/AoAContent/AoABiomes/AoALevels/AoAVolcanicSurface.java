package AspectsofAlteration.AoAContent.AoABiomes.AoALevels;

import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
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

public class AoAVolcanicSurface extends Level {
    public AoAVolcanicSurface(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAVolcanicSurface(int islandX, int islandY, float islandSize, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 90.0F) + 40;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.getTileID("watertile");
        int sandTile = TileRegistry.getTileID("aoaashengrasstile");
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), (e) -> {
            ig.generateShapedIsland(waterTile, sandTile, -1);
            int rivers = ig.random.getIntBetween(1, 3);

            for(int i = 0; i < rivers && (i <= 0 || !ig.random.getChance(0.4F)); ++i) {
                ig.generateRiver(waterTile, sandTile, -1);
            }

            ig.generateLakes(0.01F, waterTile, sandTile, -1);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateHeights();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), (e) -> {
            ig.generateObjects(ObjectRegistry.getObjectID("sandsurfacerock"), -1, 0.001F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("sandsurfacerocksmall"), -1, 0.002F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerock"), -1, 0.001F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("surfacerocksmall"), -1, 0.002F, false);
            int cactusObject = ObjectRegistry.getObjectID("aoaashtree");
            int grassObject = ObjectRegistry.getObjectID("aoavolcanograssobject");
            ig.generateObjects(cactusObject, sandTile, 0.01F);
            ig.generateObjects(grassObject, sandTile, 0.2F);
            int palmTreeObject = ObjectRegistry.getObjectID("aoavolcanotree");
            ig.generateObjects(palmTreeObject, sandTile, 0.002F);
            GameTile stoneTile = TileRegistry.getTile(TileRegistry.getTileID("rocktile"));
            GameObject stoneObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("rock"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.2F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == sandTile) {
                    if (ig.random.getChance(0.7F)) {
                        stoneTile.placeTile(level, tileX, tileY);
                    }

                    if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.1F) && stoneObject.canPlace(level, tileX, tileY, 0) == null) {
                        stoneObject.placeObject(level, tileX, tileY, 0);
                    }
                }

            });
            GameTile volcanostoneTile = TileRegistry.getTile(TileRegistry.getTileID("aoabasaltstonetile"));
            GameObject volcanostoneObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoabasaltstone"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.2F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == sandTile) {
                    if (ig.random.getChance(0.7F)) {
                        volcanostoneTile.placeTile(level, tileX, tileY);
                    }

                    if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.4F) && volcanostoneObject.canPlace(level, tileX, tileY, 0) == null) {
                        volcanostoneObject.placeObject(level, tileX, tileY, 0);
                    }
                }

            });
            GameTile lavaTile = TileRegistry.getTile(TileRegistry.getTileID("lavatile"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.2F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == sandTile) {
                    if (ig.random.getChance(0.7F)) {
                        lavaTile.placeTile(level, tileX, tileY);
                    }
                }

            });
            ig.generateFruitGrowerSingle("aoavolcanotree", 0.03F, new int[]{sandTile});
            GameObject waterPlant = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoavolcanicwatergrass"));
            GenerationTools.generateRandomVeins(this, ig.random, 0.15F, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.3F) && waterPlant.canPlace(level, tileX, tileY, 0) == null && level.liquidManager.isFreshWater(tileX, tileY)) {
                    waterPlant.placeObject(level, tileX, tileY, 0);
                }

            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), (e) -> {
            GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(ig.random)).setTiles(new String[]{"aoabasaltslabtile", "aoabasaltfinetileddarkfloor"}).setWalls(new String[]{"obsidianwall", "obsidianwall"}), false, false, ig.random, false, 40, 1);
        });
        GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, islandSize, ig));
        GenerationTools.checkValid(this);
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
    }
}
