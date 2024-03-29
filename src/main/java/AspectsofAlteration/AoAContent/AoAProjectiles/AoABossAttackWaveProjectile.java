package AspectsofAlteration.AoAContent.AoAProjectiles;

import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoABossAttackWaveProjectile extends Projectile {
    public AoABossAttackWaveProjectile() {
    }

    public AoABossAttackWaveProjectile(float x, float y, float angle, float speed, int distance, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.speed = 100;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
    }

    public void init() {
        super.init();
        this.height = 16.0F;
        this.isSolid = false;
        this.givesLight = true;
        this.setWidth(8.0F);
        this.particleDirOffset = -30.0F;
        this.particleRandomOffset = 3.0F;
        this.piercing = 10;
    }

    public Color getParticleColor() {
        return new Color(37, 255, 0);
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(60, 122, 17), 40.0F, 500, 16.0F);
    }

    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 1;
    }

    protected void modifySpinningParticle(ParticleOption particle) {
        super.modifySpinningParticle(particle);
        particle.sizeFades(8, 14);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 32;
            int drawY = camera.getDrawY(this.y);
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 400);
            final TextureDrawOptions options = this.texture.initDraw().sprite(anim, 0, 64, 32).light(light).rotate(this.getAngle(), 32, 0).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 64, 32).light(light).rotate(this.getAngle(), 32, 0).pos(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }
    }

    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("evilsproj", 3);
    }
}
