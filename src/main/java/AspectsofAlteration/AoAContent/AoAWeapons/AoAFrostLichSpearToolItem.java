package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAFrostLichSpearProjectile2;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.Random;

public class AoAFrostLichSpearToolItem extends SpearToolItem {
    private Random spreadRandom;

    public AoAFrostLichSpearToolItem() {
        super(1600);
        this.rarity = Rarity.EPIC;
        this.animSpeed = 400;
        this.attackDamage = new GameDamage(DamageTypeRegistry.MAGIC, 155.0F);
        this.attackRange = 140;
        this.knockback = 50;
    }

    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
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

            AoAFrostLichSpearProjectile2 projectile = new AoAFrostLichSpearProjectile2(player.x, player.y, (float)x, (float)y, (float)this.getAttackDamage(item, player), this.getAttackRange(item), this.getDamage(item), this.getKnockback(item, player), player);
            projectile.resetUniqueID(new GameRandom((long)seed));
            level.entityManager.projectiles.addHidden(projectile);
            if (level.isServerLevel()) {
                level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
            }
        }

        return out;
    }

}
