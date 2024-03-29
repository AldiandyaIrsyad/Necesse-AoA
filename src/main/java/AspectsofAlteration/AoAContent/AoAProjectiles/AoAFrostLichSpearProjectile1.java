package AspectsofAlteration.AoAContent.AoAProjectiles;

import AspectsofAlteration.AoAContent.AoALevelEvents.AoAFrostbornSpearExplosionEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoAFrostLichSpearProjectile1 extends Projectile {
    public AoAFrostLichSpearProjectile1() {
    }

    public AoAFrostLichSpearProjectile1(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance / 2;
        this.setDamage(damage);
        this.knockback = knockback;
        this.givesLight = true;
    }

    public void init() {
        super.init();
        this.height = 18.0F;
        this.piercing = 0;
        this.setWidth(6.0F, false);
    }

    public Color getParticleColor() {
        return new Color(0, 255, 159);
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(103, 255, 230), 12.0F, 200, this.getHeight());
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (this.getLevel().isServerLevel()) {
            AoAFrostbornSpearExplosionEvent event = new AoAFrostbornSpearExplosionEvent(x, y, this.getDamage(), this.getOwner());
            this.getLevel().entityManager.addLevelEvent(event);
        }
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 16;
            int drawY = camera.getDrawY(this.y);
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 400);
            final TextureDrawOptions options = this.texture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }
    }
}
