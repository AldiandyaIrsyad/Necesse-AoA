package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class AoAAurumArborisLog extends MatItem {

    public AoAAurumArborisLog() {
        super(10000, Rarity.LEGENDARY);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaaurumlog1"));
        tooltips.add(Localization.translate("itemtooltip", "aoaaurumlog2"));
        return tooltips;
    }

}
