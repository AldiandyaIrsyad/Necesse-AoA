package AspectsofAlteration.AoAContent;

import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.furniture.InventoryObject;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.List;
        import java.awt.Color;
        import java.awt.Rectangle;
        import java.util.List;
        import necesse.engine.localization.Localization;
        import necesse.engine.registries.ContainerRegistry;
        import necesse.engine.util.GameRandom;
        import necesse.entity.mobs.PlayerMob;
        import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
        import necesse.entity.objectEntity.ObjectEntity;
        import necesse.entity.particle.ParticleOption;
        import necesse.entity.particle.Particle.GType;
        import necesse.gfx.GameResources;
        import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
        import necesse.gfx.gameTooltips.ListGameTooltips;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.container.object.OEInventoryContainer;
        import necesse.inventory.item.toolItem.ToolType;
        import necesse.level.gameObject.ObjectHoverHitbox;
        import necesse.level.maps.Level;

public class AoARefrigeratorObject extends InventoryObject {
    public AoARefrigeratorObject(String textureName, int slots, ToolType toolType, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), toolType, mapColor);
    }

    public AoARefrigeratorObject(String textureName, int slots, Color mapColor) {
        super(textureName, slots, new Rectangle(32, 32), mapColor);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoarefrigeratortip"), 400);
        return tooltips;
    }

    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        FueledRefrigeratorObjectEntity coolingBoxObjectEntity = this.getCoolingBoxObjectEntity(level, x, y);
        if (coolingBoxObjectEntity != null && coolingBoxObjectEntity.hasFuel() && GameRandom.globalRandom.nextInt(10) == 0) {
            int startHeight = 16 + GameRandom.globalRandom.nextInt(16);
            level.entityManager.addParticle((float)(x * 32 + GameRandom.globalRandom.getIntBetween(2, 30)), (float)(y * 32 + 32), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(0, 0, 12)).heightMoves((float)startHeight, (float)(startHeight - 12)).lifeTime(3000).fadesAlphaTimeToCustomAlpha(500, 500, 0.25F).size(new ParticleOption.DrawModifier() {
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    options.size(24, 24);
                }
            });
        }

    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20) : new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        return list;
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FueledRefrigeratorObjectEntity(level, x, y, 2, 80, 0.00F);
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.FUELED_REFRIGERATOR_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    public FueledRefrigeratorObjectEntity getCoolingBoxObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        return objectEntity instanceof FueledRefrigeratorObjectEntity ? (FueledRefrigeratorObjectEntity)objectEntity : null;
    }
}
