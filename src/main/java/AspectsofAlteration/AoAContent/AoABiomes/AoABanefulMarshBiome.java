package AspectsofAlteration.AoAContent.AoABiomes;

import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAlpineCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAlpineDeepCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAlpineSurface;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoABanefulMarshSurface;
import necesse.engine.network.server.Server;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class AoABanefulMarshBiome extends Biome {
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static FishingLootTable forestSurfaceFish;
    public static LootItemInterface randomPortalDrop;
    public static LootItemInterface randomShadowGateDrop;

    public AoABanefulMarshBiome() {
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new AoABanefulMarshSurface(islandX, islandY, islandSize, server, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAAlpineCave(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAAlpineDeepCave(islandX, islandY, dimension, worldEntity);
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? deepCaveCritters : caveCritters;
        } else {
            return super.getCritterSpawnTable(level);
        }
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? forestSurfaceFish : super.getFishingLootTable(spot);
    }

    public LootTable getExtraMobDrops(Mob mob) {
        if (mob.isHostile && !mob.isBoss() && !mob.isSummoned) {
            if (mob.getLevel().getIslandDimension() == -1) {
                return new LootTable(new LootItemInterface[]{randomPortalDrop, super.getExtraMobDrops(mob)});
            }

            if (mob.getLevel().getIslandDimension() == -2) {
                return new LootTable(new LootItemInterface[]{randomShadowGateDrop, super.getExtraMobDrops(mob)});
            }
        }

        return super.getExtraMobDrops(mob);
    }

    static {
        caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "stonecaveling");
        deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "deepstonecaveling");
        forestSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, "furfish");
        randomPortalDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.01F, "mysteriousportal")});
        randomShadowGateDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "shadowgate")});
    }
}
