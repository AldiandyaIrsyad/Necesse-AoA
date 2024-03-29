package AspectsofAlteration.AoAContent.AoAProjectiles;

import AspectsofAlteration.AoAContent.AoALevelEvents.AoAIgnusExplosion;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SpideriteWaveGroundWebEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class AoAIgnusWave extends Projectile {
    private double distBuffer;

    public AoAIgnusWave() {
    }

    public AoAIgnusWave(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner) {
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
        this.maxMovePerTick = 32;
        this.height = 0.0F;
        this.isSolid = false;
        this.givesLight = true;
        this.canHitMobs = false;
        this.particleRandomOffset = 10.0F;
        this.particleDirOffset = 0.0F;
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        this.distBuffer += movedDist;
        if (this.isServer()) {
            while(this.distBuffer > 150.0) {
                this.distBuffer -= 150.0;
                AoAIgnusExplosion event = new AoAIgnusExplosion(this.x, this.y, 100, getDamage(), false, 0, getOwner());
                this.getLevel().entityManager.addLevelEvent(event);
            }
        }

    }

    public Color getParticleColor() {
        return new Color(255, 105, 0);
    }

    public Trail getTrail() {
        return null;
    }

    protected void modifySpinningParticle(ParticleOption particle) {
        super.modifySpinningParticle(particle);
        particle.sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).sizeFades(50, 100).givesLight(50.0F, 0.2F);
    }

    protected int getExtraSpinningParticles() {
        return 10;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}
