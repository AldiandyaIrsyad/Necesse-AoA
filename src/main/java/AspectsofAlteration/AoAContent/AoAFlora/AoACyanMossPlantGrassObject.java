//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.registries.TileRegistry;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

import java.awt.*;

public class AoACyanMossPlantGrassObject extends GrassObject {
    public AoACyanMossPlantGrassObject() {
        super("aoamossplant2grassobject", 2);
        this.mapColor = new Color(89, 129, 48);
        this.lightHue = 145;
        this.lightLevel = 75;
        this.lightSat = -120;
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            return level.getTileID(x, y) != TileRegistry.getTileID("aoacyanmosstile") ? "wrongtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.getTileID("aoacyanmosstile");
    }
}
