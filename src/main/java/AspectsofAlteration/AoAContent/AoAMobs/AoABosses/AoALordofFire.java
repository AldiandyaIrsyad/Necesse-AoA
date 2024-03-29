package AspectsofAlteration.AoAContent.AoAMobs.AoABosses;

import AspectsofAlteration.AoAContent.AoALevelEvents.AoAChaoticBombAttackEvent;
import AspectsofAlteration.AoAContent.AoAProjectiles.*;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.GlaiveShowAttackEvent;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import AspectsofAlteration.AoAContent.AoAMobs.AoABosses.AoALordofFire;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoVolleyProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class AoALordofFire extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(0.2F, "milleniumvinyl")});
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemInterface[]{new LootItem("aoaignusblade"), new LootItem("aoasunblessedamulett"), new LootItem("aoaflamingopalnecklace"), new LootItem("aoasearcherseye")});
    public static LootTable privateLootTable;
    public static MaxHealthGetter MAX_HEALTH;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    public LinkedList<Mob> spawnedMobs = new LinkedList();
    public boolean attackingAnimation;
    public final EmptyMobAbility magicSoundAbility;
    public final EmptyMobAbility jingleSoundAbility;
    public final EmptyMobAbility roarSoundAbility;
    public final BooleanMobAbility attackingAnimationAbility;
    public static GameDamage collisionDamage;
    public static GameDamage cryoQuakeDamage;
    public static GameDamage cryoShardDamage;
    public static GameDamage cryoWaveDamage;
    public static GameDamage cryoVolleyDamage;

    public AoALordofFire() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 60;
        this.setSpeed(110.0F);
        this.setArmor(35);
        this.setFriction(1.0F);
        this.setKnockbackModifier(0.0F);
        this.collision = new Rectangle(-40, -80, 80, 105);
        this.hitBox = new Rectangle(-40, -80, 80, 105);
        this.selectBox = new Rectangle(-40, -90, 80, 120);
        this.magicSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
                if (AoALordofFire.this.isClient()) {
                    Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(AoALordofFire.this));
                }

            }
        });
        this.jingleSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
                if (AoALordofFire.this.isClient()) {
                    float pitch = (Float) GameRandom.globalRandom.getOneOf(new Float[]{1.0F, 1.05F});
                    Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(AoALordofFire.this).pitch(pitch));
                }

            }
        });
        this.roarSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
                if (AoALordofFire.this.isClient()) {
                    Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7F).pitch(1.3F));
                }

            }
        });
        this.attackingAnimationAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
            protected void run(boolean value) {
                AoALordofFire.this.attackingAnimation = value;
            }
        });
    }

    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.attackingAnimation);
    }

    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.attackingAnimation = reader.getNextBoolean();
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

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI(this, new AoALordofFire.CryoQueenAI(), new FlyingAIMover());
        if (this.isClient()) {
            Screen.playSound(GameResources.roar, SoundEffect.globalEffect());
        }

    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public LootTable getPrivateLootTable() {
        return privateLootTable;
    }

    public boolean canBePushed(Mob other) {
        return false;
    }

    public GameDamage getCollisionDamage(Mob target) {
        return collisionDamage;
    }

    public int getCollisionKnockback(Mob target) {
        return 150;
    }

    public int getMaxHealth() {
        return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
    }

    public void setFacingDir(float deltaX, float deltaY) {
        if (deltaX < 0.0F) {
            this.dir = 1;
        } else if (deltaX > 0.0F) {
            this.dir = 0;
        }

    }

    public void clientTick() {
        super.clientTick();
        Screen.setMusic(MusicRegistry.Millenium, Screen.MusicPriority.EVENT, 1.5F);
        Screen.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
        this.setSpeed(110.0F + healthPercInv * 70.0F);
    }

    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
        this.setSpeed(110.0F + healthPercInv * 70.0F);
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for(int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fromFile("aoafirelord"), i, 17, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    protected void playHitSound() {
        float pitch = (Float)GameRandom.globalRandom.getOneOf(new Float[]{0.95F, 1.0F, 1.05F});
        Screen.playSound(GameResources.jinglehit, SoundEffect.effect(this).pitch(pitch));
    }

    protected void playDeathSound() {
        this.playHitSound();
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 64;
        int drawY = camera.getDrawY(y) - 100;
        int frame = GameUtils.getAnim(this.getWorldEntity().getTime(), 5, 750);
        DrawOptions body = MobRegistry.Textures.fromFile("aoafirelord").initDraw().sprite(frame, this.attackingAnimation ? 0 : 1, 128).size(128, 128).light(light).mirror(this.dir != 0, false).pos(drawX, drawY);
        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(x, y, light, camera);
        topList.add((tm) -> {
            shadowOptions.draw();
            body.draw();
        });
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.fromFile("disruptor");
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 20;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, int x, int y) {
        super.drawOnMap(tickManager, x, y);
        int drawX = x - 16;
        int drawY = y - 16;
        MobRegistry.Textures.fromFile("chaoticmagus").initDraw().sprite(0, 7, 64).size(32, 32).mirror(this.dir != 0, false).draw(drawX, drawY);
    }

    public Rectangle drawOnMapBox() {
        return new Rectangle(-16, -16, 32, 32);
    }

    public GameTooltips getMapTooltips() {
        return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
    }

    public boolean isSecondStage() {
        float healthPerc = (float)this.getHealth() / (float)this.getMaxHealth();
        return healthPerc < 0.5F;
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
        privateLootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((mob) -> {
            return mob.getLevel() == null || !mob.getLevel().isIncursionLevel;
        }, new LootItemInterface[]{uniqueDrops})});
        MAX_HEALTH = new MaxHealthGetter(8000, 14000, 18000, 21000, 26000);
        collisionDamage = new GameDamage(80.0F);
        cryoQuakeDamage = new GameDamage(70.0F);
        cryoShardDamage = new GameDamage(60.0F);
        cryoWaveDamage = new GameDamage(70.0F);
        cryoVolleyDamage = new GameDamage(70.0F);
    }

    public static class CryoPillar extends GroundPillar {
        public GameTextureSection texture;
        public boolean mirror;

        public CryoPillar(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            this.mirror = GameRandom.globalRandom.nextBoolean();
            this.texture = MobRegistry.Textures.fromFile("aoafirelord") == null ? null : (GameTextureSection)GameRandom.globalRandom.getOneOf(new GameTextureSection[]{(new GameTextureSection(MobRegistry.Textures.fromFile("aoafirelord"))).sprite(0, 6, 64), (new GameTextureSection(MobRegistry.Textures.fromFile("aoafirelord"))).sprite(1, 6, 64), (new GameTextureSection(MobRegistry.Textures.fromFile("aoafirelord"))).sprite(2, 6, 64)});
            this.behaviour = new TimedBehaviour(300, 200, 800);
        }

        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(this.x / 32, this.y / 32);
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
    public static class CryoVolleyRotation3<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;

        public CryoVolleyRotation3() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            ++this.ticker;
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int maxTicks = 10 + (int)(20.0F * healthPerc * 1.5F);
            return this.ticker < maxTicks ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.attackingAnimationAbility.runAndSend(false);
            LinkedList<Float> firedAngles = new LinkedList();
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int speed = 150 + (int)(Math.abs(healthPerc - 1.0F) * 170.0F);
            GameUtils.streamServerClients(mob.getLevel()).map((c) -> {
                return c.playerMob;
            }).filter((m) -> {
                return mob.getDistance(m) < 1120.0F;
            }).forEach((target) -> {
                float angleToTarget = Projectile.getAngleToTarget(mob.x, mob.y, (float)target.getX(), (float)target.getY());
                if (firedAngles.stream().noneMatch((f) -> {
                    return Math.abs(GameMath.getAngleDifference(angleToTarget, f)) < 35.0F;
                })) {
                    Point2D.Float dir = GameMath.getAngleDir(angleToTarget + 90.0F);
                    int missiles = 1;
                    int perpRange = 150;
                    int perpPerMissile = perpRange / missiles;

                    for(int i = 0; i < missiles; ++i) {
                        int offsetDistance = GameRandom.globalRandom.getIntBetween(50, 100);
                        Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                        int perp = i * perpPerMissile - perpRange / 2;
                        offset = GameMath.getPerpendicularPoint(offset, (float)perp, dir);
                        Point2D.Float targetPos = new Point2D.Float(target.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), target.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                        float var10003 = mob.x + offset.x;
                        mob.jingleSoundAbility.runAndSend();
                        mob.getLevel().entityManager.projectiles.add(new AoAFlameBladeProjectile((float)mob.x, (float)mob.y, angleToTarget -20F, (float)2000, (int)2000, AoALordofFire.collisionDamage, mob));
                        mob.getLevel().entityManager.projectiles.add(new AoAFlameBladeProjectile((float)mob.x, (float)mob.y, angleToTarget -10F, (float)2000, (int)2000, AoALordofFire.collisionDamage, mob));
                        mob.getLevel().entityManager.projectiles.add(new AoAFlameBladeProjectile((float)mob.x, (float)mob.y, angleToTarget , (float)2000, (int)2000, AoALordofFire.collisionDamage, mob));
                        mob.getLevel().entityManager.projectiles.add(new AoAFlameBladeProjectile((float)mob.x, (float)mob.y, angleToTarget + 10F, (float)2000, (int)2000, AoALordofFire.collisionDamage, mob));
                        mob.getLevel().entityManager.projectiles.add(new AoAFlameBladeProjectile((float)mob.x, (float)mob.y, angleToTarget + 20F, (float)2000, (int)2000, AoALordofFire.collisionDamage, mob));
                    }

                    firedAngles.add(angleToTarget);
                }

            });
            mob.jingleSoundAbility.runAndSend();
        }
    }
    public static class CryoVolleyRotation<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;

        public CryoVolleyRotation() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            ++this.ticker;
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int maxTicks = 10 + (int)(20.0F * healthPerc * 1.5F);
            return this.ticker < maxTicks ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.attackingAnimationAbility.runAndSend(false);
            LinkedList<Float> firedAngles = new LinkedList();
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int speed = 150 + (int)(Math.abs(healthPerc - 1.0F) * 170.0F);
            GameUtils.streamServerClients(mob.getLevel()).map((c) -> {
                return c.playerMob;
            }).filter((m) -> {
                return mob.getDistance(m) < 1120.0F;
            }).forEach((target) -> {
                float angleToTarget = Projectile.getAngleToTarget(mob.x, mob.y, (float)target.getX(), (float)target.getY());
                if (firedAngles.stream().noneMatch((f) -> {
                    return Math.abs(GameMath.getAngleDifference(angleToTarget, f)) < 35.0F;
                })) {
                    Point2D.Float dir = GameMath.getAngleDir(angleToTarget + 90.0F);
                    int missiles = 1;
                    int perpRange = 150;
                    int perpPerMissile = perpRange / missiles;

                    for(int i = 0; i < missiles; ++i) {
                        int offsetDistance = GameRandom.globalRandom.getIntBetween(50, 100);
                        Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                        int perp = i * perpPerMissile - perpRange / 2;
                        offset = GameMath.getPerpendicularPoint(offset, (float)perp, dir);
                        Point2D.Float targetPos = new Point2D.Float(target.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), target.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                        float var10003 = mob.x + offset.x;
                        mob.jingleSoundAbility.runAndSend();
                        mob.getLevel().entityManager.projectiles.add(new AoAIgnusWave(mob.getLevel(), mob.x, mob.y, (float)targetPos.x, (float)targetPos.y, 200, 2000, AoALordofFire.collisionDamage, mob));
                    }

                    firedAngles.add(angleToTarget);
                }

            });
            mob.jingleSoundAbility.runAndSend();
        }
    }
    public static class CryoVolleyRotation2<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;

        public CryoVolleyRotation2() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            ++this.ticker;
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int maxTicks = 10 + (int)(20.0F * healthPerc * 1.5F);
            return this.ticker < maxTicks ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.attackingAnimationAbility.runAndSend(false);
            LinkedList<Float> firedAngles = new LinkedList();
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int speed = 150 + (int)(Math.abs(healthPerc - 1.0F) * 170.0F);
            GameUtils.streamServerClients(mob.getLevel()).map((c) -> {
                return c.playerMob;
            }).filter((m) -> {
                return mob.getDistance(m) < 1120.0F;
            }).forEach((target) -> {
                float angleToTarget = Projectile.getAngleToTarget(mob.x, mob.y, (float)target.getX(), (float)target.getY());
                if (firedAngles.stream().noneMatch((f) -> {
                    return Math.abs(GameMath.getAngleDifference(angleToTarget, f)) < 35.0F;
                })) {
                    Point2D.Float dir = GameMath.getAngleDir(angleToTarget + 90.0F);
                    int missiles = 20;
                    int perpRange = 150;
                    int perpPerMissile = perpRange / missiles;

                    for(int i = 0; i < missiles; ++i) {
                        int offsetDistance = GameRandom.globalRandom.getIntBetween(50, 100);
                        Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                        int perp = i * perpPerMissile - perpRange / 2;
                        offset = GameMath.getPerpendicularPoint(offset, (float)perp, dir);
                        Point2D.Float targetPos = new Point2D.Float(target.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), target.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                        float var10003 = mob.x + offset.x;
                        mob.jingleSoundAbility.runAndSend();
                        mob.getLevel().entityManager.addLevelEvent(new AoAChaoticBombAttackEvent(mob,(int)target.x + GameRandom.globalRandom.getIntBetween(-300,300), (int)target.y + GameRandom.globalRandom.getIntBetween(-300,300), GameRandom.globalRandom, AoALordofFire.collisionDamage));
                    }

                    firedAngles.add(angleToTarget);
                }

            });
            mob.jingleSoundAbility.runAndSend();
        }
    }
    public static class CryoShardRotation<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;
        private int timerBuffer;
        private boolean reversed;

        public CryoShardRotation() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
            this.timerBuffer = 0;
            this.reversed = !this.reversed;
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                int distance = GameMath.limit((int)mob.getDistance(currentTarget), 100, 500);
                mob.setMovement(new MobMovementCircleLevelPos(mob, currentTarget.x, currentTarget.y, distance, 2.0F, this.reversed));
            }

        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.timerBuffer += 50;
                float healthPerc = (float) mob.getHealth() / (float) mob.getMaxHealth();
                int msPerShard = 200;
                if (this.timerBuffer > msPerShard) {
                    mob.jingleSoundAbility.runAndSend();
                }

                while (this.timerBuffer > msPerShard) {
                    int speed = 100 + (int) (Math.abs(healthPerc - 1.0F) * 160.0F);
                    this.timerBuffer -= msPerShard;
                    mob.getLevel().entityManager.projectiles.add(new AoAFlameProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAFlameProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAFlameProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                }
            }

            ++this.ticker;
            return this.ticker <= 100 ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.stopMoving();
            mob.attackingAnimationAbility.runAndSend(false);
        }
    }
    public static class CryoVolleyRotation5<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;

        public CryoVolleyRotation5() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            ++this.ticker;
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int maxTicks = 10 + (int)(20.0F * healthPerc * 1.5F);
            return this.ticker < maxTicks ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.attackingAnimationAbility.runAndSend(false);
            LinkedList<Float> firedAngles = new LinkedList();
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            int speed = 150 + (int)(Math.abs(healthPerc - 1.0F) * 170.0F);
            GameUtils.streamServerClients(mob.getLevel()).map((c) -> {
                return c.playerMob;
            }).filter((m) -> {
                return mob.getDistance(m) < 1120.0F;
            }).forEach((target) -> {
                float angleToTarget = Projectile.getAngleToTarget(mob.x, mob.y, (float)target.getX(), (float)target.getY());
                if (firedAngles.stream().noneMatch((f) -> {
                    return Math.abs(GameMath.getAngleDifference(angleToTarget, f)) < 35.0F;
                })) {
                    Point2D.Float dir = GameMath.getAngleDir(angleToTarget + 90.0F);
                    int missiles = 100;
                    int perpRange = 1500;
                    int perpPerMissile = perpRange / missiles;

                    for(int i = 0; i < missiles; ++i) {
                        int offsetDistance = GameRandom.globalRandom.getIntBetween(50, 100);
                        Point2D.Float offset = new Point2D.Float(dir.x * (float)offsetDistance, dir.y * (float)offsetDistance);
                        int perp = i * perpPerMissile - perpRange / 2;
                        offset = GameMath.getPerpendicularPoint(offset, (float)perp, dir.x, dir.y);
                        Point2D.Float targetPos = new Point2D.Float(target.x + (float)GameRandom.globalRandom.getIntBetween(-50, 50), target.y + (float)GameRandom.globalRandom.getIntBetween(-50, 50));
                        float var10003 = mob.x + offset.x;
                        mob.jingleSoundAbility.runAndSend();
                        mob.getLevel().entityManager.projectiles.add(new AoAMoltenVolleyProjectile(var10003, mob.y + offset.y - 30.0F, targetPos.x, targetPos.y, (float)speed, 1440, AoALordofFire.cryoVolleyDamage, 100, mob));
                    }

                    firedAngles.add(angleToTarget);
                }

            });
            mob.jingleSoundAbility.runAndSend();
        }
    }

    public static class CryoShardRotation2<T extends AoALordofFire> extends RunningAINode<T> {
        private int ticker;
        private int timerBuffer;
        private boolean reversed;

        public CryoShardRotation2() {
        }

        public void start(T mob, Blackboard<T> blackboard) {
            blackboard.mover.stopMoving(mob);
            mob.attackingAnimationAbility.runAndSend(true);
            this.ticker = 0;
            this.timerBuffer = 0;
            this.reversed = !this.reversed;
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                int distance = GameMath.limit((int)mob.getDistance(currentTarget), 100, 500);
                mob.setMovement(new MobMovementCircleLevelPos(mob, currentTarget.x, currentTarget.y, distance, 0.2F, this.reversed));
            }

        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.timerBuffer += 50;
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                int msPerShard = 600 - (int)healthPerc;
                if (this.timerBuffer > msPerShard) {
                    mob.jingleSoundAbility.runAndSend();
                }

                while(this.timerBuffer > msPerShard) {
                    int speed = 100 + (int)(Math.abs(healthPerc - 1.0F) * 160.0F);
                    this.timerBuffer -= msPerShard;
                    mob.getLevel().entityManager.projectiles.add(new AoAFlameTorrentProjectile(mob.getLevel(),mob,currentTarget,AoALordofFire.collisionDamage));
                }
            }

            ++this.ticker;
            return this.ticker <= 60 ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
            mob.stopMoving();
            mob.attackingAnimationAbility.runAndSend(false);
        }
    }


    public static class MoveToRandomPosition<T extends Mob> extends RunningAINode<T> {
        public int baseDistance;
        public boolean isRunningWhileMoving;

        public MoveToRandomPosition(boolean isRunningWhileMoving, int baseDistance) {
            this.isRunningWhileMoving = isRunningWhileMoving;
            this.baseDistance = baseDistance;
        }

        public void start(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            Point2D.Float base = new Point2D.Float(mob.x, mob.y);
            if (currentTarget != null) {
                base = new Point2D.Float(currentTarget.x, currentTarget.y);
            }

            Point2D.Float pos = new Point2D.Float(mob.x, mob.y);

            for(int i = 0; i < 10; ++i) {
                int randomAngle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float angleDir = GameMath.getAngleDir((float)randomAngle);
                pos = new Point2D.Float(base.x + angleDir.x * (float)this.baseDistance, base.y + angleDir.y * (float)this.baseDistance);
                if (mob.getDistance(pos.x, pos.y) >= (float)this.baseDistance / 4.0F) {
                    break;
                }
            }

            blackboard.mover.directMoveTo(this, (int)pos.x, (int)pos.y);
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            return this.isRunningWhileMoving && blackboard.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class IdleTime<T extends Mob> extends RunningAINode<T> {
        public int msToIdle;
        private int timer;

        public IdleTime(int msToIdle) {
            this.msToIdle = msToIdle;
        }

        public void start(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
        }

        public AINodeResult tickRunning(T mob, Blackboard<T> blackboard) {
            this.timer += 50;
            return this.timer <= this.msToIdle ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
        }

        public void end(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class CryoQueenAI<T extends AoALordofFire> extends SequenceAINode<T> {
        public CryoQueenAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            SequenceAINode<T> rotations = new SequenceAINode();
            rotations.addChild(new AoALordofFire.IdleTime(2000));
            rotations.addChild(new AoALordofFire.CryoVolleyRotation3());
            rotations.addChild(new AoALordofFire.IdleTime(700));
            rotations.addChild(new AoALordofFire.MoveToRandomPosition(true, 300));
            rotations.addChild(new AoALordofFire.IdleTime(100));
            rotations.addChild(new AoALordofFire.CryoShardRotation());
            rotations.addChild(new AoALordofFire.IdleTime(700));
            rotations.addChild(new AoALordofFire.CryoVolleyRotation3());
            rotations.addChild(new AoALordofFire.IdleTime(700));
            rotations.addChild(new AoALordofFire.MoveToRandomPosition(true, 100));
            rotations.addChild(new AoALordofFire.CryoVolleyRotation());
            rotations.addChild(new AoALordofFire.IdleTime(100));
            rotations.addChild(new AoALordofFire.CryoShardRotation2());
            rotations.addChild(new AoALordofFire.IdleTime(2000));
            rotations.addChild(new AoALordofFire.MoveToRandomPosition(true, 300));
            rotations.addChild(new AoALordofFire.CryoVolleyRotation3());
            rotations.addChild(new AoALordofFire.IdleTime(700));
            rotations.addChild(new AoALordofFire.IdleTime(100));
            rotations.addChild(new AoALordofFire.CryoVolleyRotation());
            rotations.addChild(new AoALordofFire.IdleTime(700));

            for(int i = 0; i < 3; ++i) {
                rotations.addChild(new AoALordofFire.MoveToRandomPosition(false, 100));
                rotations.addChild(new AoALordofFire.IdleTime(100));
                rotations.addChild(new AoALordofFire.CryoVolleyRotation5());
            }

            this.addChild(new IsolateRunningAINode(rotations));
        }
        public Stream<ModifierValue<?>> getDefaultModifiers() {
            return Stream.of((new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F).max(0.0F)));
        }
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.onRemoved((e) -> {
                mob.spawnedMobs.forEach(Mob::remove);
            });
        }
    }
}
