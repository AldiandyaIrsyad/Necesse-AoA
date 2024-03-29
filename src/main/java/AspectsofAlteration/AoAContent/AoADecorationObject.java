package AspectsofAlteration.AoAContent;

import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;

public class AoADecorationObject extends FurnitureObject {
    protected String textureName;
    public GameTexture texture;

    public AoADecorationObject(String textureName, ToolType toolType, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.furnitureType = "cabinet";
    }

    public AoADecorationObject(String textureName, Color mapColor) {
        this(textureName, ToolType.ALL, mapColor);
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    public void addDrawables(java.util.List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        float alpha = 1.0F;
        if (perspective != null && !Settings.hideUI) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 32 - 16, 64, 64);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5F;
            } else if (alphaRec.contains(camera.getMouseLevelPosX(), camera.getMouseLevelPosY())) {
                alpha = 0.5F;
            }
        }

        final TextureDrawOptions options = this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).light(light).pos(drawX, drawY - this.texture.getHeight() + 64);
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
        this.texture.initDraw().sprite(rotation % 4, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 64);
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32, y * 32 + 20, 32, 12);
        } else if (rotation == 1) {
            return new Rectangle(x * 32, y * 32, 16, 32);
        } else {
            return rotation == 2 ? new Rectangle(x * 32, y * 32, 32, 12) : new Rectangle(x * 32 + 16, y * 32, 16, 32);
        }
    }
}
