package AspectsofAlteration.AoAContent.AoAWeapons.AoAAttackHandlers;

import AspectsofAlteration.AoAContent.AoAWeapons.AoALightBringer;
import AspectsofAlteration.AoAContent.AoAWeapons.AoALightbringerProjectile;
import AspectsofAlteration.AoAContent.AoAWeapons.AoALightbringerProjectile2;
import necesse.engine.Screen;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.swordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class AoALightBringerAttackHandler extends GreatswordAttackHandler {
    AoALightBringer slimeGreatsword;
    PlayerMob player;

    public AoALightBringerAttackHandler(AoALightBringer slimeGreatsword, PlayerMob player, PlayerInventorySlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel... chargeLevels) {
        super(player, slot, item, toolItem, seed, startX, startY, chargeLevels);
        this.slimeGreatsword = slimeGreatsword;
        this.player = player;
    }

    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        switch (this.currentChargeLevel) {
            case 3:
            case 2:
            case 5:
                this.launchLargeProjectile(dir);
            default:
        }
    }
    private void launchLargeProjectile(Point2D.Float dir) {
        for(int i = -3; i <= 3; ++i) {
            float rangeMod = 7.0F;
            float velocity = 200.0F;
            float finalVelocity = (float)Math.round((Float)this.slimeGreatsword.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * velocity * (Float)this.player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
            Projectile projectile = new AoALightbringerProjectile(this.player.getLevel(), this.player.x, this.player.y, this.player.x + dir.x * 100.0F, this.player.y + dir.y * 100.0F, finalVelocity, (int)((float)this.slimeGreatsword.getAttackRange(this.item) * rangeMod), this.slimeGreatsword.getDamage(this.item), this.player);
            Projectile projectile2 = new AoALightbringerProjectile2(this.player.getLevel(), this.player.x, this.player.y, this.player.x + dir.x * 100.0F, this.player.y + dir.y * 100.0F, finalVelocity, (int)((float)this.slimeGreatsword.getAttackRange(this.item) * rangeMod), this.slimeGreatsword.getDamage(this.item), this.player);
            GameRandom random = new GameRandom((long)this.seed);
            projectile.resetUniqueID(random);
            projectile2.resetUniqueID(random);
            this.player.getLevel().entityManager.projectiles.addHidden(projectile);
            this.player.getLevel().entityManager.projectiles.addHidden(projectile2);
            projectile.moveDist(20.0);
            projectile2.moveDist(20.0);
            projectile2.setAngle(projectile2.getAngle() + (float)(5 * i) + GameRandom.globalRandom.getIntBetween(-35,35));
            projectile.setAngle(projectile.getAngle() + (float)(5 * i) + GameRandom.globalRandom.getIntBetween(-20,20));

                if (this.player.getLevel().isServerLevel()) {
                Screen.playSound(GameResources.firespell1, SoundEffect.effect(player));
                this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), this.player.getServerClient(), this.player.getServerClient());
                this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile2), this.player.getServerClient(), this.player.getServerClient());
                }
            }
    }

    public Point2D.Float findSpawnLocation(AttackingFollowingMob mob, Level level, PlayerMob player) {
        return findSpawnLocation(mob, level, player.x, player.y);
    }

    public static Point2D.Float findSpawnLocation(Mob mob, Level level, float centerX, float centerY) {
        ArrayList<Point2D.Float> possibleSpawns = new ArrayList();

        for(int cX = -1; cX <= 1; ++cX) {
            for(int cY = -1; cY <= 1; ++cY) {
                if (cX != 0 || cY != 0) {
                    float posX = centerX + (float)(cX * 32);
                    float posY = centerY + (float)(cY * 32);
                    if (!mob.collidesWith(level, (int)posX, (int)posY)) {
                        possibleSpawns.add(new Point2D.Float(posX, posY));
                    }
                }
            }
        }

        if (possibleSpawns.size() > 0) {
            return (Point2D.Float)possibleSpawns.get(GameRandom.globalRandom.nextInt(possibleSpawns.size()));
        } else {
            return new Point2D.Float(centerX, centerY);
        }
    }
}
