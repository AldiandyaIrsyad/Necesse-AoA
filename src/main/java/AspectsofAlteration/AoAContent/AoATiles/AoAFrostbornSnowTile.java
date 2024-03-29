package AspectsofAlteration.AoAContent.AoATiles;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;

import java.awt.*;

public class AoAFrostbornSnowTile extends TerrainSplatterTile {
    public static double snowChance = GameMath.getAverageSuccessRuns(2800.0);
    private final GameRandom drawRandom;

    public AoAFrostbornSnowTile() {
        super(false, "snow");
        this.mapColor = new Color(240, 240, 240);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    public void tick(Level level, int x, int y) {
        if (level.isServer()) {
            if (level.biome instanceof SnowBiome && level.rainingLayer.isRaining() && level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(snowChance)) {
                GameObject snow = ObjectRegistry.getObject(ObjectRegistry.getObjectID("snowpile0"));
                if (snow.canPlace(level, x, y, 0) == null) {
                    snow.placeObject(level, x, y, 0);
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
        return 100;
    }
}
