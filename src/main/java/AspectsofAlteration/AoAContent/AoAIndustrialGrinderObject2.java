package AspectsofAlteration.AoAContent;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.CraftingStationObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

import java.awt.*;
import java.util.List;

class AoAIndustrialGrinderObject2 extends CraftingStationObject {
    public GameTexture texture;
    protected int counterID;

    protected AoAIndustrialGrinderObject2() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(51, 53, 56);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
    }

    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 1, 2, rotation, false, new int[]{this.getID(), this.counterID});
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoaindustrialgrinderobject");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        } else if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 26, 20);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 4, y * 32, 24, 26) : new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
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
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(1, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(1, 6, 32).light(light).pos(drawX, drawY));
            int flameSprite = (int) (level.getWorldEntity().getWorldTime() % 1200L / 300L);
            options.add(this.texture.initDraw().sprite(flameSprite % 2, 7 + flameSprite / 2, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(this.texture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(0, 4, 32).light(light).pos(drawX, drawY));
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.getTech("aoaindustrialgrinder"),
                RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                RecipeTechRegistry.getTech("aoasimplegrinder")};
    }
}
