package AspectsofAlteration.AoAContent;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.CraftingStationObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoADecorationTableObject extends CraftingStationObject {
    public GameTexture texture;

    public AoADecorationTableObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(116, 111, 124);
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.roomProperties.add("potionwork");
        this.lightLevel = 85;
        this.lightSat = 150;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoadecorationtableobject");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20) : new Rectangle(x * 32 + 4, y * 32 + 2, 24, 28);
    }

    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        return list;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % 4;
        final TextureDrawOptions options = this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).light(light).pos(drawX, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.getTech("aoadecorationtable")};
    }
}
