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
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class AoAResinRoot extends GrassObject {
    public static double spreadChance = GameMath.getAverageSuccessRuns(300.0);

    public AoAResinRoot() {
        super("aoarootresin", -1);
        this.canPlaceOnShore = true;
        this.mapColor = new Color(220, 199, 43);
        this.displayMapTooltip = true;
        this.weaveAmount = 0.05F;
        this.extraWeaveSpace = 32;
        this.randomYOffset = 3.0F;
        this.randomXOffset = 10.0F;
        this.attackThrough = true;
        this.lightLevel = 80;
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "aoarootresin");
    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItemInterface[]{new LootItem("aoaresinroot")});
    }

    public int getLightLevelMod(Level level, int x, int y) {
        return 30;
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

