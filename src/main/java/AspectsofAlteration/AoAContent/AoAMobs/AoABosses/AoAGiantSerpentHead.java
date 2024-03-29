package AspectsofAlteration.AoAContent.AoAMobs.AoABosses;

import AspectsofAlteration.AoA;
import AspectsofAlteration.AoAContent.AoAProjectiles.AoADemonsRainProjectile;
import AspectsofAlteration.AoAContent.AoAWeapons.AoADemonSword;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeQuakeWarningEvent;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.*;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SwampBoulderProjectile;
import necesse.entity.projectile.SwampRazorProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class AoAGiantSerpentHead extends BossWormMobHead<AoAGiantSerpentBody, AoAGiantSerpentHead> {
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(0.2F, "oaklog")});
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemInterface[]{new LootItem("aoaserpenthunter"), new LootItem("aoaserpenttoothnecklacebuff"), new LootItem("aoaserpentscaleshield")});
    public static LootTable privateLootTable;
    public static float lengthPerBodyPart;
    public static float waveLength;
    public static final int totalBodyParts = 70;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public static GameDamage headCollisionDamage;
    public static GameDamage bodyCollisionDamage;
    public static GameDamage razorDamage;
    public static GameDamage boulderExplosionDamage;
    public static int boulderExplosionRange;
    public static int totalRazorProjectiles;
    public static MaxHealthGetter MAX_HEALTH;
    public final CoordinateMobAbility flickSound;
    public final CoordinateMobAbility swingSound;

    public AoAGiantSerpentHead() {
        super(100, waveLength, 80.0F, 70, 36.0F, -8.0F);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 160;
        this.setSpeed(150.0F);
        this.setArmor(15);
        this.accelerationMod = 1.0F;
        this.decelerationMod = 1.0F;
        this.collision = new Rectangle(-20, -15, 40, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -60, 64, 64);
        this.flickSound = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                if (AoAGiantSerpentHead.this.isClient()) {
                    Screen.playSound(GameResources.magicbolt2, SoundEffect.effect((float)x, (float)y).pitch(1.5F).volume(0.5F));
                }

            }
        });
        this.swingSound = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                if (AoAGiantSerpentHead.this.isClient()) {
                    Screen.playSound(GameResources.swing1, SoundEffect.effect((float)x, (float)y).pitch(0.8F).volume(1.0F));
                }

            }
        });
    }

    public void setupHealthPacket(PacketWriter writer, boolean isFull) {
        this.scaling.setupHealthPacket(writer, isFull);
        super.setupHealthPacket(writer, isFull);
    }

    public void applyHealthPacket(PacketReader reader, boolean isFull) {
        this.scaling.applyHealthPacket(reader, isFull);
        super.applyHealthPacket(reader, isFull);
    }

    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        if (this.scaling != null) {
            this.scaling.updatedMaxHealth();
        }

    }

    protected void onAppearAbility() {
        super.onAppearAbility();
        if (this.isClient()) {
            Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
        }

    }

    protected float getDistToBodyPart(AoAGiantSerpentBody bodyPart, int index, float lastDistance) {
        return lengthPerBodyPart;
    }

    protected AoAGiantSerpentBody createNewBodyPart(int index) {
        Object bodyPart;
        if (index == 69) {
            bodyPart = new AoAGiantSerpentTail();
        } else {
            bodyPart = new AoAGiantSerpentBody();
        }

        ((AoAGiantSerpentBody)bodyPart).sharesHitCooldownWithNext = index % 3 < 2;
        if (index != 0 && index != 68) {
            ((AoAGiantSerpentBody)bodyPart).sprite = new Point(index % 4, 0);
            ((AoAGiantSerpentBody)bodyPart).shadowSprite = 0;
        } else {
            ((AoAGiantSerpentBody)bodyPart).sprite = new Point(4, 0);
            ((AoAGiantSerpentBody)bodyPart).shadowSprite = 1;
        }

        return (AoAGiantSerpentBody)bodyPart;
    }

    protected void playMoveSound() {
        Screen.playSound(GameResources.shake, SoundEffect.effect(this).falloffDistance(1000));
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this, new AoAGiantSerpentHead.SwampGuardianAI(), new FlyingAIMover());
        if (this.isClient()) {
            Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
        }

    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public GameDamage getCollisionDamage(Mob target) {
        return headCollisionDamage;
    }

    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    public void clientTick() {
        super.clientTick();
        Screen.setMusic(MusicRegistry.getMusic("simplebeat"), Screen.MusicPriority.EVENT, 1.5F);
        Screen.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        float mod = Math.abs((float)Math.pow((double)healthPerc, 0.5) - 1.0F);
        this.setSpeed(120.0F + mod * 90.0F);
    }

    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        float mod = Math.abs((float)Math.pow((double)healthPerc, 0.5) - 1.0F);
        this.setSpeed(120.0F + mod * 90.0F);
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fromFile("aoagiantserpentmob"), GameRandom.globalRandom.nextInt(6), 6, 32, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y);
            float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
            WormMobHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.fromFile("aoagiantserpentmob"), 0, 1, 96), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, headAngle, drawX, drawY, 64);
            this.addShadowDrawables(tileList, x, y, light, camera);
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(2, 0, res).light(light).pos(drawX, drawY);
    }

    public boolean shouldDrawOnMap() {
        return this.isVisible();
    }

    public void drawOnMap(TickManager tickManager, int x, int y) {
        super.drawOnMap(tickManager, x, y);
        int drawX = x - 24;
        int drawY = y - 24;
        float headAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
        MobRegistry.Textures.fromFile("aoagiantserpentmob").initDraw().sprite(2, 2, 96).rotate(headAngle + 90.0F, 24, 24).size(48, 48).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox() {
        return new Rectangle(-15, -15, 30, 30);
    }

    public GameTooltips getMapTooltips() {
        return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
    }

    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        attackers.stream().map(Attacker::getAttackOwner).filter((m) -> {
            return m != null && m.isPlayer;
        }).distinct().forEach((m) -> {
            this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), ((PlayerMob)m).getServerClient());
        });
    }

    static {
        privateLootTable = new LootTable(new LootItemInterface[]{uniqueDrops});
        lengthPerBodyPart = 25.0F;
        waveLength = 500.0F;
        headCollisionDamage = new GameDamage(46.0F);
        bodyCollisionDamage = new GameDamage(36.0F);
        razorDamage = new GameDamage(32.0F);
        boulderExplosionDamage = new GameDamage(70.0F);
        boulderExplosionRange = 80;
        totalRazorProjectiles = 250;
        MAX_HEALTH = new MaxHealthGetter(3000, 6000, 8000, 10000, 13000);
    }

    public static class SwampGuardianAI<T extends AoAGiantSerpentHead> extends SelectorAINode<T> {
        public SwampGuardianAI() {
            SequenceAINode<T> chaserSequence = new SequenceAINode();
            this.addChild(chaserSequence);
            chaserSequence.addChild(new RemoveOnNoTargetNode(100));
            final TargetFinderAINode targetFinder;
            chaserSequence.addChild(targetFinder = new TargetFinderAINode<T>(3200) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            targetFinder.moveToAttacker = false;
            ChargingCirclingChaserAINode chaserAI;
            chaserSequence.addChild(chaserAI = new ChargingCirclingChaserAINode(500, 40));
            chaserSequence.addChild(new SpawnProjectilesOnHealthLossAINode<T>(AoAGiantSerpentHead.totalRazorProjectiles) {
                public void shootProjectile(T mob) {
                    WormMobHead.BodyPartTarget t = mob.getRandomTargetFromBodyPart(this, targetFinder, (m, bp) -> {
                        return m.getDistance(bp) < 500.0F && !mob.getLevel().collides(new Line2D.Float(m.x, m.y, bp.x, bp.y), (new CollisionFilter()).mobCollision());
                    });
                    if (t != null) {
                        t.bodyPart.getLevel().entityManager.projectiles.add(new SwampRazorProjectile(t.bodyPart.getLevel(), mob, t.bodyPart.x, t.bodyPart.y, t.target.x, t.target.y, 70.0F, 1750, AoAGiantSerpentHead.razorDamage, 50));
                        mob.flickSound.runAndSend(t.bodyPart.getX(), t.bodyPart.getY());
                    }

                }
            });
            chaserSequence.addChild(new AoAGiantSerpentHead.DiveChargeRotationAI(chaserAI));
            chaserSequence.addChild(new AoAGiantSerpentHead.ChargeRotation(chaserAI));
            this.addChild(new WandererAINode(0));
        }
    }
    public static class SpawnBouldersAI<T extends AoAGiantSerpentHead> extends AINode<T> {
        public int ticker;
        public TargetFinderAINode<T> targetFinder;

        public SpawnBouldersAI(TargetFinderAINode<T> targetFinder) {
            this.targetFinder = targetFinder;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(0.8F, 2.0F));
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            --this.ticker;
            if (this.ticker <= 0) {
                WormMobHead.BodyPartTarget t = mob.getRandomTargetFromBodyPart(this, this.targetFinder, (m, bp) -> {
                    float dist = m.getDistance(bp);
                    return dist > 350.0F && dist < 400.0F;
                });
                if (t != null) {
                    Point2D.Float targetPos = new Point2D.Float(t.target.x + GameRandom.globalRandom.floatGaussian() * 30.0F, t.target.y + GameRandom.globalRandom.floatGaussian() * 30.0F);
                    int dist = (int)t.bodyPart.getDistance(targetPos.x, targetPos.y);
                    t.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(t.bodyPart.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y + GameRandom.globalRandom.getIntOffset(50,-50), 40.0F, dist, AoAGiantSerpentHead.razorDamage, 50));
                    t.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(t.bodyPart.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y + GameRandom.globalRandom.getIntOffset(50,-50), 40.0F, dist, AoAGiantSerpentHead.razorDamage, 50));
                    t.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(t.bodyPart.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y + GameRandom.globalRandom.getIntOffset(50,-50), 40.0F, dist, AoAGiantSerpentHead.razorDamage, 50));
                    t.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(t.bodyPart.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y + GameRandom.globalRandom.getIntOffset(50,-50), 40.0F, dist, AoAGiantSerpentHead.razorDamage, 50));
                    t.bodyPart.getLevel().entityManager.projectiles.add(new SwampBoulderProjectile(t.bodyPart.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y + GameRandom.globalRandom.getIntOffset(50,-50), 40.0F, dist, AoAGiantSerpentHead.razorDamage, 50));
                    mob.swingSound.runAndSend(t.bodyPart.getX(), t.bodyPart.getY());
                }

                this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(0.8F, 2.0F));
            }

            return AINodeResult.SUCCESS;
        }
    }
    public static class ChargeRotation<T extends AoAGiantSerpentHead> extends AINode<T> {
        private int ticker;
        private ChargingCirclingChaserAINode<T> chaserAI;

        public ChargeRotation(ChargingCirclingChaserAINode<T> chaserAI) {
            this.chaserAI = chaserAI;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = 100;
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                --this.ticker;
                if (this.ticker <= 0) {
                    if (!mob.dive && !mob.isUnderground) {
                        mob.diveAbility.runAndSend();
                        this.chaserAI.startCircling(mob, blackboard, target, 100);
                        this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(2.0F, 3.0F));
                    } else {
                        this.chaserAI.startCharge(mob, blackboard, target);
                        float currentAngle = GameMath.getAngle(new Point2D.Float(mob.x - target.x, mob.y - target.y));
                        Point2D.Float dir = GameMath.getAngleDir(currentAngle);
                        mob.appearAbility.runAndSend(mob.x, mob.y, -dir.x, -dir.y);
                        this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(8.0F, 9.0F));
                    }
                }
            }

            return AINodeResult.SUCCESS;
        }
    }
    public static class DiveChargeRotationAI<T extends AoAGiantSerpentHead> extends AINode<T> {
        private int ticker;
        private ChargingCirclingChaserAINode<T> chaserAI;

        public DiveChargeRotationAI(ChargingCirclingChaserAINode<T> chaserAI) {
            this.chaserAI = chaserAI;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = 100;
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                --this.ticker;
                if (this.ticker <= 0) {
                    if (!mob.dive && !mob.isUnderground) {
                        mob.diveAbility.runAndSend();
                        this.chaserAI.startCircling(mob, blackboard, target, 100);
                        this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(2.0F, 3.0F));
                    } else {
                        this.chaserAI.startCharge(mob, blackboard, target);
                        float currentAngle = GameMath.getAngle(new Point2D.Float(mob.x - target.x, mob.y - target.y));
                        Point2D.Float dir = GameMath.getAngleDir(currentAngle);
                        mob.appearAbility.runAndSend(mob.x, mob.y, -dir.x, -dir.y);
                        mob.getLevel().entityManager.addLevelEvent(new SlimeQuakeWarningEvent(mob, (int) mob.x, (int) mob.y, new GameRandom(), 0.0F, 70, 600.0F, 2500, (float)200));
                        mob.getLevel().entityManager.addLevelEvent(new SlimeQuakeEvent(mob, (int) mob.x, (int) mob.y, new GameRandom(), 0.0F, AoAGiantSerpentHead.razorDamage, 60, 50.0F, 600.0F, (float)200));
                        this.ticker = (int)(20.0F * GameRandom.globalRandom.getFloatBetween(8.0F, 9.0F));
                    }
                }
            }

            return AINodeResult.SUCCESS;
        }

    }

    public static class ShootRotationAI<T extends AoAGiantSerpentHead> extends AINode<T> {
        private int ticker;
        private ChargingCirclingChaserAINode<T> chaserAI;

        public ShootRotationAI(ChargingCirclingChaserAINode<T> chaserAI) {
            this.chaserAI = chaserAI;
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            this.ticker = 100;
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (target != null) {
                Point2D.Float targetPoints = new Point2D.Float((float)mob.x, (float)mob.y);
                int rndX = GameRandom.globalRandom.getIntBetween(-75, 75);
                int rndY = GameRandom.globalRandom.getIntBetween(-75, 75);
                targetPoints.x += (float)rndX;
                targetPoints.y += (float)rndY;
                AoADemonsRainProjectile projectile = new AoADemonsRainProjectile(mob.getLevel(), mob, mob.x, mob.y, mob.x, mob.y - 1.0F, 200, 500, AoAGiantSerpentHead.headCollisionDamage,100, targetPoints, false);
                projectile.getUniqueID(GameRandom.globalRandom);
                mob.getLevel().entityManager.projectiles.addHidden(projectile);
                if (mob.getLevel().isServer()) {
                }

            }

            return AINodeResult.SUCCESS;
        }

    }
}
