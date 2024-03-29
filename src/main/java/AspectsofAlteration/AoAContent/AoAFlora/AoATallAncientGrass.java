package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class AoATallAncientGrass extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(300.0);

    public AoATallAncientGrass() {
        super("aoatallancientgrass", -1);
        this.canPlaceOnShore = true;
        this.mapColor = new Color(37, 63, 37);
        this.displayMapTooltip = true;
        this.weaveAmount = 0.05F;
        this.extraWeaveSpace = 32;
        this.randomYOffset = 3.0F;
        this.randomXOffset = 10.0F;
        this.attackThrough = true;
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "aoatallancientgrass");
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        float baitChance = 30.0F;
        if (level.rainingLayer.isRaining()) {
            baitChance = 10.0F;
        }

        if (level.isCave) {
            baitChance = 25.0F;
        }

        return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / baitChance, "swamplarva")});
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        return level.getTileID(x, y) != TileRegistry.grassID ? "notmud" : super.canPlace(level, x, y, rotation);
    }

    public boolean isValid(Level level, int x, int y) {
        return super.isValid(level, x, y) && level.getTileID(x, y) == TileRegistry.grassID;
    }

    public int getLightLevelMod(Level level, int x, int y) {
        return 30;
    }

    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        super.addSimulateLogic(level, x, y, ticks, list, sendChanges);
        this.addSimulateSpread(level, x, y, 2, 8, 1, spreadChance, ticks, list, sendChanges);
    }

    public void tick(Level level, int x, int y) {
        super.tick(level, x, y);
        if (level.isServer() && GameRandom.globalRandom.getChance(spreadChance)) {
            Performance.record(level.tickManager(), "growTallGrass", () -> {
                this.tickSpread(level, x, y, 2, 8, 1);
            });
        }

    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record(tickManager, "thornsSetup", () -> {
            Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
            int objs = 0;
            Integer[] var9 = adj;
            int var10 = adj.length;

            int drawX;
            for(drawX = 0; drawX < var10; ++drawX) {
                Integer id = var9[drawX];
                if (id == this.getID()) {
                    ++objs;
                }
            }

            byte minTextureIndex;
            byte maxTextureIndex;
            if (objs < 4) {
                minTextureIndex = 4;
                maxTextureIndex = 7;
            } else {
                minTextureIndex = 0;
                maxTextureIndex = 3;
            }

            drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameLight light = level.getLightLevel(tileX, tileY);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 6, -5, 0, minTextureIndex, maxTextureIndex);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 12, -5, 1);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -5, 2, minTextureIndex, maxTextureIndex);
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
        int objs = 0;
        Integer[] var10 = adj;
        int var11 = adj.length;

        int drawX;
        for(drawX = 0; drawX < var11; ++drawX) {
            Integer id = var10[drawX];
            if (id == this.getID()) {
                ++objs;
            }
        }

        byte minTextureIndex;
        byte maxTextureIndex;
        if (objs < 4) {
            minTextureIndex = 4;
            maxTextureIndex = 7;
        } else {
            minTextureIndex = 0;
            maxTextureIndex = 3;
        }

        drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        LinkedList<LevelSortedDrawable> list = new LinkedList();
        OrderableDrawables tileList = new OrderableDrawables(new TreeMap());
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, (GameLight)null, 6, -5, 0, minTextureIndex, maxTextureIndex, 0.5F);
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, (GameLight)null, 12, -5, 1, 0.5F);
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, (GameLight)null, 26, -5, 2, minTextureIndex, maxTextureIndex, 0.5F);
        tileList.forEach((d) -> {
            d.draw(level.tickManager());
        });
        list.forEach((d) -> {
            d.draw(level.tickManager());
        });
    }

    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return true;
    }
}

