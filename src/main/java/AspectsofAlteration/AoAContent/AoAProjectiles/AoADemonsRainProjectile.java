package AspectsofAlteration.AoAContent.AoAProjectiles;

import AspectsofAlteration.AoAContent.AoALevelEvents.AoAIgnusExplosion;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.TheCrimsonSkyEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class AoADemonsRainProjectile extends Projectile {
    protected boolean isFallingProjectile;
    protected Point2D.Float targetPoints;
    protected GameDamage damage;

    public AoADemonsRainProjectile() {
    }

    public AoADemonsRainProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Point2D.Float targetPoints, boolean isFallingProjectile) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.damage = damage;
        this.knockback = knockback;
        this.targetPoints = targetPoints;
        this.isFallingProjectile = isFallingProjectile;
    }

    public void init() {
        super.init();
        this.height = 40.0F;
        this.piercing = 0;
        this.isSolid = false;
        this.heightBasedOnDistance = true;
        this.trailOffset = -25.0F;
        this.removeIfOutOfBounds = false;
        this.canBreakObjects = false;
        this.setWidth(6.0F, false);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isFallingProjectile);
        if (this.targetPoints != null) {
            writer.putNextBoolean(true);
            writer.putNextFloat(this.targetPoints.x);
            writer.putNextFloat(this.targetPoints.y);
        } else {
            writer.putNextBoolean(false);
        }

    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isFallingProjectile = reader.getNextBoolean();
        if (reader.getNextBoolean()) {
            this.targetPoints = new Point2D.Float(reader.getNextFloat(), reader.getNextFloat());
        } else {
            this.targetPoints = null;
        }

    }

    public boolean canHit(Mob mob) {
        return false;
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isFallingProjectile) {
            Projectile projectile = new AoADemonsRainProjectile(this.getLevel(), this.getOwner(), this.targetPoints.x, this.targetPoints.y - (float)this.distance, this.targetPoints.x, this.targetPoints.y, this.speed, this.distance, this.damage, this.knockback, (Point2D.Float)null, true);
            projectile.getUniqueID((new GameRandom((long)this.getUniqueID())).nextSeeded(67));
            this.getLevel().entityManager.projectiles.addHidden(projectile);
        } else {
            float targetY;
            float targetX;
            if (mob != null) {
                targetX = mob.x;
                targetY = mob.y;
            } else {
                targetX = x;
                targetY = y;
            }

            int lifetime = 200;
            int range = 65;
            if (!this.isServer()) {
                Screen.playSound(GameResources.bowhit, SoundEffect.effect(this));
                Screen.playSound(GameResources.slimesplash, SoundEffect.effect(this));
                FireworksExplosion explosion = new FireworksExplosion(FireworksPath.sphere((float)GameRandom.globalRandom.getIntBetween(range - 10, range)));
                AoAIgnusExplosion explosion2 = new AoAIgnusExplosion(this.x, this.y, 150, getDamage(), false, 0, getOwner());
                explosion.colorGetter = (particle, progress, random) -> {
                    return ParticleOption.randomizeColor(348.0F, 0.94F, 0.8F, 2.0F, 0.0F, 0.0F);
                };
                explosion.trailChance = 0.5F;
                explosion.particles = 40;
                explosion.lifetime = lifetime;
                explosion.popOptions = null;
                explosion.particleLightHue = 0.0F;
                explosion.explosionSound = null;
                explosion.spawnExplosion(this.getLevel(), targetX, targetY, this.getHeight(), GameRandom.globalRandom);

            }

            if (!this.isClient()) {
                Rectangle targetBox = new Rectangle((int)targetX - range, (int)targetY - range, range * 2, range * 2);
                this.streamTargets(this.getOwner(), targetBox).filter((m) -> {
                    return m.canBeHit(this) && m.getDistance(targetX, targetY) <= (float)range;
                }).forEach((m) -> {
                    m.isServerHit(this.getDamage(), m.x - x, m.y - y, (float)this.knockback, this);
                });
            }

            this.getLevel().entityManager.addLevelEventHidden(new AoAIgnusExplosion(this.x, this.y, 150, getDamage(), false, 0, getOwner()));
        }

    }

    public Color getParticleColor() {
        return new Color(234, 91, 14);
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(255, 95, 0), 12.0F, 200, this.getHeight());
        trail.drawOnTop = true;
        return trail;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y);
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
            topList.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
        }
    }
}
