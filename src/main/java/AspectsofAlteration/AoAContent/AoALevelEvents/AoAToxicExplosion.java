package AspectsofAlteration.AoAContent.AoALevelEvents;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.BloatedSpiderMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

import java.awt.*;
import java.awt.geom.Point2D;
        import java.awt.Color;
        import java.awt.geom.Point2D;
        import necesse.engine.Screen;
        import necesse.engine.sound.SoundEffect;
        import necesse.engine.util.GameMath;
        import necesse.engine.util.GameRandom;
        import necesse.entity.ParticleTypeSwitcher;
        import necesse.entity.mobs.Attacker;
        import necesse.entity.mobs.GameDamage;
        import necesse.entity.mobs.Mob;
        import necesse.entity.mobs.hostile.BloatedSpiderMob;
        import necesse.entity.particle.Particle;
        import necesse.entity.particle.Particle.GType;
        import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AoAToxicExplosion extends ExplosionEvent implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher;

    public AoAToxicExplosion() {
        this(0.0F, 0.0F, 150, BloatedSpiderMob.explosionDamage, false, 0, (Mob)null);
    }

    public AoAToxicExplosion(float x, float y, int range, GameDamage damage, boolean destructive, int toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.explosionTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL});
    }

    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0F);
    }

    protected void playExplosionEffects() {
        Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.5F).pitch(1.5F));
        this.level.getClient().startCameraShake(this.x, this.y, 300, 40, 3.0F, 3.0F);
    }

    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) * 1.5F;
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        float dx;
        float dy;
        if (GameRandom.globalRandom.getChance(0.7F) && range < 25.0F) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(20, 70);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(10, 60) * 0.8F;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 80).movesFriction(dx * 0.05F, dy * 0.05F, 0.8F).color(new Color(79, 0, 114)).heightMoves(0.0F, 70.0F).lifeTime(lifeTime * 4);
        }

        if (range <= (float)Math.max(this.range - 125, 25)) {
            dx = dirX * (float)GameRandom.globalRandom.getIntBetween(140, 150);
            dy = dirY * (float)GameRandom.globalRandom.getIntBetween(130, 140) * 0.8F;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).movesFriction(dx * 0.05F, dy * 0.05F, 0.8F).color(new Color(148, 95, 208)).heightMoves(0.0F, 10.0F).lifeTime(lifeTime * 3);
        }

        if (GameRandom.globalRandom.getChance(0.5F)) {
            this.level.entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).sizeFades(25, 40).movesConstant(dirX * 0.8F, dirY * 0.8F).flameColor(75.0F).height(10.0F).givesLight(75.0F, 0.5F).onProgress(0.4F, (p) -> {
                Point2D.Float norm = GameMath.normalize(dirX, dirY);
                this.level.entityManager.addParticle(p.x + norm.x * 20.0F, p.y + norm.y * 20.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(dirX, dirY).smokeColor().heightMoves(10.0F, 40.0F).lifeTime(lifeTime);
            }).lifeTime(lifeTime);
        }

    }
}
