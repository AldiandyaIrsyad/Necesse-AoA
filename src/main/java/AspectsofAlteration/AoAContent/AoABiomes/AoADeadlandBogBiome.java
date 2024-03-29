package AspectsofAlteration.AoAContent.AoABiomes;

import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoADeadlandBogCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoADeadlandBogDeepCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoADeadlandBogSurface;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class AoADeadlandBogBiome extends Biome {
    public static FishingLootTable swampSurfaceFish;
    public static MobSpawnTable surfaceMobs;
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepSwampCaveMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static LootItemInterface randomSpikedFossilDrop;
    public static LootItemInterface randomDecayingLeafDrop;

    public AoADeadlandBogBiome() {
    }

    public int getRainTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(300, 420);
    }

    public int getDryTimeInSeconds(Level level, GameRandom random) {
        return random.getIntBetween(180, 240);
    }

    public float getAverageRainingPercent(Level level) {
        float averageRainTime = 360.0F;
        float averageDryTime = 210.0F;
        return averageRainTime / (averageRainTime + averageDryTime);
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new AoADeadlandBogSurface(islandX, islandY, islandSize, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoADeadlandBogCave(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoADeadlandBogDeepCave(islandX, islandY, dimension, worldEntity);
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? swampSurfaceFish : super.getFishingLootTable(spot);
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepSwampCaveMobs : caveMobs;
        }
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? deepCaveCritters : caveCritters;
        } else {
            return surfaceCritters;
        }
    }

    public LootTable getExtraMobDrops(Mob mob) {
        if (mob.isHostile && !mob.isBoss() && !mob.isSummoned) {
            if (mob.getLevel().getIslandDimension() == -1) {
                return new LootTable(new LootItemInterface[]{randomSpikedFossilDrop, super.getExtraMobDrops(mob)});
            }

            if (mob.getLevel().getIslandDimension() == -2) {
                return new LootTable(new LootItemInterface[]{randomDecayingLeafDrop, super.getExtraMobDrops(mob)});
            }
        }

        return super.getExtraMobDrops(mob);
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.SwampCavern}) : new MusicList(new GameMusic[]{MusicRegistry.MurkyMire});
        } else {
            return level.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.GatorsLullaby}) : new MusicList(new GameMusic[]{MusicRegistry.WatersideSerenade});
        }
    }

    static {
        swampSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, "swampfish");
        surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(50, "swampzombie").add(50, "swampslime");
        caveMobs = (new MobSpawnTable()).include(defaultCaveMobs).add(100, "swampzombie").add(100, "swampslime").add(60, "swampshooter");
        deepSwampCaveMobs = (new MobSpawnTable()).add(70, "ancientskeleton").add(25, "ancientskeletonthrower").add(30, "swampskeleton").add(40, "swampdweller").add(70, "giantswampslime");
        surfaceCritters = (new MobSpawnTable()).add(100, "swampslug").add(80, "frog").add(40, "bird").add(40, "cardinalbird").add(40, "duck");
        caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "swampstonecaveling").add(100, "frog");
        deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "deepswampstonecaveling").add(100, "frog");
        randomSpikedFossilDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "spikedfossil")});
        randomDecayingLeafDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "decayingleaf")});
    }
}
