package AspectsofAlteration.AoAContent.AoAWeapons;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAIgnusWave;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.*;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.BloodClawProjectile;
import AspectsofAlteration.AoAContent.AoAProjectiles.AoADemonsRainProjectile;
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
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TheCrimsonSkyProjectileToolItem;
import necesse.inventory.item.toolItem.swordToolItem.CustomSwordToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

// Extends CustomSwordToolItem
public class AoADemonSword extends CustomSwordToolItem implements ItemInteractAction {

    // Weapon attack textures are loaded from resources/player/weapons/<itemStringID>
    public int projectileMaxHeight;
    public int specialAttackProjectileCount;

    public AoADemonSword() {
        super(Rarity.QUEST, 300, 9999, 90, 100, 400);
        this.specialAttackProjectileCount = 20;
        this.projectileMaxHeight = 600;
    }



    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.buffManager.hasBuff(BuffRegistry.getBuffID("aoaignuspower"));
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds(BuffRegistry.getBuff("aoaignuspower")) / 8.0F;
    }
    public AoADemonsRainProjectile getTheCrimsonSkyProjectile(Level level, int x, int y, Mob owner, GameDamage damage, float velocity, int knockback) {
        Point2D.Float targetPoints = new Point2D.Float((float)x, (float)y);
        Point2D.Float normalizedVector = GameMath.normalize(targetPoints.x - owner.x, targetPoints.y - owner.y);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)owner.x, (double)owner.y, (double)normalizedVector.x, (double)normalizedVector.y, targetPoints.distance((double)owner.x, (double)owner.y), 0, (new CollisionFilter()).projectileCollision().addFilter((tp) -> {
            return tp.object().object.isWall || tp.object().object.isRock;
        }));
        if (!hits.isEmpty()) {
            Ray<LevelObjectHit> first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            targetPoints.y = (float)first.y2;
        }

        return new AoADemonsRainProjectile(level, owner, owner.x, owner.y, owner.x, owner.y - 1.0F, velocity, this.projectileMaxHeight, damage, knockback, targetPoints, false);
    }
    public InventoryItem onLevelInteract(Level level, final int x, final int y, final PlayerMob player, int attackHeight, final InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        final GameRandom random = new GameRandom((long)seed);

        for(int i = 0; i < this.specialAttackProjectileCount; ++i) {
            level.entityManager.addLevelEventHidden(new WaitForSecondsEvent((float)i / 10.0F) {
                public void onWaitOver() {
                    Point2D.Float targetPoints = new Point2D.Float((float)x, (float)y);
                    int rndX = random.getIntBetween(-75, 75);
                    int rndY = random.getIntBetween(-75, 75);
                    targetPoints.x += (float)rndX;
                    targetPoints.y += (float)rndY;
                    RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(this.level, (double)player.x, (double)player.y, (double)(targetPoints.x - player.x), (double)(targetPoints.y - player.y), targetPoints.distance((double)player.x, (double)player.y), 0, (new CollisionFilter()).projectileCollision().addFilter((tp) -> {
                        return tp.object().object.isWall || tp.object().object.isRock;
                    }));
                    if (!hits.isEmpty()) {
                        Ray<LevelObjectHit> first = (Ray)hits.getLast();
                        targetPoints.x = (float)first.x2;
                        targetPoints.y = (float)first.y2;
                    }

                    GameDamage specialAttackDmg = AoADemonSword.this.getDamage(item).modFinalMultiplier(1.25F);
                    AoADemonsRainProjectile projectile = new AoADemonsRainProjectile(this.level, player, player.x, player.y, player.x, player.y - 1.0F, 200, AoADemonSword.this.projectileMaxHeight, specialAttackDmg, AoADemonSword.this.getKnockback(item, player), targetPoints, false);
                    projectile.getUniqueID(random);
                    this.level.entityManager.projectiles.addHidden(projectile);
                    if (this.level.isServer()) {
                        this.level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
                    }

                }
            });
        }

        return item;
    }
    public Projectile getProjectile(Level level, int x, int y, Mob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, PacketReader contentReader) {
        return this.getTheCrimsonSkyProjectile(level, x, y, owner, damage, velocity, knockback);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoaignusbladetip1"), 400);
        return tooltips;
    }
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        float range = 500.0F;
        return new Point((int)(player.x + aimDirX * range), (int)(player.y + aimDirY * range));
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
