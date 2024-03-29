package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAIgnusWave;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.CustomSwordToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

// Extends CustomSwordToolItem
public class AoAIgnusBlade extends CustomSwordToolItem implements ItemInteractAction {

    // Weapon attack textures are loaded from resources/player/weapons/<itemStringID>

    public AoAIgnusBlade() {
        super(Rarity.UNCOMMON, 300, 150, 90, 100, 400);
    }


    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.buffManager.hasBuff(BuffRegistry.getBuffID("aoaignuspower"));
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds(BuffRegistry.getBuff("aoaignuspower")) / 8.0F;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {

                float rangeMod = 2.5F;
                float velocity = 300.0F;
                float finalVelocity = (float)Math.round((Float)this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * (Float)player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
                GameRandom random = new GameRandom((long)seed);
                player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuffID("aoaignuspower"), player, 10.0F, (Attacker)null), false);
                GameDamage specialAttackDmg = this.getDamage(item).modFinalMultiplier(10.0F);
                Projectile projectile = new AoAIgnusWave(level, player.x, player.y, (float)x, (float)y, finalVelocity, (int)((float)this.getAttackRange(item) * rangeMod) + 2000, specialAttackDmg, player);
                projectile.getUniqueID(random);
                level.entityManager.projectiles.addHidden(projectile);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
                }
        return item;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaignusbladetip1"), 400);
        return tooltips;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y) {
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }
}
