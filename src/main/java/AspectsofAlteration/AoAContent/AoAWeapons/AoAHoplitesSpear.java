package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAHoplitesSpearProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
        import java.awt.geom.Point2D;
import java.util.LinkedList;

import necesse.engine.localization.Localization;
        import necesse.engine.network.PacketReader;
        import necesse.engine.network.packet.PacketSpawnProjectile;
        import necesse.engine.registries.DamageTypeRegistry;
        import necesse.engine.util.GameMath;
        import necesse.engine.util.GameRandom;
        import necesse.entity.mobs.GameDamage;
        import necesse.entity.mobs.Mob;
        import necesse.entity.mobs.PlayerMob;
        import necesse.entity.mobs.friendly.human.HumanMob;
        import necesse.entity.projectile.CryoSpearShardProjectile;
        import necesse.gfx.gameTooltips.ListGameTooltips;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.PlayerInventorySlot;
        import necesse.inventory.item.Item.Rarity;
        import necesse.level.maps.Level;

public class AoAHoplitesSpear extends SpearToolItem implements ItemInteractAction {
    public AoAHoplitesSpear() {
        super(1600);
        this.rarity = Rarity.EPIC;
        this.animSpeed = 400;
        this.attackDamage = new GameDamage(DamageTypeRegistry.MELEE, 38.0F);
        this.attackRange = 140;
        this.knockback = 50;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cryospeartip"));
        return tooltips;
    }

    public ListGameTooltips getSettlerWeaponTooltips(HumanMob perspective, InventoryItem item) {
        ListGameTooltips tooltips = super.getSettlerWeaponTooltips(perspective, item);
        tooltips.add(Localization.translate("itemtooltip", "cryospeartip"));
        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        InventoryItem out = super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
        if (animAttack == 0) {
            Point2D.Float dir = GameMath.normalize((float)x - player.x, (float)y - player.y + (float)attackHeight);
            Mob mount = player.getMount();
            if (mount != null) {
                attackHeight -= mount.getRiderDrawYOffset();
            }
        }

        return out;
    }

    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return player.buffManager.hasBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF);
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
                float rangeMod = 2.5F;
                float velocity = 300.0F;
                float finalVelocity = (float)Math.round((Float)this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * (Float)player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
                GameRandom random = new GameRandom((long)seed);
                GameDamage specialAttackDmg = this.getDamage(item).modFinalMultiplier(0.75F);
                Projectile projectile = ProjectileRegistry.getProjectile("aoahoplitesspearprojectile", level, player.x, player.y, (float)x, (float)y, finalVelocity, 800, this.getDamage(item), this.getKnockback(item, player), player);
                projectile.setAngle(projectile.getAngle() + (float)random.getIntBetween(-30, 30));
                projectile.getUniqueID(random);
                level.entityManager.projectiles.addHidden(projectile);
                projectile.moveDist(20.0);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
                }
        return item;
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
    public InventoryItem onSettlerAttack(Level level, HumanMob mob, Mob target, int attackHeight, int seed, InventoryItem item) {
        InventoryItem out = super.onSettlerAttack(level, mob, target, attackHeight, seed, item);
        Point2D.Float dir = GameMath.normalize(target.x - mob.x, target.y - mob.y + (float)attackHeight);
        Mob mount = mob.getMount();
        if (mount != null) {
            attackHeight -= mount.getRiderDrawYOffset();
        }
        return out;
    }
}
