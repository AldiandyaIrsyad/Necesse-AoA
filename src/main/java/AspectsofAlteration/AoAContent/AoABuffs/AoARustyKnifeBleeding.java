package AspectsofAlteration.AoAContent.AoABuffs;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.OnFireBuff;
import necesse.entity.particle.Particle;

import java.awt.*;
        import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;

import necesse.engine.util.GameRandom;
        import necesse.entity.mobs.Attacker;
        import necesse.entity.mobs.Mob;
        import necesse.entity.mobs.buffs.ActiveBuff;
        import necesse.entity.mobs.buffs.BuffEventSubscriber;
        import necesse.entity.mobs.buffs.BuffModifiers;
        import necesse.entity.particle.Particle.GType;

public class AoARustyKnifeBleeding extends Buff {
    public AoARustyKnifeBleeding() {
        this.canCancel = false;
        this.isImportant = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.FROST_DAMAGE_FLAT, 20.0F);
    }

        public void clientTick(ActiveBuff buff) {
            super.clientTick(buff);
            if (buff.owner.isVisible()) {
                Mob owner = buff.owner;
                AtomicReference<Float> currentAngle = new AtomicReference(GameRandom.globalRandom.nextFloat() * 360.0F);
                float distance = 20.0F;
                owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin((Float)currentAngle.get()) * distance, owner.y + GameMath.cos((Float)currentAngle.get()) * distance * 0.75F, GType.CRITICAL).color(new Color(143, 14, 0)).height(0.0F).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    float angle = (Float)currentAngle.accumulateAndGet(delta * 150.0F / 250.0F, Float::sum);
                    float distY = distance * 0.75F;
                    pos.x = owner.x + GameMath.sin(angle) * distance;
                    pos.y = owner.y + GameMath.cos(angle) * distY * 0.75F;
                }).lifeTime(1000).sizeFades(16, 24);
            }

        }



    public Attacker getSource(Attacker source) {
        return new OnFireBuff.FireAttacker(source);
    }
}
