package AspectsofAlteration.AoAContent.AoABiomes;

import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAVolcanicCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAVolcanicDeepCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAVolcanicSurface;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
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
import necesse.level.maps.biomes.MobSpawnTable;

public class AoAVolcanicBiome extends Biome {
    public static MobSpawnTable surfaceMobs;
    public static MobSpawnTable caveVolcanoMobs;
    public static MobSpawnTable deepVolcanoMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static LootItemInterface randomAncientStatueDrop;
    public static LootItemInterface dragonSoulsDrop;

    public AoAVolcanicBiome() {
    }

    public boolean canRain(Level level) {
        return false;
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new AoAVolcanicSurface(islandX, islandY, islandSize, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAVolcanicCave(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAVolcanicDeepCave(islandX, islandY, dimension, worldEntity);
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepVolcanoMobs : caveVolcanoMobs;
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
                return new LootTable(new LootItemInterface[]{randomAncientStatueDrop, super.getExtraMobDrops(mob)});
            }

            if (mob.getLevel().getIslandDimension() == -2) {
                return new LootTable(new LootItemInterface[]{dragonSoulsDrop, super.getExtraMobDrops(mob)});
            }
        }

        return super.getExtraMobDrops(mob);
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.getMusic("heathazard")}) : new MusicList(new GameMusic[]{MusicRegistry.getMusic("heathazard")});
        } else {
            return level.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.getMusic("heathazard")}) : new MusicList(new GameMusic[]{MusicRegistry.getMusic("heathazard")});
        }
    }

    static {
        surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(35, "aoaflamewolf");
        caveVolcanoMobs = (new MobSpawnTable()).add(80, "aoaignuscristalline").add(80, "aoaflamewolf");
        deepVolcanoMobs = (new MobSpawnTable()).add(100, "aoaflamewolf").add(80, "aoaignuscristalline");
        surfaceCritters = (new MobSpawnTable()).add(100, "crab").add(60, "scorpion").add(60, "canarybird").add(20, "bird").add(60, "turtle").add(10, "duck");
        caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "sandstonecaveling");
        deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "deepsandstonecaveling");
        randomAncientStatueDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "ancientstatue")});
        dragonSoulsDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "dragonsouls")});
    }
}
