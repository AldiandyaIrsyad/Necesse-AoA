package AspectsofAlteration.AoAContent;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
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

public class AoAAdvancedGrinderObject extends CraftingStationObject {
    public GameTexture texture;
    protected int counterID;

    public AoAAdvancedGrinderObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(51, 53, 56);
        this.rarity = Item.Rarity.UNCOMMON;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
    }

    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, new int[]{this.counterID, this.getID()});
    }

    public int getPlaceRotation(Level level, int levelX, int levelY, PlayerMob player, int playerDir) {
        return Math.floorMod(super.getPlaceRotation(level, levelX, levelY, player, playerDir) - 1, 4);
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoaadvancedgrinderobject");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28) : new Rectangle(x * 32, y * 32 + 6, 26, 20);
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
            options.add(this.texture.initDraw().sprite(0, 2, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(0, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(0, 6, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(this.texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(this.texture.initDraw().sprite(1, 4, 32).light(light).pos(drawX, drawY));
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

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        if (rotation == 0) {
            this.texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY);
            this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY - 64);
            this.texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            this.texture.initDraw().sprite(0, 5, 32).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(0, 6, 32).alpha(alpha).draw(drawX, drawY);
            this.texture.initDraw().sprite(1, 5, 32).alpha(alpha).draw(drawX + 32, drawY - 32);
            this.texture.initDraw().sprite(1, 6, 32).alpha(alpha).draw(drawX + 32, drawY);
        } else if (rotation == 2) {
            this.texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY);
            this.texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            this.texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY - 32);
            this.texture.initDraw().sprite(1, 4, 32).alpha(alpha).draw(drawX, drawY);
            this.texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY - 32);
            this.texture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX - 32, drawY);
        }

    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.getTech("aoaadvancedgrinder"),
                RecipeTechRegistry.getTech("aoademonicgrinder"),
                RecipeTechRegistry.getTech("aoasimplegrinder")};
    }

    public static int[] registerAdvancedGrinderObject() {
        AoAAdvancedGrinderObject o1 = new AoAAdvancedGrinderObject();
        AoAAdvancedGrinderObject2 o2 = new AoAAdvancedGrinderObject2();
        int i1 = ObjectRegistry.registerObject("aoaadvancedgrinderobject", o1, 140.0F, true);
        int i2 = ObjectRegistry.registerObject("aoaadvancedgrinderobject2", o2, 0.0F, false);
        o1.counterID = i2;
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}
