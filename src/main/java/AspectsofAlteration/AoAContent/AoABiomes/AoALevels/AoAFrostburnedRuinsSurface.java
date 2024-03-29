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

public class AoAFrostburnedRuinsSurface extends Level {
    public AoAFrostburnedRuinsSurface(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAFrostburnedRuinsSurface(int islandX, int islandY, float islandSize, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 100.0F) + 50;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.iceID;
        int snowTile = TileRegistry.getTileID("aoafrostbornsnowtile");
        int iceTile = TileRegistry.iceID;
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), (e) -> {
            ig.generateShapedIsland(waterTile, snowTile, iceTile);
            int rivers = ig.random.getIntBetween(1, 5);

            for(int i = 0; i < rivers && (i <= 0 || !ig.random.getChance(0.4F)); ++i) {
                ig.generateRiver(waterTile, snowTile, iceTile);
            }

            ig.generateLakes(0.02F, waterTile, snowTile, iceTile);
            ig.clearTinyIslands(waterTile);
            this.liquidManager.calculateHeights();
        });
        GameTile stoneTile = TileRegistry.getTile(TileRegistry.getTileID("deepsnowstonebrickfloor"));
        GameObject stoneObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepsnowstonecolumn"));
        GameObject stoneWall = ObjectRegistry.getObject(ObjectRegistry.getObjectID("deepsnowstonewall"));
        GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.05F, 4, 4.0F, 20.0F, 3.0F, 10.0F, (level, tileX, tileY) -> {
            if (level.getTileID(tileX, tileY) == snowTile) {
                if (ig.random.getChance(0.7F)) {
                    stoneTile.placeTile(level, tileX, tileY);
                }

                if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.02F) && stoneObject.canPlace(level, tileX, tileY, 0) == null) {
                    stoneObject.placeObject(level, tileX, tileY, 0);
                }
                if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.1F) && stoneWall.canPlace(level, tileX, tileY, 0) == null) {
                    stoneWall.placeObject(level, tileX, tileY, 0);
                }
            }

        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), (e) -> {
            int treeObject = ObjectRegistry.getObjectID("aoafrostborntree");
            int gianttreeObject = ObjectRegistry.getObjectID("aoagiantfrostborntree");
            int grassObject = ObjectRegistry.getObjectID("aoafrostborngrass");
            ig.generateCellMapObjects(0.5F, treeObject, snowTile, 0.08F);
            ig.generateCellMapObjects(0.5F, gianttreeObject, snowTile, 0.05F);
            ig.generateObjects(grassObject, snowTile, 0.4F);
            ig.generateObjects(ObjectRegistry.getObjectID("deepsnowcaverock"), -1, 0.005F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("deepsnowcaverocksmall"), -1, 0.006F, false);
            ig.generateFruitGrowerVeins("wildaoafrostthistleplant", 0.1F, 8, 10, 0.1F, new int[]{snowTile});
            GenerationTools.generateRandomObjectVeinsOnTile(this, ig.random, 0.03F, 6, 12, snowTile, ObjectRegistry.getObjectID("wildiceblossom"), 0.2F, false);
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), (e) -> {
            GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(ig.random)).setTiles(new String[]{"deepsnowstonebrickfloor", "deepsnowstonebrickfloor"}).setWalls(new String[]{"deepsnowstonewall", "deepsnowstonewall"}), false, false, ig.random, false, 40, 1);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandAnimalsEvent(this, islandSize, ig), (e) -> {
            ig.spawnMobHerds("sheep", ig.random.getIntBetween(20, 40), snowTile, 2, 6, islandSize);
            ig.spawnMobHerds("penguin", ig.random.getIntBetween(20, 40), snowTile, 2, 6, islandSize);
            ig.spawnMobHerds("polarbear", ig.random.getIntBetween(5, 10), snowTile, 1, 1, islandSize);
        });
        GameEvents.triggerEvent(new GeneratedIslandAnimalsEvent(this, islandSize, ig));
        GenerationTools.checkValid(this);
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
    }
}
