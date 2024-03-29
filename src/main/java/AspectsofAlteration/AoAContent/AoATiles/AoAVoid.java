package AspectsofAlteration.AoAContent.AoATiles;

import necesse.engine.Screen;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.LiquidTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.desert.DesertBiome;

import java.awt.*;
import java.util.List;
        import java.awt.Color;
        import java.util.List;
        import necesse.engine.Screen;
        import necesse.engine.registries.BiomeRegistry;
        import necesse.engine.registries.TileRegistry;
        import necesse.engine.sound.SoundEffect;
        import necesse.engine.tickManager.TickManager;
        import necesse.engine.util.GameRandom;
        import necesse.entity.mobs.Mob;
        import necesse.entity.projectile.BombProjectile;
        import necesse.gfx.GameResources;
        import necesse.gfx.camera.GameCamera;
        import necesse.gfx.drawables.LevelSortedDrawable;
        import necesse.gfx.drawables.LevelTileDrawOptions;
        import necesse.gfx.gameTexture.GameTexture;
        import necesse.gfx.gameTexture.GameTextureSection;
        import necesse.level.maps.Level;
        import necesse.level.maps.biomes.desert.DesertBiome;

public class AoAVoid extends LiquidTile {
    public GameTextureSection deepTexture;
    public GameTextureSection shallowTexture;
    protected final GameRandom drawRandom = new GameRandom();

    public AoAVoid() {
        super(new Color(0, 0, 0));
    }

    protected void loadTextures() {
        super.loadTextures();
        this.deepTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/waterdeep"));
        this.shallowTexture = tileTextures.addTexture(GameTexture.fromFile("tiles/watershallow"));
    }

    public void tick(Mob mob, Level level, int x, int y) {
        if (!mob.isFlying() && !mob.isWaterWalking() && level.inLiquid(mob.getX(), mob.getY())) {
            mob.buffManager.removeBuff("onfire", false);
        }

    }

    public void tickValid(Level level, int x, int y, boolean underGeneration) {
        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    GameTile t = level.getTile(x + i, y + j);
                    if (t.isLiquid && t instanceof LavaTile) {
                        if (!underGeneration && level.isClient()) {
                            for(int k = 0; k < 10; ++k) {
                                BombProjectile.spawnFuseParticle(level, (float)(x * 32 + GameRandom.globalRandom.nextInt(33)), (float)(y * 32 + GameRandom.globalRandom.nextInt(33)), 1.0F);
                            }

                            level.lightManager.refreshParticleLight(x, y, 0.0F, 0.3F);
                            Screen.playSound(GameResources.fizz, SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)).volume(0.5F));
                        }

                        level.setTile(x, y, TileRegistry.getTileID("aoavoid"));
                    }
                }
            }
        }

    }

    public Color getLiquidColor(Level level, int x, int y) {
            return this.getLiquidColor(5);
        }


    public Color getMapColor(Level level, int tileX, int tileY) {
        return this.getLiquidColor(level, tileX, tileY);
    }

    protected void addLiquidTopDrawables(LevelTileDrawOptions list, List<LevelSortedDrawable> sortedList, Level level, int tileX, int tileY, GameCamera camera, TickManager tickManager) {
        boolean addBobbing;
        synchronized(this.drawRandom) {
            addBobbing = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).getChance(0.15F);
        }

        if (addBobbing) {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int offset = this.getLiquidBobbing(level, tileX, tileY);
            int xOffset;
            int yOffset;
            GameTextureSection bobbingTexture;
            if (level.liquidManager.getHeight(tileX, tileY) <= -10) {
                xOffset = 0;
                yOffset = offset;
                bobbingTexture = this.deepTexture;
            } else {
                xOffset = offset;
                yOffset = 0;
                bobbingTexture = this.shallowTexture;
            }

            int tile;
            synchronized(this.drawRandom) {
                tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(bobbingTexture.getHeight() / 32);
            }

            list.add(bobbingTexture.sprite(0, tile, 32)).color(this.getLiquidColor(level, tileX, tileY).brighter()).pos(drawX + xOffset, drawY + yOffset - 2);
        }

    }
}
