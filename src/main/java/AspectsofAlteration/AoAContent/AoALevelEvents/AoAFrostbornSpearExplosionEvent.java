package AspectsofAlteration.AoAContent.AoALevelEvents;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

import java.awt.*;

public class AoAFrostbornSpearExplosionEvent extends ExplosionEvent implements Attacker {
    public AoAFrostbornSpearExplosionEvent() {
        this(0.0F, 0.0F, new GameDamage(100.0F), (Mob)null);
    }

    public AoAFrostbornSpearExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 40, damage, false, 0, owner);
        this.sendCustomData = false;
        this.sendOwnerData = true;
    }

    protected void playExplosionEffects() {
        Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(this.x, this.y).volume(2.0F).pitch(1.3F));
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime) {
        this.level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX, dirY).color(new Color(0, 255, 180)).height(10.0F).givesLight(270.0F, 0.5F).lifeTime(lifeTime);
        this.level.entityManager.addParticle(x, y, Particle.GType.CRITICAL).movesConstant(dirX, dirY).color(new Color(68, 124, 255)).height(10.0F).givesLight(270.0F, 0.5F).lifeTime(lifeTime);
    }
}
