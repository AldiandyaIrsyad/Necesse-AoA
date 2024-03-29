//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;

import java.awt.*;

import static AspectsofAlteration.AoAContent.AoAFlora.AoATallAncientGrass.spreadChance;

public class AoAMushroomGrassObject extends GrassObject {
    public AoAMushroomGrassObject() {
        super("aoamushroomgrassobject", 2);
        this.mapColor = new Color(131, 99, 88);
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        float baitChance = 35.0F;
        if (level.rainingLayer.isRaining()) {
            baitChance = 15.0F;
        }

        return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / baitChance, "mushroom"), new ChanceLootItem(0.5F, "mushroom")});
    }
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        super.addSimulateLogic(level, x, y, ticks, list, sendChanges);
        this.addSimulateSpread(level, x, y, 2, 8, 1, spreadChance, ticks, list, sendChanges);
    }
    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("mudtile") ? "wrongtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.getTileID("mudtile");
    }
}
