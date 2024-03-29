package AspectsofAlteration.AoAContent.AoABiomes.AoADungeons;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldGenerator;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.furniturePresets.*;
import necesse.level.maps.presets.set.FurnitureSet;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class AoAVoidLevel extends Level {
    public static int PADDING_TILES = 50;
    public static int CORRIDOR_MIN_WIDTH = 9;
    public static int CORRIDOR_MAX_WIDTH = 11;
    public static int LOOT_ROOM_CORRIDOR_MIN_WIDTH = 6;
    public static int LOOT_ROOM_CORRIDOR_MAX_WIDTH = 8;
    public static int LOOT_ROOM_MIN_SIZE = 15;
    public static int LOOT_ROOM_MAX_SIZE = 20;
    public static ArrayList<AoAVoidLevel.TempleLayout> layouts = new ArrayList();

    public static Level generateNew(int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        AoAVoidLevel.TempleNode first = generateLayout(islandX, islandY);
        Rectangle bounds = getSize(first);
        GameRandom r = new GameRandom(WorldGenerator.getSeed(islandX, islandY));
        AoAVoidLevel level = new AoAVoidLevel(bounds.width + PADDING_TILES * 2, bounds.height + PADDING_TILES * 2, islandX, islandY, dimension, worldEntity);
        level.generateTemple(first, r, -bounds.x + PADDING_TILES, -bounds.y + PADDING_TILES);
        return level;
    }

    public AoAVoidLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    private AoAVoidLevel(int width, int height, int islandX, int islandY, int dimension, WorldEntity worldEntity) {
        super(new LevelIdentifier(islandX, islandY, dimension), width, height, worldEntity);
        this.isCave = true;
    }

    public GameMessage getSetSpawnError(int x, int y, ServerClient client) {
        return new LocalMessage("misc", "spawndungeon");
    }

    public LootTable getCrateLootTable() {
        return LootTablePresets.desertDeepCrate;
    }

    protected void generateTemple(AoAVoidLevel.TempleNode node, GameRandom random, int xOffset, int yOffset) {
        node = node.first;
        int sandBrickID = TileRegistry.getTileID("sandbrick");
        int woodFloorID = TileRegistry.getTileID("aoavoid");
        int deepSandstoneWall = ObjectRegistry.getObjectID("air");

        int currentWidth;
        for(currentWidth = 0; currentWidth < this.width; ++currentWidth) {
            for(int y = 0; y < this.height; ++y) {
                if (random.getChance(0.75F)) {
                    this.setTile(currentWidth, y, sandBrickID);
                } else {
                    this.setTile(currentWidth, y, woodFloorID);
                }

                this.setObject(currentWidth, y, deepSandstoneWall);
            }
        }

        currentWidth = random.getIntBetween(CORRIDOR_MIN_WIDTH, CORRIDOR_MAX_WIDTH);
        LinesGeneration lg = new LinesGeneration(node.tileX + xOffset, node.tileY + yOffset, (float)currentWidth);

        for(LinesGeneration currentLG = lg; node.next != null; node = node.next) {
            AoAVoidLevel.TempleNode next = node.next;
            currentWidth = GameMath.limit(random.getIntOffset(currentWidth, 1), CORRIDOR_MIN_WIDTH, CORRIDOR_MAX_WIDTH);
            currentLG = currentLG.addLineTo(next.tileX + xOffset, next.tileY + yOffset, (float)currentWidth);
            Iterator var12 = next.lootRooms.iterator();

            while(var12.hasNext()) {
                Point lootRoom = (Point)var12.next();
                int lootWidth = GameMath.limit(random.getIntOffset(currentWidth, 1), LOOT_ROOM_CORRIDOR_MIN_WIDTH, LOOT_ROOM_CORRIDOR_MAX_WIDTH);
                currentLG.addLineTo(lootRoom.x + xOffset, lootRoom.y + yOffset, (float)lootWidth);
            }
        }

        CellAutomaton ca = lg.doCellularAutomaton(random);
        ca.cleanHardEdges();
        node = node.first;
        addAliveRoom(ca, node.tileX + xOffset, node.tileY + yOffset, 10);
        AoAVoidLevel.RoomLocation lastRoom = new AoAVoidLevel.RoomLocation(node.tileX + xOffset, node.tileY + yOffset, 10);

        LinkedList lootRooms;
        for(lootRooms = new LinkedList(); node.next != null; node = node.next) {
            AoAVoidLevel.TempleNode next = node.next;
            Iterator var15 = next.lootRooms.iterator();

            while(var15.hasNext()) {
                Point lootRoom = (Point)var15.next();
                int roomSize = random.getIntBetween(LOOT_ROOM_MIN_SIZE, LOOT_ROOM_MAX_SIZE) / 2;
                addAliveRoom(ca, lootRoom.x + xOffset, lootRoom.y + yOffset, roomSize);
                lootRooms.add(new AoAVoidLevel.RoomLocation(lootRoom.x + xOffset, lootRoom.y + yOffset, roomSize));
            }
        }

        addAliveRoom(ca, node.tileX + xOffset, node.tileY + yOffset, 10);
        AoAVoidLevel.RoomLocation latRoom = new AoAVoidLevel.RoomLocation(node.tileX + xOffset, node.tileY + yOffset, 10);
        ca.forEachTile(this, (level, tileX, tileY) -> {
            level.setObject(tileX, tileY, 0);
        });
        ca.placeEdgeWalls(this, deepSandstoneWall, true);
        ca.forEachTile(this, (l, tileX, tileY) -> {
            if (random.getChance(0.02F)) {
                GameObject breakObject = ObjectRegistry.getObject((String)random.getOneOf(new String[]{"crate", "vase"}));
                if (breakObject.canPlace(l, tileX, tileY, 0) == null) {
                    breakObject.placeObject(l, tileX, tileY, 0);
                }
            }

        });
        TicketSystemList<Preset> templeFurniture = new TicketSystemList();
        templeFurniture.addObject(100, new BedDresserPreset(FurnitureSet.palm, 2));
        templeFurniture.addObject(100, new BenchPreset(FurnitureSet.palm, 2));
        templeFurniture.addObject(100, new BookshelfClockPreset(FurnitureSet.palm, 2));
        templeFurniture.addObject(100, new BookshelvesPreset(FurnitureSet.palm, 2, 3));
        templeFurniture.addObject(100, new CabinetsPreset(FurnitureSet.palm, 2, 3));
        templeFurniture.addObject(100, new DeskBookshelfPreset(FurnitureSet.palm, 2));
        templeFurniture.addObject(100, new DinnerTablePreset(FurnitureSet.palm, 2));
        templeFurniture.addObject(100, new DisplayStandClockPreset(FurnitureSet.palm, 2, random, (LootTable)null, new Object[0]));
        templeFurniture.addObject(100, new ModularDinnerTablePreset(FurnitureSet.palm, 2, 1));
        templeFurniture.addObject(100, new ModularTablesPreset(FurnitureSet.palm, 2, 2, true));
        ca.placeFurniturePresets(templeFurniture, 0.4F, this, random);
        generateColumns(this, random, lastRoom.x, lastRoom.y, 4, lastRoom.radius - lastRoom.radius / 3, FurnitureSet.palm.candelabra);
        generateColumns(this, random, latRoom.x, latRoom.y, 4, latRoom.radius - latRoom.radius / 3, FurnitureSet.palm.candelabra);
        ObjectRegistry.getObject("templeentrance").placeObject(this, latRoom.x - 1, latRoom.y, 0);
        int columnID = ObjectRegistry.getObjectID("deepsandstonecolumn");
        AtomicInteger lootRotation = new AtomicInteger();
        Iterator var18 = lootRooms.iterator();

        while(var18.hasNext()) {
            AoAVoidLevel.RoomLocation room = (AoAVoidLevel.RoomLocation)var18.next();
            generateColumns(this, random, room.x, room.y, random.getIntBetween(3, 5), room.radius - room.radius / 3, columnID);
            this.setObject(room.x, room.y, FurnitureSet.palm.chest, 2);
            LootTablePresets.templeChest.applyToLevel(random, (Float)this.buffManager.getModifier(LevelModifiers.LOOT), this, room.x, room.y, new Object[]{this, lootRotation});
        }

    }

    protected static void addAliveRoom(CellAutomaton ca, int centerX, int centerY, int radius) {
        for(int x = centerX - radius; x <= centerX + radius; ++x) {
            for(int y = centerY - radius; y <= centerY + radius; ++y) {
                if ((new Point(centerX, centerY)).distance((double)x, (double)y) <= (double)((float)radius + 0.5F)) {
                    ca.setAlive(x, y);
                }
            }
        }

    }

    protected static void generateColumns(Level level, GameRandom random, int centerX, int centerY, int columns, int range, int object) {
        int anglerPer = 360 / columns;
        int columnAngle = random.nextInt(360);

        for(int i = 0; i < columns; ++i) {
            columnAngle += random.getIntOffset(anglerPer, anglerPer / 5);
            Point2D.Float columnDir = GameMath.getAngleDir((float)columnAngle);
            level.setObject(centerX + (int)(columnDir.x * (float)range), centerY + (int)(columnDir.y * (float)range), object);
        }

    }

    protected static AoAVoidLevel.TempleNode generateLayout(int islandX, int islandY) {
        GameRandom r = new GameRandom(WorldGenerator.getSeed(islandX, islandY));
        AoAVoidLevel.TempleLayout layout = (AoAVoidLevel.TempleLayout)r.getOneOf(layouts);
        AoAVoidLevel.TempleNode first = new AoAVoidLevel.TempleNode(0, 0);
        layout.generate(first, r);
        if (r.nextBoolean()) {
            first = first.mirrorX();
        }

        if (r.nextBoolean()) {
            first = first.mirrorY();
        }

        PresetRotation rotation = (PresetRotation)r.getOneOf(new PresetRotation[]{PresetRotation.CLOCKWISE, PresetRotation.HALF_180, PresetRotation.ANTI_CLOCKWISE, null});
        if (rotation != null) {
            first = first.rotate(rotation);
        }

        return first;
    }

    public static Point getEntranceSpawnPos(int islandX, int islandY) {
        AoAVoidLevel.TempleNode first = generateLayout(islandX, islandY);
        Rectangle bounds = getSize(first);
        return new Point(PADDING_TILES - bounds.x, PADDING_TILES - bounds.y);
    }

    protected static Rectangle getSize(AoAVoidLevel.TempleNode node) {
        node = node.first;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        int maxY;
        for(maxY = Integer.MIN_VALUE; node != null; node = node.next) {
            if (minX > node.tileX) {
                minX = node.tileX;
            }

            if (minY > node.tileY) {
                minY = node.tileY;
            }

            if (maxX < node.tileX) {
                maxX = node.tileX;
            }

            if (maxY < node.tileY) {
                maxY = node.tileY;
            }

            Iterator var5 = node.lootRooms.iterator();

            while(var5.hasNext()) {
                Point lootRoom = (Point)var5.next();
                if (minX > lootRoom.x) {
                    minX = lootRoom.x;
                }

                if (minY > lootRoom.y) {
                    minY = lootRoom.y;
                }

                if (maxX < lootRoom.x) {
                    maxX = lootRoom.x;
                }

                if (maxY < lootRoom.y) {
                    maxY = lootRoom.y;
                }
            }
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    static {
        layouts.add((start, r) -> {
            int minSectionDistance = 50;
            int maxSectionDistance = 60;
            int minRoomDistance = 20;
            int maxRoomDistance = 30;
            start.nextAngle(r, 70.0F, 110.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, 150.0F, 210.0F, (float)minRoomDistance, (float)maxRoomDistance).nextAngle(r, -20.0F, 20.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, 250.0F, 380.0F, (float)minRoomDistance, (float)maxRoomDistance).nextAngle(r, 70.0F, 110.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, -20.0F, 90.0F, (float)minRoomDistance, (float)maxRoomDistance).nextAngle(r, 170.0F, 190.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, 250.0F, 290.0F, (float)(minRoomDistance * 2) / 3.0F, (float)(maxRoomDistance * 2) / 3.0F).nextAngle(r, 170.0F, 190.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, 170.0F, 280.0F, (float)minRoomDistance, (float)maxRoomDistance).nextAngle(r, 70.0F, 110.0F, (float)minSectionDistance, (float)maxSectionDistance).lootRoomAngle(r, 70.0F, 190.0F, (float)minRoomDistance, (float)maxRoomDistance).nextAngle(r, -20.0F, 20.0F, (float)minSectionDistance, (float)maxSectionDistance).nextAngle(r, -10.0F, 120.0F, (float)minRoomDistance, (float)maxRoomDistance);
        });
    }

    protected static class RoomLocation extends Point {
        public int radius;

        public RoomLocation(int x, int y, int radius) {
            super(x, y);
            this.radius = radius;
        }
    }

    public static class TempleNode {
        protected final int tileX;
        protected final int tileY;
        protected LinkedList<Point> lootRooms = new LinkedList();
        protected AoAVoidLevel.TempleNode first;
        protected AoAVoidLevel.TempleNode prev;
        protected AoAVoidLevel.TempleNode next;

        protected TempleNode(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.first = this;
        }

        protected AoAVoidLevel.TempleNode mirrorX() {
            AoAVoidLevel.TempleNode last = null;

            for(AoAVoidLevel.TempleNode current = this.first; current != null; current = current.next) {
                AoAVoidLevel.TempleNode next = new AoAVoidLevel.TempleNode(-current.tileX, current.tileY);
                if (last != null) {
                    last.next = next;
                    next.first = last.first;
                }

                next.prev = last;
                Iterator var4 = current.lootRooms.iterator();

                while(var4.hasNext()) {
                    Point lootRoom = (Point)var4.next();
                    next.lootRooms.add(new Point(-lootRoom.x, lootRoom.y));
                }

                last = next;
            }

            return last == null ? null : last.first;
        }

        protected AoAVoidLevel.TempleNode mirrorY() {
            AoAVoidLevel.TempleNode last = null;

            for(AoAVoidLevel.TempleNode current = this.first; current != null; current = current.next) {
                AoAVoidLevel.TempleNode next = new AoAVoidLevel.TempleNode(current.tileX, -current.tileY);
                if (last != null) {
                    last.next = next;
                    next.first = last.first;
                }

                next.prev = last;
                Iterator var4 = current.lootRooms.iterator();

                while(var4.hasNext()) {
                    Point lootRoom = (Point)var4.next();
                    next.lootRooms.add(new Point(lootRoom.x, -lootRoom.y));
                }

                last = next;
            }

            return last == null ? null : last.first;
        }

        protected AoAVoidLevel.TempleNode rotate(int rightAngles) {
            return this.rotate(PresetRotation.toRotationAngle(rightAngles));
        }

        protected AoAVoidLevel.TempleNode rotate(PresetRotation rotation) {
            if (rotation == null) {
                return this.first;
            } else {
                AoAVoidLevel.TempleNode last = null;

                for(AoAVoidLevel.TempleNode current = this.first; current != null; current = current.next) {
                    Point nextTile = PresetUtils.getRotatedPoint(current.tileX, current.tileY, 0, 0, rotation);
                    AoAVoidLevel.TempleNode next = new AoAVoidLevel.TempleNode(nextTile.x, nextTile.y);
                    if (last != null) {
                        last.next = next;
                        next.first = last.first;
                    }

                    next.prev = last;
                    Iterator var6 = current.lootRooms.iterator();

                    while(var6.hasNext()) {
                        Point lootRoom = (Point)var6.next();
                        Point nextRoom = PresetUtils.getRotatedPoint(lootRoom.x, lootRoom.y, 0, 0, rotation);
                        next.lootRooms.add(new Point(nextRoom.x, nextRoom.y));
                    }

                    last = next;
                }

                return last == null ? null : last.first;
            }
        }

        public AoAVoidLevel.TempleNode lootRoom(int deltaX, int deltaY) {
            this.lootRooms.add(new Point(this.tileX + deltaX, this.tileY + deltaY));
            return this;
        }

        public AoAVoidLevel.TempleNode lootRoomAngle(float angle, float distance) {
            Point2D.Float dir = GameMath.getAngleDir(angle);
            return this.lootRoom((int)(dir.x * distance), (int)(dir.y * distance));
        }

        public AoAVoidLevel.TempleNode lootRoomAngle(GameRandom random, float minAngle, float maxAngle, float minDistance, float maxDistance) {
            return this.lootRoomAngle(random.getFloatBetween(minAngle, maxAngle), random.getFloatBetween(minDistance, maxDistance));
        }

        public AoAVoidLevel.TempleNode next(int deltaX, int deltaY) {
            this.next = new AoAVoidLevel.TempleNode(this.tileX + deltaX, this.tileY + deltaY);
            this.next.prev = this;
            this.next.first = this.first;
            return this.next;
        }

        public AoAVoidLevel.TempleNode nextAngle(float angle, float distance) {
            Point2D.Float dir = GameMath.getAngleDir(angle);
            return this.next((int)(dir.x * distance), (int)(dir.y * distance));
        }

        public AoAVoidLevel.TempleNode nextAngle(GameRandom random, float minAngle, float maxAngle, float minDistance, float maxDistance) {
            return this.nextAngle(random.getFloatBetween(minAngle, maxAngle), random.getFloatBetween(minDistance, maxDistance));
        }
    }

    @FunctionalInterface
    public interface TempleLayout {
        void generate(AoAVoidLevel.TempleNode var1, GameRandom var2);
    }
}
