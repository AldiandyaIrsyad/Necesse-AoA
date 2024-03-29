package AspectsofAlteration.AoAContent.AoATiles;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAVenomStoneTile extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public AoAVenomStoneTile() {
        super(false, "aoavenomstonetile");
        this.mapColor = new Color(116, 92, 131);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        synchronized(this.drawRandom) {
            tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    public void tick(Mob mob, Level level, int x, int y) {
            mob.addBuff(new ActiveBuff(BuffRegistry.getBuffID("aoaenvenomedbuff"), mob, 10.0F, (Attacker)null), true);

    }

    public int getTerrainPriority() {
        return 0;
    }

}
