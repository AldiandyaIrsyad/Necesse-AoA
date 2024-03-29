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
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.swamp.SwampCaveLevel;
import necesse.level.maps.biomes.swamp.SwampSurfaceLevel;
import necesse.level.maps.generationModules.*;
import necesse.level.maps.presets.Preset;
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

public class AoADeadlandBogDeepCave extends SwampCaveLevel {
    public AoADeadlandBogDeepCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoADeadlandBogDeepCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "aoabogstonetile", "aoadeadlandbogrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel();
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("swampcrate");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        LinkedList<Mob> minecartsGenerated = new LinkedList();
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GameTile mudTile = TileRegistry.getTile(TileRegistry.mudID);
            GameObject wildMushroom = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoamushroomgrassobject"));
            GameObject thorns = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoatallboggrassobject"));
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.1F, 5, 4.0F, 10.0F, 3.0F, 5.0F, (cells) -> {
                boolean addThorns = cg.random.getChance(0.3F);
                cells.forEachTile(this, (level, tileX, tileY) -> {
                    mudTile.placeTile(level, tileX, tileY);
                    if (this.getObjectID(tileX, tileY) == 0) {
                        if (addThorns) {
                            if (cg.random.getChance(0.85F) && thorns.canPlace(level, tileX, tileY, 0) == null) {
                                thorns.placeObject(level, tileX, tileY, 0);
                            }
                        } else if (cg.random.getChance(0.05F) && wildMushroom.canPlace(level, tileX, tileY, 0) == null) {
                            wildMushroom.placeObject(level, tileX, tileY, 0);
                        }
                    }

                });
            });
            GameTile nestTile = TileRegistry.getTile(TileRegistry.getTileID("aoaantspidernesttile"));
            GameObject egg1 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoaspiderantegg"));
            GameObject egg2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoaspideranteggcluster"));
            GameObject egg3 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoaspideranteggclustersmall"));
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.3F, 5, 4.0F, 10.0F, 3.0F, 5.0F, (cells) -> {
                boolean addThorns = cg.random.getChance(0.3F);
                cells.forEachTile(this, (level, tileX, tileY) -> {
                    nestTile.placeTile(level, tileX, tileY);
                    if (this.getObjectID(tileX, tileY) == 0) {
                        if (addThorns) {
                            if (cg.random.getChance(0.85F) && egg1.canPlace(level, tileX, tileY, 0) == null) {
                                egg1.placeObject(level, tileX, tileY, 0);
                            }
                        } else if (cg.random.getChance(0.05F) && egg2.canPlace(level, tileX, tileY, 0) == null) {
                            egg2.placeObject(level, tileX, tileY, 0);
                        }
                    } else if (cg.random.getChance(0.05F) && egg3.canPlace(level, tileX, tileY, 0) == null) {
                        egg3.placeObject(level, tileX, tileY, 0);
                    }

                });
            });
            GenerationTools.generateRandomPoints(this, cg.random, 0.01F, 15, (p) -> {
                LinesGeneration lg = new LinesGeneration(p.x, p.y);
                ArrayList<LinesGeneration> tntArms = new ArrayList();
                int armAngle = cg.random.nextInt(4) * 90;
                int arms = cg.random.getIntBetween(3, 10);

                for(int i = 0; i < arms; ++i) {
                    lg = lg.addArm((float)cg.random.getIntOffset(armAngle, 20), (float)cg.random.getIntBetween(5, 25), 3.0F);
                    tntArms.add(lg);
                    int angleChange = (Integer)cg.random.getOneOfWeighted(Integer.class, new Object[]{15, 0, 5, 90, 5, -90});
                    armAngle += angleChange;
                }

                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ca.forEachTile(this, (level, tileX, tileY) -> {
                    if (level.isSolidTile(tileX, tileY)) {
                        level.setObject(tileX, tileY, 0);
                    }

                    if (cg.random.getChance(0.05)) {
                        level.setObject(tileX, tileY, crate);
                    }

                });
            });
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02F, 2, 2.0F, 10.0F, 2.0F, 4.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02F, 2, 2.0F, 10.0F, 2.0F, 4.0F, TileRegistry.getTileID("watertile"), 1.0F, true);
            this.liquidManager.calculateShores();
            GameObject cattail = ObjectRegistry.getObject(ObjectRegistry.getObjectID("cattail"));
            GenerationTools.generateRandomVeins(this, cg.random, 0.5F, 12, 20, (level, tileX, tileY) -> {
                if (cg.random.getChance(0.3F) && cattail.canPlace(level, tileX, tileY, 0) == null) {
                    cattail.placeObject(level, tileX, tileY, 0);
                }

            });
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("swampcaverock"), 0.005F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("swampcaverocksmall"), 0.01F);
            GameObject grassObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoatallboggrassobject"));
            GenerationTools.iterateLevel(this, (x, y) -> {
                return this.getTileID(x, y) == TileRegistry.swampRockID && this.getObjectID(x, y) == 0 && cg.random.getChance(0.6F);
            }, (x, y) -> {
                grassObject.placeObject(this, x, y, 0);
            });
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.5F, 2, 25, ObjectRegistry.getObjectID("air"));
            cg.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("aoasyrilliteoredeadlandstone"));
            cg.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("aoadeadlandbogrockcopper"));
            cg.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("aoadeadlandbogrockgold"));
            cg.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("aoadeadlandbogrockgoldivy"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);

            int lootAreaAmount;
            for(lootAreaAmount = 0; lootAreaAmount < chestRoomAmount; ++lootAreaAmount) {
                Preset chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.swampCaveChest, chestRoomRotation, new ChestRoomSet[]{ChestRoomSet.swampStone, ChestRoomSet.wood});
                chestRoom.replaceTile(TileRegistry.stoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID}));
                chestRoom.replaceTile(TileRegistry.swampStoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.swampStoneFloorID, TileRegistry.swampStoneBrickFloorID}));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }

            lootAreaAmount = cg.random.getIntBetween(5, 10);

            for(int i = 0; i < lootAreaAmount; ++i) {
                Preset lootArea = new RandomLootAreaPreset(cg.random, 15, "swampstonecolumn", new String[]{"swampzombie"});
                presets.findRandomValidPositionAndApply(cg.random, 5, lootArea, 10, true, true);
            }
            cg.generateRandomCrates(0.03F, new int[]{crate});
        });
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        GenerationTools.checkValid(this);
        Iterator var6 = minecartsGenerated.iterator();

        while(var6.hasNext()) {
            Mob mob = (Mob)var6.next();
            if (this.getObjectID(mob.getTileX(), mob.getTileY()) != trackObject) {
                mob.remove();
            }
        }

    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.swampCrate;
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }
}
