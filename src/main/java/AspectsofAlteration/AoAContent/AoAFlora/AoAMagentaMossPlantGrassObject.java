//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAMagentaMossPlantGrassObject extends GrassObject {
    public AoAMagentaMossPlantGrassObject() {
        super("aoamossplant3grassobject", 2);
        this.mapColor = new Color(89, 129, 48);
        this.lightLevel = 75;
        this.lightHue = 20.0F;
        this.lightSat = 180.0F;
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("aoamagentamosstile") ? "wrongtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.getTileID("aoamagentamosstile");
    }
}
