package AspectsofAlteration.AoAContent.AoABuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
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
        import necesse.entity.mobs.MobWasHitEvent;
        import necesse.entity.mobs.PlayerMob;
        import necesse.entity.mobs.buffs.ActiveBuff;
        import necesse.entity.mobs.buffs.BuffEventSubscriber;
        import necesse.gfx.gameTooltips.ListGameTooltips;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.item.trinketItem.TrinketItem;

public class AoARustyKnifeBuff extends TrinketBuff {
    public AoARustyKnifeBuff() {
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoarustyknifetip"));
        return tooltips;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (!event.wasPrevented) {
            event.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuffID("aoarustyknifebleeding"), event.target, 5.0F, event.attacker), event.target.isServer());
        }

    }
}
