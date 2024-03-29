package AspectsofAlteration.AoAContent.AoAMobs;

import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.SpiderkinMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class AoASkeletonArcher extends SpiderkinMob {
    protected static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItemList(0.75F, new LootItemInterface[]{new LootItem("spideritearrow", 10)})});
    protected int shotsRemaining;

    public AoASkeletonArcher() {
        super(300, 40, 20);
        this.texture = MobRegistry.Textures.ancientArmoredSkeleton;
        this.attackAnimSpeed = 250;
        this.attackCooldown = 1000;
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<Mob>((Supplier)null, 384, 384, 40000, false, false) {
            public boolean attackTarget(Mob mob, Mob target) {
                if (AoASkeletonArcher.this.canAttack()) {
                    if (AoASkeletonArcher.this.shotsRemaining <= 0) {
                        AoASkeletonArcher.this.shotsRemaining = 3;
                    }

                    --AoASkeletonArcher.this.shotsRemaining;
                    mob.attack(target.getX(), target.getY(), false);
                    Projectile projectile = ProjectileRegistry.getProjectile("ironarrow", mob.getLevel(), mob.x, mob.y, target.x, target.y, 120.0F, 480, new GameDamage(80.0F), mob);
                    projectile.moveDist(60.0);
                    mob.getLevel().entityManager.projectiles.add(projectile);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public boolean canAttack() {
        if ((Boolean)this.buffManager.getModifier(BuffModifiers.INTIMIDATED)) {
            return false;
        } else {
            return super.canAttack() || this.shotsRemaining > 0 && this.getTimeSinceLastAttack() >= 150L;
        }
    }

    protected void doWasHitLogic(MobWasHitEvent event) {
        super.doWasHitLogic(event);
        if (!event.wasPrevented) {
            this.startAttackCooldown();
        }

    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        float animProgress = this.getAttackAnimProgress();
        Point sprite = this.getAnimSprite(x, y, this.dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        HumanDrawOptions humanDrawOptions = (new HumanDrawOptions(this.texture)).sprite(sprite).dir(this.dir).light(light);
        humanDrawOptions.hatTexture((player, dir, spriteX, spriteY, spriteRes, drawX1, drawY1, width, height, mirrorX, mirrorY, light1, alpha, mask) -> {
            return MobRegistry.Textures.human_baby_shadow.initDraw().sprite(spriteX, spriteY, spriteRes).light(light1.minLevelCopy(150.0F)).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addShaderTextureFit(mask, 1).pos(drawX1, drawY1);
        }, ArmorItem.HairDrawMode.NO_HAIR);
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("antiquebow"), (PlayerMob)null, animProgress, this.attackDir.x, this.attackDir.y);
        }

        final DrawOptions drawOptions = humanDrawOptions.pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, x, y, light, camera);
    }

    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            Screen.playSound(GameResources.bow, SoundEffect.effect(this));
        }

    }
}
