package AspectsofAlteration.AoAContent.AoABuffs;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle.GType;

import java.awt.*;

public class AoAEnvenomedBuff extends Buff {
    public AoAEnvenomedBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, 200.0F);
    }

    public void clientTick(ActiveBuff buff) {
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
                owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(new Color(107, 0, 143)).givesLight(0.0F, 0.5F).height(16.0F);
        }

    }

    public Attacker getSource(Attacker source) {
        return new necesse.entity.mobs.buffs.staticBuffs.OnFireBuff.FireAttacker(source);
    }

    public static class FireAttacker implements Attacker {
        Attacker owner;

        public FireAttacker(Attacker owner) {
            this.owner = owner;
        }

        public GameMessage getAttackerName() {
            return (GameMessage)(this.owner != null ? this.owner.getAttackerName() : new LocalMessage("deaths", "aoavenomname"));
        }

        public DeathMessageTable getDeathMessages() {
            return this.getDeathMessages("fire", 3);
        }

        public Mob getFirstAttackOwner() {
            return this.owner != null ? this.owner.getAttackOwner() : null;
        }
    }
}
