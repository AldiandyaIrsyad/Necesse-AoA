package AspectsofAlteration.AoAContent;

import AspectsofAlteration.AoAContent.AoABiomes.AoAVolcanicBiome;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class AoAChaoticSpawnItem extends ConsumableItem {
    public AoAChaoticSpawnItem() {
        super(1, true);
        this.itemCooldown = 2000;
        this.setItemCategory(new String[]{"consumable", "bossitems"});
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("boss");
        this.rarity = Rarity.LEGENDARY;
        this.worldDrawSize = 32;
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (level.isServerLevel()) {
            System.out.println("Chaotic Magus has been summoned at " + level.getIdentifier() + ".");
            float angle = (float) GameRandom.globalRandom.nextInt(360);
            float nx = (float)Math.cos(Math.toRadians((double)angle));
            float ny = (float)Math.sin(Math.toRadians((double)angle));
            float distance = 960.0F;
            Mob mob = MobRegistry.getMob("aoachaoticmagus", level);
            level.entityManager.addMob(mob, (float)(player.getX() + (int)(nx * distance)), (float)(player.getY() + (int)(ny * distance)));
            level.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", mob.getLocalization())), level);
        }

        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }

        return item;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "aoauseanywheretip"));
        return tooltips;
    }
}
