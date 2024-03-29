package AspectsofAlteration.AoAContent.AoAWeapons.AoAAttackHandlers;

import AspectsofAlteration.AoAContent.AoAWeapons.AoAAssaultRifle;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.ArachnidWebBowToolItem;

import java.awt.geom.Point2D;

public class AoAAssaultRifleAttackHandler extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final AoAAssaultRifle toolItem;
    private final int attackSeed;
    private int shotsRemaining = 8;
    private int shots;
    private long timeBuffer;
    private final GameRandom random = new GameRandom();
    private int timeBetweenReloads = 500;
    private int timeBetweenBurstShots = 50;

    public AoAAssaultRifleAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, AoAAssaultRifle toolItem, int seed, int startTargetX, int startTargetY) {
        super(player, slot, 20, 1000.0F, startTargetX, startTargetY);
        this.attackSeed = seed;
        this.timeBuffer = (long)this.timeBetweenReloads;
        this.item = item;
        this.toolItem = toolItem;
    }

    public void onUpdate() {
        super.onUpdate();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.player.getX() + (int)(dir.x * 100.0F);
        int attackY = this.player.getY() + (int)(dir.y * 100.0F);
        if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
            int seed = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
            Packet attackContent = new Packet();
            this.player.showAttack(this.item, attackX, attackY, seed, attackContent);
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketShowAttack(this.player, this.item, attackX, attackY, seed, attackContent), client);
            }

            this.timeBuffer += (long)this.updateInterval;

            while(true) {
                float speedModifier = this.getSpeedModifier();
                if ((float)this.timeBuffer < (float)this.timeBetweenReloads * speedModifier) {
                    break;
                }

                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                --this.shotsRemaining;
                this.toolItem.superOnAttack(this.player.getLevel(), attackX, attackY, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, seed, new PacketReader(attackContent));
                if (this.player.isClient()) {
                    Screen.playSound(GameResources.bow, SoundEffect.effect(this.player));
                }

                if (this.shotsRemaining <= 0) {
                    this.shotsRemaining = 8;
                    this.timeBuffer = 0L;
                    break;
                }

                this.timeBuffer = (long)((int)((float)(this.timeBetweenReloads - this.timeBetweenBurstShots) * speedModifier));
            }
        }

    }

    private float getSpeedModifier() {
        return this.toolItem.getSpeedModifier(this.item, this.player);
    }

    public void onEndAttack(boolean bySelf) {
        this.player.startItemCooldown(this.toolItem, (int)((float)this.timeBetweenReloads * this.getSpeedModifier()));
        this.player.stopAttack();
        if (this.player.isServer()) {
            ServerClient client = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketPlayerStopAttack(client.slot), client);
        }

    }
}
