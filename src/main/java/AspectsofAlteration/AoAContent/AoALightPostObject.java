package AspectsofAlteration.AoAContent;

import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.furniture.LampObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.List;

public class AoALightPostObject extends LampObject {
    public float flameHue;
    public float smokeHue;

    public AoALightPostObject(String textureName, ToolType toolType, Color mapColor, float lightHue, float lightSat) {
        super(textureName, new Rectangle(4, 4, 24, 24), toolType, mapColor, lightHue, lightSat);
        this.furnitureType = "aoalightpost";
        this.lightLevel = 200;
        this.lightHue = 30.0F;
        this.lightSat = 0.2F;
        this.toolTier = 0;
        this.toolType = ToolType.ALL;
    }

    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -28, 32, 28));
        return list;
    }
}
