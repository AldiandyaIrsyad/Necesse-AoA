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

public class AoADeadlandBogSurface extends Level {
    public AoADeadlandBogSurface(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoADeadlandBogSurface(int islandX, int islandY, float islandSize, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, 0), 300, 300, worldEntity);
        this.generateLevel(islandSize);
    }

    public void generateLevel(float islandSize) {
        int size = (int)(islandSize * 100.0F) + 20;
        IslandGeneration ig = new IslandGeneration(this, size);
        int waterTile = TileRegistry.waterID;
        int grassTile = TileRegistry.getTileID("aoaboggrasstile");
        GameEvents.triggerEvent(new GenerateIslandLayoutEvent(this, islandSize, ig), (e) -> {
            for(int i = 0; i < 25; ++i) {
                ig.generateRandomCellIsland(ig.random.getIntBetween(10, 40), ig.random.getIntBetween(50, this.width - 50), ig.random.getIntBetween(50, this.height - 50));
            }

            ig.cellMap = GenerationTools.doCellularAutomaton(ig.cellMap, this.width, this.height, 5, 4, false, 4);
            ig.updateCellMap(grassTile, waterTile);
            GenerationTools.smoothTile(this, grassTile);
            this.liquidManager.calculateHeights();
        });
        GameEvents.triggerEvent(new GeneratedIslandLayoutEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandFloraEvent(this, islandSize, ig), (e) -> {
            GameTile mudTile = TileRegistry.getTile(TileRegistry.mudID);
            GameObject MushroomGrass = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoamushroomgrassobject"));
            GameObject wildMushroom = ObjectRegistry.getObject(ObjectRegistry.getObjectID("mushroom"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.1F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == grassTile) {
                    if (ig.random.getChance(0.7F)) {
                        mudTile.placeTile(level, tileX, tileY);
                    }

                    if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.8F) && MushroomGrass.canPlace(level, tileX, tileY, 0) == null) {
                        MushroomGrass.placeObject(level, tileX, tileY, 0);
                    }
                    if (this.getObjectID(tileX, tileY) == 0 && ig.random.getChance(0.2F) && wildMushroom.canPlace(level, tileX, tileY, 0) == null) {
                        wildMushroom.placeObject(level, tileX, tileY, 0);
                    }
                }

            });
            GameTile bogstone = TileRegistry.getTile(TileRegistry.getTileID("aoabogstonetile"));
            GenerationTools.generateRandomSmoothVeins(this, ig.random, 0.1F, 4, 4.0F, 7.0F, 3.0F, 5.0F, (level, tileX, tileY) -> {
                if (level.getTileID(tileX, tileY) == grassTile) {
                    if (ig.random.getChance(0.7F)) {
                        bogstone.placeTile(level, tileX, tileY);
                    }}});
            int willowTree = ObjectRegistry.getObjectID("willowtree");
            int deadTree = ObjectRegistry.getObjectID("aoapetrifiedtree");
            int grassObject = ObjectRegistry.getObjectID("aoatallboggrassobject");
            ig.generateCellMapObjects(0.35F, willowTree, grassTile, 0.08F);
            ig.generateCellMapObjects(0.35F, deadTree, grassTile, 0.08F);
            ig.generateObjects(grassObject, grassTile, 0.4F);
            ig.generateObjects(ObjectRegistry.getObjectID("swampsurfacerock"), -1, 0.004F, false);
            ig.generateObjects(ObjectRegistry.getObjectID("swampsurfacerocksmall"), -1, 0.008F, false);
            GameObject cattail = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cattail"));
            GenerationTools.generateRandomVeins(this, ig.random, 0.2F, 12, 20, (level, tileX, tileY) -> {
                if (ig.random.getChance(0.3F) && cattail.canPlace(level, tileX, tileY, 0) == null) {
                    cattail.placeObject(level, tileX, tileY, 0);
                }

            });
        });
        GameEvents.triggerEvent(new GeneratedIslandFloraEvent(this, islandSize, ig));
        GameEvents.triggerEvent(new GenerateIslandStructuresEvent(this, islandSize, ig), (e) -> {
            GenerationTools.spawnRandomPreset(this, (new RandomRuinsPreset(ig.random)).setTiles(new String[]{"swampstonefloor"}).setWalls(new String[]{"swampstonewall"}), false, false, ig.random, false, 40, 1);
        });
        GameEvents.triggerEvent(new GeneratedIslandStructuresEvent(this, islandSize, ig));
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "surface", "biome", this.biome.getLocalization());
    }
}
