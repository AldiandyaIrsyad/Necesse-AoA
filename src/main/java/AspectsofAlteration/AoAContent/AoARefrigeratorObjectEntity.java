package AspectsofAlteration.AoAContent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
        import necesse.engine.network.PacketReader;
        import necesse.engine.network.PacketWriter;
        import necesse.engine.save.LoadData;
        import necesse.engine.save.SaveData;
        import necesse.engine.util.GameMath;
        import necesse.inventory.Inventory;
        import necesse.inventory.InventoryItem;
        import necesse.inventory.InventoryRange;
        import necesse.inventory.itemFilter.ItemCategoriesFilter;
        import necesse.level.maps.Level;

public class AoARefrigeratorObjectEntity extends InventoryObjectEntity {
    public static int FUEL_TIME_ADDED = 240000;
    public int fuelSlots;
    public float spoilRate;
    public boolean forceUpdate = true;
    protected int remainingFuelTime;
    protected int usedFuelTime;
    protected long lastTickedTime;

    public AoARefrigeratorObjectEntity(Level level, int x, int y, int fuelSlots, int inventorySlots, float spoilRate) {
        super(level, x, y, inventorySlots + fuelSlots);
        this.fuelSlots = fuelSlots;
        this.spoilRate = spoilRate;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("remainingFuelTime", this.remainingFuelTime);
        save.addInt("usedFuelTime", this.usedFuelTime);
        save.addLong("lastTickedTime", this.lastTickedTime);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.remainingFuelTime = save.getInt("remainingFuelTime", 0);
        this.usedFuelTime = save.getInt("usedFuelTime", 0);
        this.lastTickedTime = save.getLong("lastTickedTime", 0L);
    }

    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextInt(this.remainingFuelTime);
        writer.putNextInt(this.usedFuelTime);
        writer.putNextLong(this.lastTickedTime);
    }

    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.remainingFuelTime = reader.getNextInt();
        this.usedFuelTime = reader.getNextInt();
        this.lastTickedTime = reader.getNextLong();
    }

    protected void onInventorySlotUpdated(int slot) {
        super.onInventorySlotUpdated(slot);
        this.forceUpdate = true;
    }

    public boolean isItemValid(int slot, InventoryItem item) {
        if (slot >= this.fuelSlots) {
            return super.isItemValid(slot, item);
        } else {
            return item == null || this.isFuel(item);
        }
    }

    public void init() {
        super.init();
        this.updateInventorySpoilRate();
    }

    public void clientTick() {
        super.clientTick();
        this.updateFuel();
    }

    public void serverTick() {
        super.serverTick();
        this.updateFuel();
    }

    public void updateFuel() {
        long currentWorldTime = this.getWorldTime();
        long lastTime = this.lastTickedTime == 0L ? currentWorldTime : this.lastTickedTime;
        long addedTime = Math.max(0L, currentWorldTime - lastTime);

        while(addedTime > 0L || this.forceUpdate) {
            if (this.forceUpdate) {
                this.forceUpdate = false;
                if (this.remainingFuelTime <= 0) {
                    this.tryUseFuel();
                }
            }

            boolean couldNotUseFuel = false;
            if (this.remainingFuelTime <= 0 && !this.tryUseFuel()) {
                couldNotUseFuel = true;
            }

            long usedTime = Math.max(0L, GameMath.min(new long[]{(long)this.remainingFuelTime, addedTime}));
            this.remainingFuelTime = (int)((long)this.remainingFuelTime - usedTime);
            this.usedFuelTime = (int)((long)this.usedFuelTime + usedTime);
            addedTime -= usedTime;
            if (this.remainingFuelTime <= 0) {
                this.usedFuelTime = 0;
                if (couldNotUseFuel) {
                    break;
                }

                this.forceUpdate = true;
            }
        }

        this.updateInventorySpoilRate();
        this.lastTickedTime = currentWorldTime;
    }

    public boolean tryUseFuel() {
        for(int i = 0; i < this.fuelSlots; ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (this.isFuel(item)) {
                this.remainingFuelTime += FUEL_TIME_ADDED;
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) {
                    this.inventory.setItem(i, (InventoryItem)null);
                }

                this.inventory.markDirty(i);
                return true;
            }
        }

        return false;
    }

    public boolean isFuel(InventoryItem item) {
        return item != null && item.item.isGlobalIngredient("anycoolingfuel");
    }

    public void updateInventorySpoilRate() {
        if (this.remainingFuelTime > 0) {
            this.inventory.spoilRateModifier = this.spoilRate;
        } else {
            this.inventory.spoilRateModifier = 1.0F;
        }

    }

    public boolean hasFuel() {
        return this.remainingFuelTime > 0;
    }

    public float getFuelProgress() {
        if (this.remainingFuelTime > 0) {
            int totalFuelTime = this.usedFuelTime + this.remainingFuelTime;
            return Math.abs(GameMath.limit((float)this.usedFuelTime / (float)totalFuelTime, 0.0F, 1.0F) - 1.0F);
        } else {
            return 0.0F;
        }
    }

    public InventoryRange getSettlementStorage() {
        Inventory inventory = this.getInventory();
        return new InventoryRange(inventory, this.fuelSlots, inventory.getSize());
    }

    public InventoryRange getFuelInventoryRange() {
        return new InventoryRange(this.getInventory(), 0, this.fuelSlots - 1);
    }

    public InventoryRange getSettlementFuelInventoryRange() {
        return this.getFuelInventoryRange();
    }

    public ItemCategoriesFilter getSettlementDefaultFuelFilters() {
        return new ItemCategoriesFilter(5, 10, true);
    }
}
