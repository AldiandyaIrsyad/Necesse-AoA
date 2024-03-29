package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAAlpineGrassObject extends GrassObject {
    public AoAAlpineGrassObject() {
        super("aoaalpinegrassobject", 2);
        this.mapColor = new Color(109, 161, 99);
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        float baitChance = 35.0F;
        if (level.rainingLayer.isRaining()) {
            baitChance = 15.0F;
        }

        return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / baitChance, "wormbait"), new ChanceLootItem(0.01F, "aoaalpinegrassseed")});
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("aoaalpinegrasstile") ? "wrongtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.getTileID("aoaalpinegrasstile");
    }
}
