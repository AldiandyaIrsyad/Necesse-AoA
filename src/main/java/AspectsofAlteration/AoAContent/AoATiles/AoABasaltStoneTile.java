package AspectsofAlteration.AoAContent.AoATiles;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.layers.SimulatePriorityList;

import java.awt.*;

import static necesse.level.gameTile.LiquidTile.LAVA_ATTACKER;

public class AoABasaltStoneTile extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public AoABasaltStoneTile() {
        super(false, "aoabasaltstonetile");
        this.mapColor = new Color(58, 58, 58);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        synchronized(this.drawRandom) {
            tile = this.drawRandom.seeded(this.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }

        return new Point(0, tile);
    }

    public void tick(Mob mob, Level level, int x, int y) {
            mob.addBuff(new ActiveBuff(BuffRegistry.getBuffID("aoaimmolationbuff"), mob, 10.0F, (Attacker)null), true);

    }

    public int getTerrainPriority() {
        return 0;
    }

}
