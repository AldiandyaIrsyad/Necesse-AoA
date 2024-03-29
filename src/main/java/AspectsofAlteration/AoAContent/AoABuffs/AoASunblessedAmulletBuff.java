package AspectsofAlteration.AoAContent.AoABuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
        import necesse.engine.localization.Localization;
        import necesse.engine.registries.BuffRegistry.Debuffs;
        import necesse.entity.mobs.Mob;
        import necesse.entity.mobs.MobWasHitEvent;
        import necesse.entity.mobs.PlayerMob;
        import necesse.entity.mobs.buffs.ActiveBuff;
        import necesse.entity.mobs.buffs.BuffEventSubscriber;
        import necesse.gfx.gameTooltips.ListGameTooltips;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.item.trinketItem.TrinketItem;

public class AoASunblessedAmulletBuff extends TrinketBuff {
    public AoASunblessedAmulletBuff() {
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (!event.wasPrevented && buff.owner.isServer()) {
            Mob attackOwner = event.attacker != null ? event.attacker.getAttackOwner() : null;
            if (attackOwner != null) {
                attackOwner.addBuff(new ActiveBuff(BuffRegistry.getBuffID("aoasunblessedsearing"), attackOwner, 5.0F, buff.owner), true);
            }
        }

    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoasunblessedamullettip"));
        return tooltips;
    }
}
