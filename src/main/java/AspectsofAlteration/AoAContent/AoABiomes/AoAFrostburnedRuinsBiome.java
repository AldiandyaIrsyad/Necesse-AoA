package AspectsofAlteration.AoAContent.AoABiomes;

import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAFrostburnedRuinsCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAFrostburnedRuinsDeepCave;
import AspectsofAlteration.AoAContent.AoABiomes.AoALevels.AoAFrostburnedRuinsSurface;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.network.server.Server;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowSurfaceLevel;

import java.awt.*;

public class AoAFrostburnedRuinsBiome extends Biome {
    public static FishingLootTable snowSurfaceFish;
    public static MobSpawnTable surfaceMobs;
    public static MobSpawnTable caveMobs;
    public static MobSpawnTable deepSnowCaveMobs;
    public static MobSpawnTable surfaceCritters;
    public static MobSpawnTable caveCritters;
    public static MobSpawnTable deepCaveCritters;
    public static LootItemInterface randomRoyalEggDrop;
    public static LootItemInterface randomIceCrownDrop;

    public AoAFrostburnedRuinsBiome() {
    }

    protected void loadRainTexture() {
        this.rainTexture = GameTexture.fromFile("snowfall");
    }

    public Color getRainColor(Level level) {
        return new Color(255, 255, 255, 200);
    }

    public void tickRainEffect(GameCamera camera, Level level, int tileX, int tileY, float rainAlpha) {
    }

    public GameSound getRainSound(Level level) {
        return null;
    }

    public Level getNewSurfaceLevel(int islandX, int islandY, float islandSize, Server server, WorldEntity worldEntity) {
        return new AoAFrostburnedRuinsSurface(islandX, islandY, islandSize, worldEntity);
    }

    public Level getNewCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAFrostburnedRuinsCave(islandX, islandY, dimension, worldEntity);
    }

    public Level getNewDeepCaveLevel(int islandX, int islandY, int dimension, Server server, WorldEntity worldEntity) {
        return new AoAFrostburnedRuinsDeepCave(islandX, islandY, dimension, worldEntity);
    }

    public FishingLootTable getFishingLootTable(FishingSpot spot) {
        return !spot.tile.level.isCave ? snowSurfaceFish : super.getFishingLootTable(spot);
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        if (!level.isCave) {
            return surfaceMobs;
        } else {
            return level.getIslandDimension() == -2 ? deepSnowCaveMobs : caveMobs;
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
                return new LootTable(new LootItemInterface[]{randomRoyalEggDrop, super.getExtraMobDrops(mob)});
            }

            if (mob.getLevel().getIslandDimension() == -2) {
                return new LootTable(new LootItemInterface[]{randomIceCrownDrop, super.getExtraMobDrops(mob)});
            }
        }

        return super.getExtraMobDrops(mob);
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        if (level.isCave) {
            return level.getIslandDimension() == -2 ? new MusicList(new GameMusic[]{MusicRegistry.getMusic("crypt")}) : new MusicList(new GameMusic[]{MusicRegistry.getMusic("catacomb")});
        } else {
            return level.getWorldEntity().isNight() ? new MusicList(new GameMusic[]{MusicRegistry.getMusic("frozenwarfield")}) : new MusicList(new GameMusic[]{MusicRegistry.getMusic("silentcold")});
        }
    }

    static {
        snowSurfaceFish = (new FishingLootTable(defaultSurfaceFish)).addWater(120, "icefish");
        surfaceMobs = (new MobSpawnTable()).include(defaultSurfaceMobs).add(80, "aoawisp").add(50, "aoaundeadwarrior").add(50, "aoaundeadknight").add(50, "aoaundeadarcher");
        caveMobs = (new MobSpawnTable()).add(50, "aoaundeadwarrior").add(50, "aoaundeadknight").add(50, "aoaundeadarcher");
        deepSnowCaveMobs = (new MobSpawnTable()).add(50, "aoaundeadwarrior").add(50, "aoaundeadknight").add(50, "aoaundeadarcher").add(20, "aoafrostbornmage").add(20, "aoafrostbornchampion");
        surfaceCritters = (new MobSpawnTable()).add(100, "snowhare").add(60, "bluebird").add(20, "bird").add(60, "duck");
        caveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "snowstonecaveling");
        deepCaveCritters = (new MobSpawnTable()).include(Biome.defaultCaveCritters).add(100, "deepsnowstonecaveling");
        randomRoyalEggDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.005F, "royalegg")});
        randomIceCrownDrop = new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.004F, "icecrown")});
    }
}
