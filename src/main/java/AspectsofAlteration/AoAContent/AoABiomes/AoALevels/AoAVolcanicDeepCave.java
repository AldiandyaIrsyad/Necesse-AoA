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
import necesse.level.maps.biomes.desert.DesertSurfaceLevel;
import necesse.level.maps.generationModules.*;
import necesse.level.maps.presets.AncientVultureArenaPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
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
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.AncientVultureArenaPreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class AoAVolcanicDeepCave extends DesertSurfaceLevel {
    public AoAVolcanicDeepCave(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public AoAVolcanicDeepCave(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.isCave = true;
        this.generateLevel();
    }

    public void generateLevel() {
        CaveGeneration cg = new CaveGeneration(this, "aoabasaltstonetile", "aoabasaltstone");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), (e) -> {
            cg.generateLevel();
        });
        int crate = ObjectRegistry.getObjectID("crate");
        int vase = ObjectRegistry.getObjectID("vase");
        int trackObject = ObjectRegistry.getObjectID("minecarttrack");
        LinkedList<Mob> minecartsGenerated = new LinkedList();
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), (e) -> {
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
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02F, 5, 10.0F, 50.0F, 5.0F, 20.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.02F, 2, 2.0F, 10.0F, 2.0F, 4.0F, TileRegistry.getTileID("lavatile"), 1.0F, true);
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverock"), 0.025F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("caverocksmall"), 0.03F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("aoaignuscluster"), 0.015F);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("aoaignusclustersmall"), 0.015F);
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), (e) -> {
            cg.generateOreVeins(0.15F, 3, 5, ObjectRegistry.getObjectID("aoamineralchunkorebasalt"));
            cg.generateOreVeins(0.15F, 7, 10, ObjectRegistry.getObjectID("aoarawignusorebasalt"));
            cg.generateOreVeins(0.05F, 7, 10, ObjectRegistry.getObjectID("aoatungstenbasalt"));
            cg.generateOreVeins(0.05F, 7, 10, ObjectRegistry.getObjectID("aoalifequartzbasalt"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GenerationTools.checkValid(this);
        Iterator var7 = minecartsGenerated.iterator();

        while(var7.hasNext()) {
            Mob mob = (Mob)var7.next();
            if (this.getObjectID(mob.getTileX(), mob.getTileY()) != trackObject) {
                mob.remove();
            }
        }

    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.desertCrate;
    }

    public GameMessage getLocationMessage() {
        return new LocalMessage("biome", "cave", "biome", this.biome.getLocalization());
    }
}
