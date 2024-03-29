package AspectsofAlteration.AoAContent;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AoATier3ProcessingForgeObject extends GameObject implements SettlementWorkstationObject {
    public GameTexture texture;

    public AoATier3ProcessingForgeObject() {
        super(new Rectangle(32, 32));
        this.setItemCategory(new String[]{"objects", "craftingstations"});
        this.drawDamage = false;
        this.isLightTransparent = true;
        this.roomProperties.add("metalwork");
        this.lightHue = 50.0F;
        this.lightSat = 0.2F;
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    public int getLightLevel(Level level, int x, int y) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, x, y);
        return forgeObjectEntity != null && forgeObjectEntity.isFuelRunning() ? 100 : 0;
    }

    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        if (GameRandom.globalRandom.nextInt(10) == 0) {
            AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, x, y);
            if (forgeObjectEntity != null && forgeObjectEntity.isFuelRunning()) {
                int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
                level.entityManager.addParticle((float)(x * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(y * 32 + 32), Particle.GType.COSMETIC).smokeColor().heightMoves((float)startHeight, (float)(startHeight + 20)).lifeTime(1000);
            }
        }

    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoafallenforge");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20) : new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
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
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean isFueled = false;
        AoATier3ProcessingForgeObjectEntity objectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (objectEntity != null) {
            isFueled = objectEntity.isFuelRunning();
        }

        int spriteHeight = this.texture.getHeight() - 32;
        final TextureDrawOptions options = this.texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).light(light).pos(drawX, drawY - (spriteHeight - 32));
        final TextureDrawOptionsEnd flame;
        if (isFueled && rotation == 2) {
            int spriteX = (int)(level.getWorldEntity().getWorldTime() % 1200L / 300L);
            flame = this.texture.initDraw().sprite(spriteX, spriteHeight / 32, 32).light(light).pos(drawX, drawY);
        } else {
            flame = null;
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
                if (flame != null) {
                    flame.draw();
                }

            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteHeight = this.texture.getHeight() - 32;
        this.texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - (spriteHeight - 32));
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new AoATier3ProcessingForgeObjectEntity(level, x, y);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_PROCESSING_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    public AoATier3ProcessingForgeObjectEntity getForgeObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        return objectEntity instanceof AoATier3ProcessingForgeObjectEntity ? (AoATier3ProcessingForgeObjectEntity)objectEntity : null;
    }

    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? Recipes.streamRecipes(forgeObjectEntity.techs) : Stream.empty();
    }

    public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return true;
    }

    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity == null) {
            return false;
        } else {
            return forgeObjectEntity.getExpectedResults().crafts < 10 && (forgeObjectEntity.isFuelRunning() || forgeObjectEntity.canUseFuel());
        }
    }

    public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 5;
    }

    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getInputInventoryRange() : null;
    }

    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getOutputInventoryRange() : null;
    }

    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getCurrentAndExpectedResults().items : new ArrayList();
    }

    public ItemCategoriesFilter getDefaultFuelFilters(Level level, int tileX, int tileY) {
        return new ItemCategoriesFilter(5, 10, true);
    }

    public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        AoATier3ProcessingForgeObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity != null) {
            Inventory inventory = forgeObjectEntity.getInventory();
            if (inventory != null && forgeObjectEntity.fuelSlots > 0) {
                return new InventoryRange(inventory, 0, forgeObjectEntity.fuelSlots - 1);
            }
        }

        return null;
    }
}
