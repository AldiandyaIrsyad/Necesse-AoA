package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAWeapons.AoAAttackHandlers.AoAAssaultRifleAttackHandler;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.DeathRipperAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

public class AoAAssaultRifle extends GunProjectileToolItem {
    public AoAAssaultRifle() {
        super(NORMAL_AMMO_TYPES, 1600);
        this.rarity = Rarity.COMMON;
        this.animSpeed = 2000;
        this.attackDamage = new GameDamage(DamageTypeRegistry.RANGED, 19.0F);
        this.attackXOffset = 24;
        this.attackYOffset = 10;
        this.attackRange = 1000;
        this.velocity = 500;
        this.knockback = 25;
        this.ammoConsumeChance = 0.25F;
        this.resilienceGain = 0.5F;
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    public int getAnimSpeed(InventoryItem item, Mob mob) {
        return this.animSpeed;
    }

    public boolean animDrawBehindHand() {
        return true;
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY, 0.0F);
    }

    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, Mob owner) {
        Projectile out = super.getProjectile(item, bulletItem, x, y, targetX, targetY, range, owner);
        out.height = 20.0F;
        out.moveDist(50);
        return out;
    }

    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.startAttackHandler(new AoAAssaultRifleAttackHandler(player, slot, item, this, seed, x, y));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        return super.onAttack(level, x, y, player, attackHeight, item, slot, animAttack, seed, contentReader);
    }

    protected void addTooltips(ListGameTooltips tooltips, InventoryItem item, boolean isSettlerWeapon) {
        super.addTooltips(tooltips, item, isSettlerWeapon);
        tooltips.add(Localization.translate("itemtooltip", "aoaassaultrifletip1"));
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "aoaassaultrifletip2"));
    }

    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, player, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)player.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        projectile.setAngle(projectile.getAngle() + spreadRandom.getFloatOffset(0.0F, 2.0F));
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }

    }

    public void playFireSound(AttackAnimMob mob) {
    }
}
