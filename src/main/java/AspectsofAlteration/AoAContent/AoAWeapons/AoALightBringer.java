package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAWeapons.AoAAttackHandlers.AoALightBringerAttackHandler;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.BloodGrimoireRightClickProjectile;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;

import java.awt.*;

public class AoALightBringer extends GreatswordToolItem {
    long lastSpriteUpdateTime = 0L;
    int spriteIndex = 0;

    public AoALightBringer() {
        super(Rarity.LEGENDARY, 200, 200, 150, 150, 1800, 17, 19, GreatswordToolItem.getThreeChargeLevels(500, 600, 700));
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoalightbringertip1"));
        tooltips.add(Localization.translate("itemtooltip", "aoalightbringertip2"));
        return tooltips;
    }

    public static GreatswordChargeLevel[] getThreeChargeLevels(int level1Time, int level2Time, int level3Time) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(level1Time, 1.0F, new Color(246, 224, 82)), new GreatswordChargeLevel(level2Time, 1.5F, new Color(255, 255, 31)), new GreatswordChargeLevel(level3Time, 2.0F, new Color(255, 201, 0))};
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.startAttackHandler(new AoALightBringerAttackHandler(this, player, slot, item, this, seed, x, y, this.chargeLevels));
        return item;
    }

    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        int timePerFrame = 100;
        if (player.getWorldEntity().getTime() > this.lastSpriteUpdateTime + (long)timePerFrame) {
            if (this.spriteIndex != 0) {
                ++this.spriteIndex;
            } else {
                this.spriteIndex = 0;
            }

            this.lastSpriteUpdateTime = player.getWorldEntity().getTime();
        }

        return new GameSprite(this.attackTexture, this.spriteIndex, 0, 100);
    }
    public Color getParticleColor() {
        return (null);
    }
}
