package AspectsofAlteration.AoAContent.AoABuffs;

import necesse.entity.mobs.buffs.*;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
        import necesse.entity.mobs.buffs.ActiveBuff;
        import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class AoASucessiveCombo extends Buff {
    public AoASucessiveCombo() {
        this.isImportant = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ALL_DAMAGE, 0.5F);
    }

    public int getStackSize() {
        return 5;
    }

    public boolean overridesStackDuration() {
        return true;
    }
}
