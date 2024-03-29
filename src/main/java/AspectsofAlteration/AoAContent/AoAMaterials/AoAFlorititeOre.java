package AspectsofAlteration.AoAContent.AoAMaterials;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class AoAFlorititeOre extends MatItem {

    public AoAFlorititeOre() {
        super(10000, Rarity.UNCOMMON);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaflorititetip1"));
        tooltips.add(Localization.translate("itemtooltip", "aoaflorititetip2"));
        tooltips.add(Localization.translate("itemtooltip", "aoasmeltabletip"));
        return tooltips;
    }

}