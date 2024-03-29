package AspectsofAlteration.AoAContent.AoABiomes;

import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAncientForestCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAncientForestDeepCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAAncientForestSurface;
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
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;

import static AspectsofAlteration.AoAContent.AoABiomes.AoADeadlandBogBiome.surfaceMobs;

public class AoAAncientForestBiome extends Biome {
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static FishingLootTable forestSurfaceFish;
    public static LootItemInterface randomPortalDrop;
    public static LootItemInterface randomShadowGateDrop;
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepSwampCaveMobs;

    public AoAAncientForestBiome() {
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new AoAAncientForestSurface(islandX, islandY, islandSize, server, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAAncientForestCave(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAAncientForestDeepCave(islandX, islandY, dimension, worldEntity);
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? deepCaveCritters : caveCritters;
        } else {
            return super.getCritterSpawnTable(level);
        }
    }
    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepSwampCaveMobs : caveMobs;
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
        caveMobs = (new MobSpawnTable()).include(defaultCaveMobs).add(100, "aoagolemmob").add(100, "aoaarmoredgolemmob").add(60, "swampshooter");
        deepSwampCaveMobs = (new MobSpawnTable()).add(70, "aoagolemmob").add(25, "aoaarmoredgolemmob").add(30, "aoaarmoredswordgolemmob").add(40, "aoaswordgolemmob").add(70, "giantswampslime");
        caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "stonecaveling");
        deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "deepstonecaveling");
        forestSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, "furfish");
        randomPortalDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.01F, "mysteriousportal")});
        randomShadowGateDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "shadowgate")});
    }
}
