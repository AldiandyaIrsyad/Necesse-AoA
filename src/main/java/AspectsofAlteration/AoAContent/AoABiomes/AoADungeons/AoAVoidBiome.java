package AspectsofAlteration.AoAContent.AoABiomes.AoADungeons;

import necesse.engine.AbstractMusicList;
import necesse.engine.MusicList;
import necesse.engine.registries.MusicRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.biomes.forest.ForestBiome;
        import necesse.engine.AbstractMusicList;
        import necesse.engine.MusicList;
        import necesse.engine.registries.MusicRegistry;
        import necesse.entity.mobs.PlayerMob;
        import necesse.gfx.GameMusic;
        import necesse.level.maps.Level;
        import necesse.level.maps.biomes.MobSpawnTable;
        import necesse.level.maps.biomes.forest.ForestBiome;

public class AoAVoidBiome extends ForestBiome {
    public static MobSpawnTable templeMobs = (new MobSpawnTable()).add(50, "ancientskeleton").add(50, "ancientarmoredskeleton").add(40, "ancientskeletonthrower").add(30, "ancientskeletonmage");

    public AoAVoidBiome() {
    }

    public float getSpawnRateMod(Level level) {
        return super.getSpawnRateMod(level) * 0.75F;
    }

    public float getSpawnCapMod(Level level) {
        return super.getSpawnCapMod(level) * 0.75F;
    }

    public MobSpawnTable getMobSpawnTable(Level level) {
        return level.getIslandDimension() == -201 ? new MobSpawnTable() : templeMobs;
    }

    public MobSpawnTable getCritterSpawnTable(Level level) {
        return level.isCave ? defaultCaveCritters : defaultSurfaceCritters;
    }

    public AbstractMusicList getLevelMusic(Level level, PlayerMob perspective) {
        return new MusicList(new GameMusic[]{MusicRegistry.getMusic("fathomless")});
    }
}
