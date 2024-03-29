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
import necesse.level.maps.biomes.snow.SnowSurfaceLevel;
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

public class AoAFrostburnedRuinsCave extends SnowSurfaceLevel {
    public AoAFrostburnedRuinsCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAFrostburnedRuinsCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "snowrocktile", "snowrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel();
        });
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        int crate = ObjectRegistry.getObjectID("snowcrate");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        LinkedList<Mob> minecartsGenerated = new LinkedList();
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.04F, 5, 7.0F, 15.0F, 3.0F, 5.0F, (ca) -> {
                ca.forEachTile(this, (level, tileX, tileY) -> {
                    level.setTile(tileX, tileY, TileRegistry.spiderNestID);
                    if (cg.random.getChance(0.95F)) {
                        level.setObject(tileX, tileY, ObjectRegistry.cobWebID);
                    } else {
                        level.setObject(tileX, tileY, 0);
                    }

                });
                ArrayList<Point> possibleEggSpawns = new ArrayList(ca.getAliveUnordered().size());
                int aliveRadius = 3;
                ArrayList<Point> neighboursList = new ArrayList((aliveRadius * 2 + 1) * 2);

                for(int x = -aliveRadius; x <= aliveRadius; ++x) {
                    for(int y = -aliveRadius; y <= aliveRadius; ++y) {
                        if (x != 0 || y != 0) {
                            neighboursList.add(new Point(x, y));
                        }
                    }
                }

                Point[] neighbours = (Point[])neighboursList.toArray(new Point[0]);
                ca.forEachTile(this, (level, tileX, tileY) -> {
                    if (ca.isAllAlive(tileX, tileY, neighbours)) {
                        possibleEggSpawns.add(new Point(tileX, tileY));
                    }

                });
                if (cg.random.getChance(0.5F) && !possibleEggSpawns.isEmpty()) {
                    Point eggTile = (Point)cg.random.getOneOf(possibleEggSpawns);
                    this.setObject(eggTile.x, eggTile.y, ObjectRegistry.getObjectID("royaleggobject"));
                    this.setObject(eggTile.x, eggTile.y + 1, 0);
                }

                ca.spawnMobs(this, cg.random, "blackcavespider", 4, 8, 1, 4);
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
                ArrayList<Point> trackTiles = new ArrayList();
                lg.getRoot().recursiveLines((lg2) -> {
                    GameLinkedList<Point> tiles = new GameLinkedList();
                    LinesGeneration.pathTiles(lg2.getTileLine(), true, (from, nextx) -> {
                        tiles.add(nextx);
                    });
                    Iterator var5 = tiles.elements().iterator();

                    while(var5.hasNext()) {
                        GameLinkedList<Point>.Element el = (GameLinkedList.Element)var5.next();
                        Point current = (Point)el.object;
                        if ((new Rectangle(2, 2, this.width - 4, this.height - 4)).contains(current)) {
                            trackTiles.add(current);
                            GameLinkedList<Point>.Element nextEl = el.next();
                            if (nextEl != null) {
                                Point next = (Point)nextEl.object;
                                if (next.x < current.x) {
                                    this.setObject(current.x, current.y, trackObject, 3);
                                } else if (next.x > current.x) {
                                    this.setObject(current.x, current.y, trackObject, 1);
                                } else if (next.y < current.y) {
                                    this.setObject(current.x, current.y, trackObject, 0);
                                } else if (next.y > current.y) {
                                    this.setObject(current.x, current.y, trackObject, 2);
                                }
                            } else {
                                GameLinkedList<Point>.Element prevEl = el.prev();
                                if (prevEl != null) {
                                    Point prev = (Point)prevEl.object;
                                    if (prev.x < current.x) {
                                        this.setObject(current.x, current.y, trackObject, 1);
                                    } else if (prev.x > current.x) {
                                        this.setObject(current.x, current.y, trackObject, 3);
                                    } else if (prev.y < current.y) {
                                        this.setObject(current.x, current.y, trackObject, 2);
                                    } else if (prev.y > current.y) {
                                        this.setObject(current.x, current.y, trackObject, 0);
                                    }
                                } else {
                                    this.setObject(current.x, current.y, trackObject, 0);
                                }
                            }
                        }
                    }

                    return true;
                });
                int minecartCount = (Integer)cg.random.getOneOfWeighted(Integer.class, new Object[]{100, 0, 200, 1, 50, 2});

                int tntCount;
                int ix;
                for(tntCount = 0; tntCount < minecartCount && !trackTiles.isEmpty(); ++tntCount) {
                    ix = cg.random.nextInt(trackTiles.size());
                    Point nextx = (Point)trackTiles.remove(ix);
                    Mob minecart = MobRegistry.getMob("minecart", this);
                    this.entityManager.addMob(minecart, (float)(nextx.x * 32 + 16), (float)(nextx.y * 32 + 16));
                    minecartsGenerated.add(minecart);
                }

                tntCount = (Integer)cg.random.getOneOfWeighted(Integer.class, new Object[]{100, 0, 200, 1, 50, 2});

                for(ix = 0; ix < tntCount && !tntArms.isEmpty(); ++ix) {
                    int index = cg.random.nextInt(tntArms.size());
                    LinesGeneration next = (LinesGeneration)tntArms.remove(index);
                    int wireLength = cg.random.getIntBetween(10, 14) * (Integer)cg.random.getOneOf(new Integer[]{1, -1});
                    float lineLength = (float)(new Point(next.x1, next.y1)).distance((double)next.x2, (double)next.y2);
                    Point2D.Float dir = GameMath.normalize((float)(next.x1 - next.x2), (float)(next.y1 - next.y2));
                    float linePointLength = cg.random.getFloatBetween(0.0F, lineLength);
                    Point2D.Float linePoint = new Point2D.Float((float)next.x2 + dir.x * linePointLength, (float)next.y2 + dir.y * linePointLength);
                    Point2D.Float leverPoint = GameMath.getPerpendicularPoint(linePoint, 2.0F * Math.signum((float)wireLength), dir);
                    Point2D.Float tntPoint = GameMath.getPerpendicularPoint(linePoint, (float)wireLength, dir);
                    Line2D.Float wireLine = new Line2D.Float(leverPoint, tntPoint);
                    LinkedList<Point> tiles = new LinkedList();
                    LinesGeneration.pathTiles(wireLine, true, (fromTile, nextTile) -> {
                        tiles.add(nextTile);
                    });
                    Iterator var26 = tiles.iterator();

                    Point tile;
                    while(var26.hasNext()) {
                        tile = (Point)var26.next();
                        this.wireManager.setWire(tile.x, tile.y, 0, true);
                        if (this.getObject(tile.x, tile.y).isSolid) {
                            this.setObject(tile.x, tile.y, 0);
                        }
                    }

                    Point first = (Point)tiles.getFirst();
                    tile = (Point)tiles.getLast();
                    this.setObject(first.x, first.y, ObjectRegistry.getObjectID("rocklever"));
                    this.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("tnt"));
                }

            });
            AtomicInteger cryptRotation = new AtomicInteger();
            GameObject cryptGrass = ObjectRegistry.getObject("cryptgrass");
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.0075F, 2, 3.0F, 5.0F, 8.0F, 10.0F, (ca) -> {
                ca.streamAliveOrdered().forEachOrdered((tilexx) -> {
                    cg.addIllegalCrateTile(tilexx.x, tilexx.y);
                    this.setTile(tilexx.x, tilexx.y, TileRegistry.cryptAshID);
                    this.setObject(tilexx.x, tilexx.y, 0);
                });
                ca.placeEdgeWalls(this, WallSet.snowStone.wall, true);
                ArrayList<Point> coffinTiles = new ArrayList();
                ca.streamAliveOrdered().forEachOrdered((tilexx) -> {
                    if (cg.random.getChance(0.2F) && cryptGrass.canPlace(this, tilexx.x, tilexx.y, 0) == null) {
                        cryptGrass.placeObject(this, tilexx.x, tilexx.y, 0);
                    }

                    if (this.getObjectID(tilexx.x, tilexx.y) == 0 && this.getObjectID(tilexx.x - 1, tilexx.y) == 0 && this.getObjectID(tilexx.x + 1, tilexx.y) == 0 && this.getObjectID(tilexx.x, tilexx.y - 1) == 0 && this.getObjectID(tilexx.x, tilexx.y + 1) == 0) {
                        if (cg.random.getChance(0.2F)) {
                            int rotation = cg.random.nextInt(4);
                            Point[] clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1), new Point(0, -2)};
                            if (this.getRelativeAnd(tilexx.x, tilexx.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints), (tileX, tileY) -> {
                                return ca.isAlive(tileX, tileY) && this.getObjectID(tileX, tileY) == 0;
                            })) {
                                ObjectRegistry.getObject(ObjectRegistry.getObjectID("stonecoffin")).placeObject(this, tilexx.x, tilexx.y, rotation);
                                coffinTiles.add(tilexx);
                            }
                        } else if (cg.random.getChance(0.06F)) {
                            this.setObject(tilexx.x, tilexx.y, ObjectRegistry.getObjectID("stonecolumn"));
                        } else if (cg.random.getChance(0.3F)) {
                            cg.random.runOneOf(new Runnable[]{() -> {
                                this.setObject(tilexx.x, tilexx.y, ObjectRegistry.getObjectID("gravestone1"), cg.random.nextInt(4));
                            }, () -> {
                                this.setObject(tilexx.x, tilexx.y, ObjectRegistry.getObjectID("gravestone2"), cg.random.nextInt(4));
                            }});
                        }
                    }

                });
                Point tilex;
                if (!coffinTiles.isEmpty()) {
                    Point tile = (Point)cg.random.getOneOf(coffinTiles);
                    tilex = (Point)cg.random.getOneOf(coffinTiles);
                    LootTablePresets.caveCryptUniqueItems.applyToLevel(cg.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, tile.x, tile.y, new Object[]{this, cryptRotation});
                    LootTablePresets.caveCryptBloodPlateItems.applyToLevel(cg.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, tilex.x, tilex.y, new Object[]{this, cryptRotation});
                }

                Iterator var8 = coffinTiles.iterator();

                while(var8.hasNext()) {
                    tilex = (Point)var8.next();
                    LootTablePresets.caveCryptCoffin.applyToLevel(cg.random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, tilex.x, tilex.y, new Object[]{this, cryptRotation});
                }

                ca.spawnMobs(this, cg.random, "vampire", 25, 45, 1, 4);
            });
            int waterTile = TileRegistry.waterID;
            int iceTile = TileRegistry.iceID;
            GenerationTools.generateRandomSmoothVeinsC(this, cg.random, 0.07F, 2, 2.0F, 10.0F, 3.0F, 5.0F, (cells) -> {
                boolean hasWater = cg.random.getChance(0.5F);
                if (hasWater) {
                    CellAutomaton waterCells = new CellAutomaton();
                    cells.forEachTile(this, (level, tileX, tileY) -> {
                        if (level.getTileID(tileX, tileY) == cg.rockTile) {
                            level.setTile(tileX, tileY, iceTile);
                            waterCells.setAlive(tileX, tileY);
                        }

                    });
                    cells.forEachTile(this, (level, tileX, tileY) -> {
                        if (cells.countDead(tileX, tileY, CellAutomaton.allNeighbours) > 0) {
                            waterCells.setDead(tileX, tileY);
                        }

                    });
                    waterCells.doCellularAutomaton(4, 100, 2);
                    waterCells.forEachTile(this, (level, tileX, tileY) -> {
                        level.setTile(tileX, tileY, waterTile);
                    });
                } else {
                    cells.forEachTile(this, (level, tileX, tileY) -> {
                        if (level.getTileID(tileX, tileY) == cg.rockTile) {
                            level.setTile(tileX, tileY, iceTile);
                        }

                    });
                }

            });
            GenerationTools.iterateLevel(this, (x, y) -> {
                return this.getTileID(x, y) == iceTile;
            }, (x, y) -> {
                for(int i = -1; i <= 1; ++i) {
                    for(int j = -1; j <= 1; ++j) {
                        if (this.getObject(x + i, y + j).isRock) {
                            this.setObject(x + i, y + j, 0);
                        }
                    }
                }

            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("snowcaverock"), 0.005F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("snowcaverocksmall"), 0.01F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("frostshardsnow"));
            cg.generateOreVeins(0.3F, 3, 6, ObjectRegistry.getObjectID("copperoresnow"));
            cg.generateOreVeins(0.25F, 3, 6, ObjectRegistry.getObjectID("ironoresnow"));
            cg.generateOreVeins(0.15F, 3, 6, ObjectRegistry.getObjectID("goldoresnow"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), (e) -> {
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);

            int lootAreaAmount;
            for(lootAreaAmount = 0; lootAreaAmount < chestRoomAmount; ++lootAreaAmount) {
                Preset chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.snowCaveChest, chestRoomRotation, new ChestRoomSet[]{ChestRoomSet.snowStone, ChestRoomSet.ice, ChestRoomSet.wood});
                chestRoom.replaceTile(TileRegistry.stoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.stoneFloorID, TileRegistry.stoneBrickFloorID}));
                chestRoom.replaceTile(TileRegistry.snowStoneFloorID, (Integer)cg.random.getOneOf(new Integer[]{TileRegistry.snowStoneFloorID, TileRegistry.snowStoneBrickFloorID}));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }

            lootAreaAmount = cg.random.getIntBetween(5, 10);

            for(int i = 0; i < lootAreaAmount; ++i) {
                Preset lootArea = new RandomLootAreaPreset(cg.random, 15, "snowstonecolumn", new String[]{"frozendwarf"});
                presets.findRandomValidPositionAndApply(cg.random, 5, lootArea, 10, true, true);
            }

            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);

            for(int ix = 0; ix < caveRuinsCount; ++ix) {
                WallSet wallSet = (WallSet)cg.random.getOneOf(new WallSet[]{WallSet.snowStone, WallSet.wood});
                FurnitureSet furnitureSet = (FurnitureSet)cg.random.getOneOf(new FurnitureSet[]{FurnitureSet.pine, FurnitureSet.spruce});
                String floorStringID = (String)cg.random.getOneOf(new String[]{"woodfloor", "woodfloor", "snowstonefloor", "snowstonebrickfloor"});
                Preset room = ((CaveRuins.CaveRuinGetter)cg.random.getOneOf(CaveRuins.caveRuinGetters)).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.snowCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
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
        return LootTablePresets.snowCrate;
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }
}
