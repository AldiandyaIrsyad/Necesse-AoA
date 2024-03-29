package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class AoAAurumArborisLeaf extends MatItem {

    public AoAAurumArborisLeaf() {
        super(10000, Rarity.LEGENDARY);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaaurumleaf1"));
        tooltips.add(Localization.translate("itemtooltip", "aoaaurumleaf2"));
        tooltips.add(Localization.translate("itemtooltip", "aoaaurumleaf3"));
        return tooltips;
    }

}
