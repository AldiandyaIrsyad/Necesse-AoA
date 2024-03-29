package AspectsofAlteration.AoAContent.AoAWeapons;

import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoALightbringerProjectile extends Projectile {
    public AoALightbringerProjectile() {
    }

    public AoALightbringerProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    public void init() {
        super.init();
        this.piercing = 3;
        this.height = 16.0F;
        this.givesLight = true;
        this.setWidth(55.0F, true);
        this.particleRandomOffset = 14.0F;
    }

    public Color getParticleColor() {
        return new Color(255, 204, 38);
    }

    protected void spawnDeathParticles() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            float height = this.getHeight();

            for(int i = 0; i < 40; ++i) {
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(10, 20).movesConstant((float)(GameRandom.globalRandom.getIntBetween((int)(-this.dy * 30.0F), (int)(this.dy * 30.0F)) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3)), (float)(GameRandom.globalRandom.getIntBetween((int)(-this.dx * 30.0F), (int)(this.dx * 30.0F)) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3))).color(this.getParticleColor()).height(height);
            }
        }

    }

    public Trail getTrail() {
        return null;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 16;
            int drawY = camera.getDrawY(this.y);
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
            final TextureDrawOptions options = this.texture.initDraw().sprite(anim, 0, 32, 128).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32, 128).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }

    }
}
