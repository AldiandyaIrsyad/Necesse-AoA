//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAGoldenMossPlantGrassObject extends GrassObject {
    public AoAGoldenMossPlantGrassObject() {
        super("aoamossplantgrassobject", 2);
        this.mapColor = new Color(54, 79, 30);
        this.lightLevel = 75;
        this.lightHue = 70.0F;
        this.lightSat = 0.4F;
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("aoagoldenmosstile") ? "wrongtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.getTileID("aoagoldenmosstile");
    }
}
