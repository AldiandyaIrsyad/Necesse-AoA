package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.Screen;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SaplingObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoASaplingObject extends GameObject {
    public GameTexture texture;
    protected String textureName;
    public String[] validTiles;
    public String resultObjectStringID;
    public int minGrowTimeInSeconds;
    public int maxGrowTimeInSeconds;
    protected final GameRandom drawRandom;
    public boolean addAnySaplingIngredient;

    public AoASaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, String... validTiles) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.resultObjectStringID = resultObjectStringID;
        this.minGrowTimeInSeconds = minGrowTimeInSeconds;
        this.maxGrowTimeInSeconds = maxGrowTimeInSeconds;
        this.addAnySaplingIngredient = addAnySaplingIngredient;
        this.validTiles = validTiles;
        this.setItemCategory(new String[]{"objects", "saplings"});
        this.mapColor = new Color(16, 147, 30);
        this.displayMapTooltip = true;
        this.drawDamage = false;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.replaceCategories.add("sapling");
        this.canReplaceCategories.add("sapling");
        this.canReplaceCategories.add("tree");
        this.canReplaceCategories.add("bush");
        this.replaceRotations = false;
    }

    public AoASaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient) {
        this(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, "grasstile", "swampgrasstile", "dirttile", "farmland", "snowtile", "aoaalpinegrasstile","aoafrostbornsnowtile");
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        Screen.playSound(GameResources.grass, SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)));
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        boolean mirror;
        synchronized(this.drawRandom) {
            this.drawRandom.setSeed(this.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }

        final TextureDrawOptions options = this.texture.initDraw().sprite(0, 0, 32).mirror(mirror, false).light(light).pos(drawX, drawY - 8);
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
        boolean mirror;
        synchronized(this.drawRandom) {
            this.drawRandom.setSeed(this.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }

        this.texture.initDraw().sprite(0, 0, 32).mirror(mirror, false).alpha(alpha).draw(drawX, drawY - 8);
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            boolean valid = false;
            String[] var7 = this.validTiles;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String tile = var7[var9];
                if (level.getTileID(x, y) == TileRegistry.getTileID(tile)) {
                    valid = true;
                    break;
                }
            }

            return !valid ? "invalidtile" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        if (super.isValid(level, x, y)) {
            String[] var4 = this.validTiles;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String tile = var4[var6];
                if (level.getTileID(x, y) == TileRegistry.getTileID(tile)) {
                    return true;
                }
            }
        }

        return false;
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SaplingObjectEntity(level, x, y, this.resultObjectStringID, this.minGrowTimeInSeconds, this.maxGrowTimeInSeconds);
    }

    public Item generateNewObjectItem() {
        Item item = super.generateNewObjectItem();
        if (this.addAnySaplingIngredient) {
            item.addGlobalIngredient(new String[]{"anysapling"});
        }

        item.addGlobalIngredient(new String[]{"anycompostable"});
        return item;
    }
}
