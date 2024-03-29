package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAFrostbornSpearProjectile;
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
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.Random;

public class AoAFrostbornSpearToolItem extends SpearToolItem {
    private Random spreadRandom;

    public AoAFrostbornSpearToolItem() {
        super(1600);
        this.rarity = Rarity.EPIC;
        this.animSpeed = 400;
        this.attackDamage = new GameDamage(DamageTypeRegistry.MAGIC, 75.0F);
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

            AoAFrostbornSpearProjectile projectile = new AoAFrostbornSpearProjectile(level, player, player.x, player.y, (float)x, (float)y, this.animSpeed / 2, attackRange * 4, attackDamage, knockback);
            projectile.resetUniqueID(new GameRandom((long)seed));
            level.entityManager.projectiles.addHidden(projectile);
            if (level.isServer()) {
                level.getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
            }
        }

        return out;
    }

    public InventoryItem onSettlerAttack(Level level, HumanMob mob, Mob target, int attackHeight, int seed, InventoryItem item) {
        InventoryItem out = super.onSettlerAttack(level, mob, target, attackHeight, seed, item);
        Point2D.Float dir = GameMath.normalize(target.x - mob.x, target.y - mob.y + (float)attackHeight);
        Mob mount = mob.getMount();
        if (mount != null) {
            attackHeight -= mount.getRiderDrawYOffset();
        }

        CryoSpearShardProjectile projectile = new CryoSpearShardProjectile(mob.x + dir.x * (float)(this.attackRange - 35), mob.y + dir.y * (float)(this.attackRange - 35), mob.x + dir.x * 1000.0F, mob.y + dir.y * 1000.0F, 150.0F, 200, this.getDamage(item), this.getKnockback(item, mob), mob, attackHeight);
        projectile.resetUniqueID(new GameRandom((long)seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (level.isServerLevel()) {
            level.getServer().network.sendToClientsAt(new PacketSpawnProjectile(projectile), level);
        }
        return out;
    }

}
