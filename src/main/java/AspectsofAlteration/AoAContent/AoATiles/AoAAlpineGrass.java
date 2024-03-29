package AspectsofAlteration.AoAContent.AoATiles;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;

import java.awt.*;

public class AoAAlpineGrass extends TerrainSplatterTile {
    public static double growChance = GameMath.getAverageSuccessRuns(7000.0);
    public static double spreadChance = GameMath.getAverageSuccessRuns(850.0);
    private final GameRandom drawRandom;

    public AoAAlpineGrass() {
        super(false, "aoaalpinegrass");
        this.mapColor = new Color(127, 190, 127);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.04F, "aoaalpinegrassseed")});
    }

    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        addSimulateGrow(level, x, y, growChance, ticks, "aoaalpinegrassobject", list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, SimulatePriorityList list, boolean sendChanges) {
        addSimulateGrow(level, tileX, tileY, growChance, ticks, growObjectID, (object, l, x, y, r) -> {
            return object.canPlace(l, x, y, r) == null;
        }, list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, necesse.level.gameTile.GrassTile.CanPlacePredicate canPlace, SimulatePriorityList list, boolean sendChanges) {
        if (level.getObjectID(tileX, tileY) == 0) {
            double runs = Math.max(1.0, GameMath.getRunsForSuccess(growChance, GameRandom.globalRandom.nextDouble()));
            long remainingTicks = (long)((double)ticks - runs);
            if (remainingTicks > 0L) {
                GameObject obj = ObjectRegistry.getObject(ObjectRegistry.getObjectID(growObjectID));
                if (canPlace.check(obj, level, tileX, tileY, 0)) {
                    list.add(tileX, tileY, remainingTicks, () -> {
                        if (canPlace.check(obj, level, tileX, tileY, 0)) {
                            obj.placeObject(level, tileX, tileY, 0);
                            if (sendChanges) {
                                level.sendObjectUpdatePacket(tileX, tileY);
                            }
                        }

                    });
                }
            }
        }

    }

    public double spreadToDirtChance() {
        return spreadChance;
    }

    public void tick(Level level, int x, int y) {
        if (level.isServer()) {
            if (level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(growChance)) {
                GameObject grass = ObjectRegistry.getObject(ObjectRegistry.getObjectID("aoaalpinegrassobject"));
                if (grass.canPlace(level, x, y, 0) == null) {
                    grass.placeObject(level, x, y, 0);
                    level.sendObjectUpdatePacket(x, y);
                }
            }

        }
    }

    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        synchronized(this.drawRandom) {
            tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    public int getTerrainPriority() {
        return 0;
    }

    public interface CanPlacePredicate {
        boolean check(GameObject var1, Level var2, int var3, int var4, int var5);
    }
}
