package AspectsofAlteration.AoAContent.AoAMaterials;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;
        import necesse.engine.localization.Localization;
        import necesse.entity.mobs.PlayerMob;
        import necesse.gfx.gameTooltips.ListGameTooltips;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.item.matItem.MatItem;

public class AoASyrilliteOre extends MatItem {

    public AoASyrilliteOre() {
        super(10000, Rarity.RARE);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoasyrillitetip1"));
        tooltips.add(Localization.translate("itemtooltip", "aoasyrillitetip2"));
        tooltips.add(Localization.translate("itemtooltip", "aoasmeltabletip"));
        return tooltips;
    }

}