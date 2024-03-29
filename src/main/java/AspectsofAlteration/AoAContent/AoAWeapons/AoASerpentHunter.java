package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAFrostbornSpearProjectile;
import AspectsofAlteration.AoAContent.AoAProjectiles.AoASerpentHunterWave;
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
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CryoSpearShardProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.VenomSlasherWaveProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.Random;

public class AoASerpentHunter extends SpearToolItem {
    private Random spreadRandom;

    public AoASerpentHunter() {
        super(1600);
        this.rarity = Rarity.EPIC;
        this.animSpeed = 400;
        this.attackDamage = new GameDamage(DamageTypeRegistry.MELEE, 35.0F);
        this.attackRange = 140;
        this.knockback = 50;
    }
    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaserpenthuntertip"));
        return tooltips;
    }

    public ListGameTooltips getSettlerWeaponTooltips(HumanMob perspective, InventoryItem item) {
        ListGameTooltips tooltips = super.getSettlerWeaponTooltips(perspective, item);
        tooltips.add(Localization.translate("itemtooltip", "aoaserpenthuntertip"));
        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        item = super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
        float rangeMod = 4.0F;
        float velocity = 180.0F;
        float finalVelocity = (float)Math.round((Float)this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * (Float)player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
        Projectile projectile = new AoASerpentHunterWave(level, player.x, player.y, (float)x, (float)y, finalVelocity, (int)((float)this.getAttackRange(item) * rangeMod), this.getDamage(item), player);
        GameRandom random = new GameRandom((long)seed);
        projectile.resetUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        projectile.moveDist(120.0);
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }

        return item;
    }

}
