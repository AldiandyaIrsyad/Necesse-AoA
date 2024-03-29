package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAVolcanoGrassObject extends GrassObject {
    public AoAVolcanoGrassObject() {
        super("aoavolcanograssobject", 2);
        this.mapColor = new Color(178, 108, 64);
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        float baitChance = 35.0F;
        if (level.rainingLayer.isRaining()) {
            baitChance = 15.0F;
        }

        return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / baitChance, "aoaashengrassseed")});
    }

}
