package AspectsofAlteration.AoAContent.AoAMaterials;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class AoACrystalBarMaterialItem extends MatItem {

    public AoACrystalBarMaterialItem() {
        super(10000, Rarity.UNCOMMON);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoacrystalbartip1"));
        tooltips.add(Localization.translate("itemtooltip", "aoacrystalbartip2"));
        return tooltips;
    }

}
