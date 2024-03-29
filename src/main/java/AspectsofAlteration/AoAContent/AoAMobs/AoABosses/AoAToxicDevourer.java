package AspectsofAlteration.AoAContent.AoAMobs.AoABosses;

import AspectsofAlteration.AoAContent.AoAProjectiles.AoAToxicEggProjectile;
import AspectsofAlteration.AoAContent.AoAProjectiles.AoAToxicStingerProjectile;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.*;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.FlyingBossMob;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.*;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.entity.projectile.QueenSpiderSpitProjectile;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public class AoAToxicDevourer extends FlyingBossMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("cavespidergland", 10, 20, MultiTextureMatItem.getGNDData(1)), new ChanceLootItem(0.2F, "queenspidersdancevinyl")});
    public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemInterface[]{new LootItem("spidercharm"), new LootItem("spiderclaw"), new LootItemList(new LootItemInterface[]{new LootItem("webbedgun"), LootItem.between("simplebullet", 50, 100)}), new LootItem("frostpiercer")});
    public static LootTable privateLootTable;
    protected MobHealthScaling scaling = new MobHealthScaling(this);
    private ArrayList<AoAToxicDevourer.SpiderLeg> frontLegs;
    private ArrayList<AoAToxicDevourer.SpiderLeg> backLegs;
    public float currentHeight;
    public EmptyMobAbility roarAbility;
    public IntMobAbility startLaunchAnimation;
    public long launchStartTime;
    public int launchAnimationTime;
    public EmptyMobAbility playSpitSoundAbility;
    public int jumpStartX;
    public int jumpStartY;
    public int jumpEndX;
    public int jumpEndY;
    public boolean isJumping;
    public CoordinateMobAbility startJumpAbility;
    public static GameDamage collisionDamage;
    public static GameDamage spitDamage;
    public static GameDamage landDamage;
    public static GameDamage hatchlingDamage;
    public static MaxHealthGetter MAX_HEALTH;
    public static float SPIT_LINGER_SECONDS;

    public AoAToxicDevourer() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.moveAccuracy = 20;
        this.setSpeed(75.0F);
        this.setArmor(5);
        this.setFriction(2.0F);
        this.setKnockbackModifier(0.0F);
        this.collision = new Rectangle(-60, -60, 120, 90);
        this.hitBox = new Rectangle(-60, -60, 120, 90);
        this.selectBox = new Rectangle(-70, -45, 140, 100);
        this.roarAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
                if (AoAToxicDevourer.this.isClient()) {
                    Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7F).pitch(1.3F));
                }

            }
        });
        this.startLaunchAnimation = (IntMobAbility)this.registerAbility(new IntMobAbility() {
            protected void run(int value) {
                AoAToxicDevourer.this.launchStartTime = AoAToxicDevourer.this.getWorldEntity().getLocalTime();
                AoAToxicDevourer.this.launchAnimationTime = value;
                if (AoAToxicDevourer.this.isClient()) {
                    Screen.playSound(GameResources.spit, SoundEffect.effect(AoAToxicDevourer.this));
                }

            }
        });
        this.playSpitSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
            protected void run() {
                if (AoAToxicDevourer.this.isClient()) {
                    Screen.playSound(GameResources.spit, SoundEffect.effect(AoAToxicDevourer.this));
                }

            }
        });
        this.startJumpAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
            protected void run(int x, int y) {
                AoAToxicDevourer.this.jumpStartX = AoAToxicDevourer.this.getX();
                AoAToxicDevourer.this.jumpStartY = AoAToxicDevourer.this.getY();
                AoAToxicDevourer.this.jumpEndX = x;
                AoAToxicDevourer.this.jumpEndY = y;
                AoAToxicDevourer.this.isJumping = true;
            }
        });
    }

    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isJumping);
        if (this.isJumping) {
            writer.putNextInt(this.jumpStartX);
            writer.putNextInt(this.jumpStartY);
            writer.putNextInt(this.jumpEndX);
            writer.putNextInt(this.jumpEndY);
        }

    }

    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isJumping = reader.getNextBoolean();
        if (this.isJumping) {
            this.jumpStartX = reader.getNextInt();
            this.jumpStartY = reader.getNextInt();
            this.jumpEndX = reader.getNextInt();
            this.jumpEndY = reader.getNextInt();
        }

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

    public void setPos(float x, float y, boolean isDirect) {
        super.setPos(x, y, isDirect);
        if (isDirect && this.backLegs != null && this.frontLegs != null) {
            Iterator var4 = this.backLegs.iterator();

            AoAToxicDevourer.SpiderLeg leg;
            while(var4.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var4.next();
                leg.snapToPosition();
            }

            var4 = this.frontLegs.iterator();

            while(var4.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var4.next();
                leg.snapToPosition();
            }
        }

    }

    public void init() {
        super.init();
        this.frontLegs = new ArrayList();
        this.backLegs = new ArrayList();
        int legCount = 8;
        this.currentHeight = this.getDesiredHeight();
        float[] angles = new float[]{65.0F, 95.0F, 125.0F, 155.0F, -65.0F, -95.0F, -125.0F, -155.0F};

        for(int i = 0; i < legCount; ++i) {
            final float angle = angles[i] - 90.0F;
            float offsetPercent = (float)(i + (i % 2 == 0 ? 4 : 0)) / (float)legCount % 1.0F;
            final Point2D.Float dir = GameMath.getAngleDir(angle);
            float maxLeftAngle = 170.0F;
            float maxRightAngle = 170.0F;
            if (dir.x < 0.0F) {
                maxRightAngle = 0.0F;
            } else if (dir.x > 0.0F) {
                maxLeftAngle = 0.0F;
            }

            AoAToxicDevourer.SpiderLeg leg = new AoAToxicDevourer.SpiderLeg(this, 125.0F, offsetPercent, maxLeftAngle, maxRightAngle) {
                public GamePoint3D getCenterPosition() {
                    int dist = 50;
                    return new GamePoint3D((float) AoAToxicDevourer.this.getDrawX() + dir.x * (float)dist, (float) AoAToxicDevourer.this.getDrawY() + dir.y * (float)dist * 0.5F, (float) AoAToxicDevourer.this.getFlyingHeight());
                }

                public GamePoint3D getDesiredPosition() {
                    Point2D.Float dirx = GameMath.getAngleDir(angle);
                    int dist = 130;
                    float moveMod = Math.min(AoAToxicDevourer.this.getCurrentSpeed() / 250.0F, 1.0F);
                    Point2D.Float moveDir = GameMath.normalize(AoAToxicDevourer.this.dx, AoAToxicDevourer.this.dy);
                    if (moveDir.y < 0.0F) {
                        moveMod *= 1.0F + -moveDir.y * 30.0F / AoAToxicDevourer.this.getSpeed();
                    }

                    return new GamePoint3D((float) AoAToxicDevourer.this.getDrawX() + dirx.x * (float)dist + moveDir.x * (float)dist * moveMod, (float) AoAToxicDevourer.this.getDrawY() + dirx.y * (float)dist + moveDir.y * (float)dist * moveMod, 0.0F);
                }

                public float getJumpHeight() {
                    return AoAToxicDevourer.this.getCurrentJumpHeight();
                }
            };
            if (dir.y < 0.0F) {
                this.backLegs.add(leg);
            } else {
                this.frontLegs.add(leg);
            }
        }

        this.frontLegs.sort(Comparator.comparingDouble((l) -> {
            return (double)l.y;
        }));
        this.backLegs.sort(Comparator.comparingDouble((l) -> {
            return (double)l.y;
        }));
        this.ai = new BehaviourTreeAI(this, new AoAToxicDevourer.SpiderMotherAI(), new FlyingAIMover());
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

    public void tickMovement(float delta) {
        float desiredHeight = this.getDesiredHeight();
        float heightDelta = desiredHeight - this.currentHeight;
        float heightSpeed = Math.abs(heightDelta) * 2.0F + 10.0F;
        float heightToMove = heightSpeed * delta / 250.0F;
        if (Math.abs(heightDelta) < heightToMove) {
            this.currentHeight = desiredHeight;
        } else {
            this.currentHeight += Math.signum(heightDelta) * heightToMove;
        }

        if (this.isJumping) {
            float distToEnd = this.getDistance((float)this.jumpEndX, (float)this.jumpEndY);
            float distToMove = this.getSpeed() * 1.3F * delta / 250.0F;
            if (!(distToEnd < distToMove)) {
                Point2D.Float dir = GameMath.normalize((float)this.jumpEndX - this.x, (float)this.jumpEndY - this.y);
                this.x += dir.x * distToMove;
                this.y += dir.y * distToMove;
            } else {
                int particles = 200;
                float anglePerParticle = 360.0F / (float)particles;

                int halfSize;
                for(int i = 0; i < particles; ++i) {
                    halfSize = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float)Math.sin(Math.toRadians((double)halfSize)) * (float)GameRandom.globalRandom.getIntBetween(100, 200);
                    float dy = (float)Math.cos(Math.toRadians((double)halfSize)) * (float)GameRandom.globalRandom.getIntBetween(100, 200) * 0.8F;
                    this.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8F).color(new Color(50, 50, 50)).heightMoves(0.0F, 30.0F).lifeTime(1000);
                }

                this.x = (float)this.jumpEndX;
                this.y = (float)this.jumpEndY;
                this.isJumping = false;
                this.stopMoving();
                if (this.isServer()) {
                    int size = 300;
                    halfSize = size / 2;
                    Ellipse2D hitBox = new Ellipse2D.Float(this.x - (float)halfSize, this.y - (float)halfSize * 0.8F, (float)size, (float)size * 0.8F);
                    GameUtils.streamTargets(this, GameUtils.rangeTileBounds(this.getX(), this.getY(), 8)).filter((m) -> {
                        return m.canBeHit(this) && hitBox.intersects(m.getHitBox());
                    }).forEach((m) -> {
                        m.isServerHit(landDamage, (float)m.getX() - this.x, (float)m.getY() - this.y, 150.0F, this);
                    });
                }
            }

            this.calcNetworkSmooth(delta);
            Iterator var17 = this.backLegs.iterator();

            AoAToxicDevourer.SpiderLeg leg;
            while(var17.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var17.next();
                leg.snapToPosition();
            }

            var17 = this.frontLegs.iterator();

            while(var17.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var17.next();
                leg.snapToPosition();
            }
        } else {
            super.tickMovement(delta);
            Iterator var14 = this.backLegs.iterator();

            AoAToxicDevourer.SpiderLeg leg;
            while(var14.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var14.next();
                leg.tickMovement(delta);
            }

            var14 = this.frontLegs.iterator();

            while(var14.hasNext()) {
                leg = (AoAToxicDevourer.SpiderLeg)var14.next();
                leg.tickMovement(delta);
            }
        }

    }

    public void clientTick() {
        super.clientTick();
        Screen.setMusic(MusicRegistry.QueenSpidersDance, Screen.MusicPriority.EVENT, 1.5F);
        Screen.registerMobHealthStatusBar(this);
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
        this.setSpeed(70.0F + healthPercInv * 40.0F);
    }

    public void serverTick() {
        super.serverTick();
        this.scaling.serverTick();
        BossNearbyBuff.applyAround(this);
        float healthPercInv = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
        this.setSpeed(70.0F + healthPercInv * 40.0F);
    }

    public int getFlyingHeight() {
        return (int)this.currentHeight;
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
        float height = GameMath.sin(perc * 360.0F) * 10.0F;
        long localTime = this.getWorldEntity().getLocalTime();
        if (this.isJumping) {
            height = 0.0F;
        } else if (localTime < this.launchStartTime + (long)this.launchAnimationTime) {
            float progress = (float)(localTime - this.launchStartTime) / (float)this.launchAnimationTime;
            float endPref = 0.4F;
            float slope = 1.5F;
            height = 10.0F - (float)Math.pow(Math.sin(Math.pow((double)progress * Math.PI, (double)endPref) / Math.pow(Math.PI, (double)(endPref - 1.0F))), (double)slope) * 25.0F;
        }

        return (float)(20 + (int)height);
    }

    public float getCurrentJumpHeight() {
        if (!this.isJumping) {
            return 0.0F;
        } else {
            float totalDist = (float)(new Point2D.Float((float)this.jumpStartX, (float)this.jumpStartY)).distance((double)this.jumpEndX, (double)this.jumpEndY);
            float distToEnd = (float)(new Point2D.Float(this.x, this.y)).distance((double)this.jumpEndX, (double)this.jumpEndY);
            float distPerc = distToEnd / totalDist;
            return GameMath.sin(distPerc * 180.0F) * totalDist / 1.2F;
        }
    }

    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y -= this.getFlyingHeight();
        return selectBox;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for(int i = 0; i < 7; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.queenSpiderDebris, i, 0, 32, this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 15.0F, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    protected void addDrawables(java.util.List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        drawY -= this.getFlyingHeight();
        drawY = (int)((float)drawY - this.getCurrentJumpHeight());
        float rotate = GameMath.limit(this.dx / 10.0F, -10.0F, 10.0F);
        DrawOptions body = MobRegistry.Textures.fromFile("aoatoxicdevourerbody").initDraw().light(light).rotate(rotate, MobRegistry.Textures.fromFile("aoatoxicdevourerbody").getWidth() / 2, (int)((float) MobRegistry.Textures.fromFile("aoatoxicdevourerbody").getHeight() * 0.6F)).posMiddle(drawX, drawY);
        DrawOptions head = MobRegistry.Textures.fromFile("aoatoxicdevourerhead").initDraw().light(light).rotate(rotate, MobRegistry.Textures.fromFile("aoatoxicdevourerhead").getWidth() / 2, (int)((float) MobRegistry.Textures.fromFile("aoatoxicdevourerhead").getHeight() * 0.6F)).posMiddle(drawX, drawY + 24);
        DrawOptionsList legsShadows = new DrawOptionsList();
        DrawOptionsList backLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList backLegsDrawsTop = new DrawOptionsList();
        DrawOptionsList frontLegsDrawBottom = new DrawOptionsList();
        DrawOptionsList frontLegsDrawsTop = new DrawOptionsList();
        Iterator var21 = this.backLegs.iterator();

        AoAToxicDevourer.SpiderLeg leg;
        while(var21.hasNext()) {
            leg = (AoAToxicDevourer.SpiderLeg)var21.next();
            leg.addDrawOptions(legsShadows, backLegsDrawBottom, backLegsDrawsTop, level, camera);
        }

        var21 = this.frontLegs.iterator();

        while(var21.hasNext()) {
            leg = (AoAToxicDevourer.SpiderLeg)var21.next();
            leg.addDrawOptions(legsShadows, frontLegsDrawBottom, frontLegsDrawsTop, level, camera);
        }

        TextureDrawOptions shadowOptions = this.getShadowDrawOptions(x, y, light, camera);
        topList.add((tm) -> {
            shadowOptions.draw();
            legsShadows.draw();
            backLegsDrawBottom.draw();
            backLegsDrawsTop.draw();
            body.draw();
            frontLegsDrawBottom.draw();
            frontLegsDrawsTop.draw();
            head.draw();
        });
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.queenSpider_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 24;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }

    public boolean shouldDrawOnMap() {
        return true;
    }

    public void drawOnMap(TickManager tickManager, int x, int y) {
        super.drawOnMap(tickManager, x, y);
        MobRegistry.Textures.fromFile("aoatoxicdevourerhead").initDraw().size(MobRegistry.Textures.fromFile("aoatoxicdevourerhead").getWidth() / 2, MobRegistry.Textures.fromFile("aoatoxicdevourerhead").getHeight() / 2).posMiddle(x, y).draw();
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
        collisionDamage = new GameDamage(28.0F);
        spitDamage = new GameDamage(24.0F);
        landDamage = new GameDamage(40.0F);
        hatchlingDamage = new GameDamage(23.0F);
        MAX_HEALTH = new MaxHealthGetter(3500, 4000, 4500, 5000, 6000);
        SPIT_LINGER_SECONDS = 60.0F;
    }

    public static class JumpTargetStage<T extends AoAToxicDevourer> extends AINode<T> implements AttackStageInterface<T> {
        public boolean jumped = false;

        public JumpTargetStage() {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.jumped = false;
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob target = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (target == null) {
                return AINodeResult.SUCCESS;
            } else {
                if (!this.jumped) {
                    mob.startJumpAbility.runAndSend((int)(target.x + GameRandom.globalRandom.floatGaussian() * 30.0F), (int)(target.y + GameRandom.globalRandom.floatGaussian() * 30.0F));
                    this.jumped = true;
                }

                return mob.isJumping ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
            }
        }
    }

    public static class SpitStage<T extends AoAToxicDevourer> extends AINode<T> implements AttackStageInterface<T> {
        private int timer;
        private float shootBuffer;
        private boolean reversed;
        private int radius;

        public SpitStage() {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            this.timer += 50;
            if (currentTarget != null) {
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float secondsPerSpit = 0.25F + healthPerc * 0.4F;
                this.shootBuffer += 1.0F / secondsPerSpit / 20.0F;
                if (this.shootBuffer >= 1.0F) {
                    --this.shootBuffer;
                    float healthPercInv = Math.abs(healthPerc - 1.0F);
                    float mod = GameRandom.globalRandom.nextFloat();
                    int targetDist = (int)(140.0F * mod);
                    float angle = (float)GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(currentTarget.x + dir.x * (float)targetDist, currentTarget.y + dir.y * (float)targetDist);
                    int dist = Math.min((int)targetPos.distance((double)mob.x, (double)mob.y), 960);
                    float speed = 60.0F + healthPercInv * 40.0F;
                    mob.getLevel().entityManager.projectiles.add(new QueenSpiderSpitProjectile(mob.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y, speed, dist, AoAToxicDevourer.spitDamage, 50));
                    mob.playSpitSoundAbility.runAndSend();
                }
            }

            if (this.timer >= 4000) {
                blackboard.mover.stopMoving(mob);
                return AINodeResult.SUCCESS;
            } else {
                return AINodeResult.RUNNING;
            }
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
            this.shootBuffer = 0.0F;
            this.reversed = !this.reversed;
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.radius = GameMath.limit((int)mob.getDistance(currentTarget), 200, 400);
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float speedMod = 0.5F + Math.abs(healthPerc - 1.0F) / 2.0F;
                float speed = MobMovementCircleLevelPos.convertToRotSpeed(this.radius, mob.getSpeed() * speedMod);
                blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos(mob, currentTarget.x, currentTarget.y, this.radius, speed, this.reversed));
            }

        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }

    public static class SpitStage2<T extends AoAToxicDevourer> extends AINode<T> implements AttackStageInterface<T> {
        private int timer;
        private float shootBuffer;
        private boolean reversed;
        private int radius;

        public SpitStage2() {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            this.timer += 50;
            if (currentTarget != null) {
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float secondsPerSpit = 0.25F + healthPerc * 0.4F;
                this.shootBuffer += 1.0F / secondsPerSpit / 20.0F;
                if (this.shootBuffer >= 1.0F) {
                    --this.shootBuffer;
                    float healthPercInv = Math.abs(healthPerc - 1.0F);
                    float mod = GameRandom.globalRandom.nextFloat();
                    int targetDist = (int)(140.0F * mod);
                    float angle = (float)GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(currentTarget.x + dir.x * (float)targetDist, currentTarget.y + dir.y * (float)targetDist);
                    int dist = Math.min((int)targetPos.distance((double)mob.x, (double)mob.y), 960);
                    float speed = 60.0F + healthPercInv * 40.0F;
                    mob.getLevel().entityManager.projectiles.add(new SpideriteWaveProjectile(mob.getLevel(),mob.x, mob.y, targetPos.x + GameRandom.globalRandom.getIntBetween(-50,50), targetPos.y + GameRandom.globalRandom.getIntBetween(-50,50), speed, dist * 5, AoAToxicDevourer.spitDamage, mob));
                    mob.playSpitSoundAbility.runAndSend();
                }
            }

            if (this.timer >= 4000) {
                blackboard.mover.stopMoving(mob);
                return AINodeResult.SUCCESS;
            } else {
                return AINodeResult.RUNNING;
            }
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
            this.shootBuffer = 0.0F;
            this.reversed = !this.reversed;
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.radius = GameMath.limit((int)mob.getDistance(currentTarget), 200, 400);
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float speedMod = 0.5F + Math.abs(healthPerc - 1.0F) / 2.0F;
                float speed = MobMovementCircleLevelPos.convertToRotSpeed(this.radius, mob.getSpeed() * speedMod);
                blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos(mob, currentTarget.x, currentTarget.y, this.radius, speed, this.reversed));
            }

        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
    public static class SpitStage3<T extends AoAToxicDevourer> extends AINode<T> implements AttackStageInterface<T> {
        private int timer;
        private float shootBuffer;
        private boolean reversed;
        private int radius;

        public SpitStage3() {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            this.timer += 50;
            if (currentTarget != null) {
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float secondsPerSpit = 0.25F + healthPerc * 0.4F;
                this.shootBuffer += 1.0F / secondsPerSpit / 20.0F;
                if (this.shootBuffer >= 1.0F) {
                    --this.shootBuffer;
                    float healthPercInv = Math.abs(healthPerc - 1.0F);
                    float mod = GameRandom.globalRandom.nextFloat();
                    int targetDist = (int)(140.0F * mod);
                    float angle = (float)GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float(currentTarget.x + dir.x * (float)targetDist, currentTarget.y + dir.y * (float)targetDist);
                    int dist = Math.min((int)targetPos.distance((double)mob.x, (double)mob.y), 960);
                    float speed = 60.0F + healthPercInv * 40.0F;
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicStingerProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicStingerProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicStingerProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicStingerProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicStingerProjectile(mob.getLevel(), mob, mob.x, mob.y, currentTarget.x + GameRandom.globalRandom.getIntBetween(100, -100), currentTarget.y + GameRandom.globalRandom.getIntBetween(100, -100), 120.0F, 640, new GameDamage(65.0F), 50));
                    mob.playSpitSoundAbility.runAndSend();
                }
            }

            if (this.timer >= 4000) {
                blackboard.mover.stopMoving(mob);
                return AINodeResult.SUCCESS;
            } else {
                return AINodeResult.RUNNING;
            }
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.timer = 0;
            this.shootBuffer = 0.0F;
            this.reversed = !this.reversed;
            Mob currentTarget = (Mob)blackboard.getObject(Mob.class, "currentTarget");
            if (currentTarget != null) {
                this.radius = GameMath.limit((int)mob.getDistance(currentTarget), 200, 400);
                float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
                float speedMod = 0.5F + Math.abs(healthPerc - 1.0F) / 2.0F;
                float speed = MobMovementCircleLevelPos.convertToRotSpeed(this.radius, mob.getSpeed() * speedMod);
                blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos(mob, currentTarget.x, currentTarget.y, this.radius, speed, this.reversed));
            }

        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }
    }
    public static class ChargeTargetStage<T extends AoAToxicDevourer> extends FlyToOppositeDirectionAttackStage<T> {
        public ChargeTargetStage() {
            super(true, 250.0F, 0.0F);
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            super.onStarted(mob, blackboard);
            if (blackboard.mover.isMoving()) {
                mob.roarAbility.runAndSend();
                mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, mob, 5.0F, (Attacker)null), true);
            }

        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
            super.onEnded(mob, blackboard);
            mob.buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
        }
    }

    public static class LaunchEggsStage<T extends AoAToxicDevourer> extends AINode<T> implements AttackStageInterface<T> {
        public float buffer;
        public float eggsPerLaunch;
        public int launchCounter;

        public LaunchEggsStage() {
        }

        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        public void init(T mob, Blackboard<T> blackboard) {
        }

        public void onStarted(T mob, Blackboard<T> blackboard) {
            this.buffer = 0.0F;
            this.eggsPerLaunch = 5.0F;
            this.launchCounter = 0;
        }

        public void onEnded(T mob, Blackboard<T> blackboard) {
        }

        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            float launchesPerSecond = 0.7F;
            this.buffer += 1.0F / launchesPerSecond / 20.0F;
            if (this.buffer >= 1.0F) {
                mob.startLaunchAnimation.runAndSend((int)(launchesPerSecond * 1000.0F));

                float eggsIncPerLaunch;
                for(int i = 0; i < (int)this.eggsPerLaunch; ++i) {
                    eggsIncPerLaunch = GameRandom.globalRandom.getFloatBetween(0.7F, 1.0F);
                    int dist = (int)(300.0F * eggsIncPerLaunch);
                    float angle = (float)GameRandom.globalRandom.nextInt(360);
                    Point2D.Float dir = GameMath.getAngleDir(angle);
                    Point2D.Float targetPos = new Point2D.Float((mob.x + dir.x * (float)dist+ GameRandom.globalRandom.getIntBetween(50,-50)), (mob.y + dir.y * (float)dist )+ GameRandom.globalRandom.getIntBetween(50,-50));
                    mob.getLevel().entityManager.projectiles.add(new AoAToxicEggProjectile(mob.getLevel(), mob, mob.x, mob.y, targetPos.x, targetPos.y, 30.0F, dist, new GameDamage(0.0F), 50));
                }

                --this.buffer;
                float healthPercInv = Math.abs(GameMath.limit((float)mob.getHealth() / (float)mob.getMaxHealth(), 0.0F, 1.0F) - 1.0F);
                eggsIncPerLaunch = healthPercInv * 1.3F;
                long clients = GameUtils.streamServerClients(mob.getLevel()).filter((c) -> {
                    return !c.isDead() && mob.getDistance(c.playerMob) < 1280.0F;
                }).count();
                float clientsMod = Math.min(1.0F + (float)(clients - 1L) / 2.0F, 4.0F);
                this.eggsPerLaunch += eggsIncPerLaunch * clientsMod;
                ++this.launchCounter;
                if (this.launchCounter >= 4) {
                    return AINodeResult.SUCCESS;
                }
            }

            return AINodeResult.RUNNING;
        }
    }

    public static class SpiderMotherAI<T extends AoAToxicDevourer> extends SequenceAINode<T> {
        public SpiderMotherAI() {
            this.addChild(new RemoveOnNoTargetNode(100));
            this.addChild(new TargetFinderAINode<T>(3200) {
                public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                    return TargetFinderAINode.streamPlayers(mob, base, distance);
                }
            });
            AttackStageManagerNode<T> attackStages = new AttackStageManagerNode();
            this.addChild(new IsolateRunningAINode(attackStages));
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new AoAToxicDevourer.SpitStage3());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new AoAToxicDevourer.LaunchEggsStage());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 1000);
            }));
            attackStages.addChild(new FlyToOppositeDirectionAttackStage(true, 300.0F, 20.0F));
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new AoAToxicDevourer.SpitStage2());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new FlyToRandomPositionAttackStage(true, 300));
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 1000);
            }));
            attackStages.addChild(new AoAToxicDevourer.ChargeTargetStage());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 1000);
            }));
            attackStages.addChild(new AoAToxicDevourer.ChargeTargetStage());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 1000);
            }));
            attackStages.addChild(new AoAToxicDevourer.SpitStage3());
            attackStages.addChild(new IdleTimeAttackStage((m) -> {
                return this.getIdleTime((T) m, 500);
            }));
            attackStages.addChild(new AoAToxicDevourer.ChargeTargetStage());
        }

        private int getIdleTime(T mob, int maxTime) {
            float healthPerc = (float)mob.getHealth() / (float)mob.getMaxHealth();
            return (int)((float)maxTime * healthPerc);
        }
    }

    public abstract static class SpiderLeg {
        public final Mob mob;
        public float startX;
        public float startY;
        public float x;
        public float y;
        public float nextX;
        public float nextY;
        public boolean isMoving;
        private InverseKinematics ik;
        private List<Line2D.Float> shadowLines;
        public float maxLeftAngle;
        public float maxRightAngle;
        private float moveAtDist;
        private float checkX;
        private float checkY;
        private float distBuffer;

        public SpiderLeg(Mob mob, float moveAtDist, float offsetPercent, float maxLeftAngle, float maxRightAngle) {
            this.mob = mob;
            this.moveAtDist = moveAtDist;
            this.distBuffer = moveAtDist * offsetPercent;
            this.maxLeftAngle = maxLeftAngle;
            this.maxRightAngle = maxRightAngle;
            this.snapToPosition();
            this.checkX = this.x;
            this.checkY = this.y;
        }

        public void snapToPosition() {
            GamePoint3D desiredPosition = this.getDesiredPosition();
            this.x = desiredPosition.x;
            this.y = desiredPosition.y;
            this.nextX = desiredPosition.x;
            this.nextY = desiredPosition.y;
            this.updateIK();
        }

        public void tickMovement(float delta) {
            GamePoint3D centerPos = this.getCenterPosition();
            double checkDist = (new Point2D.Float(centerPos.x, centerPos.y)).distance((double)this.checkX, (double)this.checkY);
            this.distBuffer = (float)((double)this.distBuffer + checkDist);
            this.checkX = centerPos.x;
            this.checkY = centerPos.y;
            if (checkDist == 0.0) {
                this.distBuffer += delta / (this.moveAtDist / 20.0F);
            }

            if (!this.isMoving) {
                GamePoint3D desiredPos = this.getDesiredPosition();
                double desiredDist = (new Point2D.Float(desiredPos.x, desiredPos.y)).distance((double)this.x, (double)this.y);
                if (desiredDist > 175.0) {
                    this.distBuffer += this.moveAtDist;
                }

                if (this.distBuffer >= this.moveAtDist) {
                    this.distBuffer -= this.moveAtDist;
                    if (this.x != desiredPos.x || this.y != desiredPos.y) {
                        this.startX = this.x;
                        this.startY = this.y;
                        this.nextX = desiredPos.x;
                        this.nextY = desiredPos.y;
                        this.isMoving = true;
                    }
                }
            }

            if (this.isMoving) {
                double nextDist = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
                float speed = (float)nextDist * 2.0F + this.mob.getSpeed() * 1.2F;
                float distToMove = speed * delta / 250.0F;
                if (nextDist < (double)distToMove) {
                    if (this.mob.isClient()) {
                        int particles = 20;
                        float anglePerParticle = 360.0F / (float)particles;

                        for(int i = 0; i < particles; ++i) {
                            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                            float dx = (float)Math.sin(Math.toRadians((double)angle)) * (float)GameRandom.globalRandom.getIntBetween(10, 40);
                            float dy = (float)Math.cos(Math.toRadians((double)angle)) * (float)GameRandom.globalRandom.getIntBetween(10, 40) * 0.8F;
                            this.mob.getLevel().entityManager.addParticle(this.x, this.y, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8F).color(new Color(50, 50, 50)).heightMoves(0.0F, 10.0F).lifeTime(500);
                        }

                        Screen.playSound(GameResources.punch, SoundEffect.effect(this.x, this.y).volume(0.4F).pitch(0.8F));
                    }

                    this.x = this.nextX;
                    this.y = this.nextY;
                    this.isMoving = false;
                } else {
                    Point2D.Float dir = GameMath.normalize(this.nextX - this.x, this.nextY - this.y);
                    this.x += dir.x * distToMove;
                    this.y += dir.y * distToMove;
                }
            }

            this.updateIK();
        }

        public void updateIK() {
            if (!this.mob.isServer()) {
                GamePoint3D centerPos = this.getCenterPosition();
                Point2D.Float dir = GameMath.normalize(this.x - centerPos.x, this.y - centerPos.y);
                float jointDistMod = 0.5F;
                float dist = (float)(new Point2D.Float(centerPos.x, centerPos.y)).distance((double)this.x, (double)this.y) * jointDistMod;
                float jointHeight = 40.0F;
                GamePoint3D jointPos = centerPos.dirFromLength(centerPos.x + dir.x * dist, centerPos.y + dir.y * dist, jointHeight, 80.0F);
                GamePoint3D footPos = jointPos.dirFromLength(this.x, this.y, 0.0F, 100.0F);
                float jumpHeight = this.getJumpHeight();
                float perspectiveMod = 0.6F;
                synchronized(this) {
                    this.shadowLines = Collections.synchronizedList(new LinkedList());
                    this.shadowLines.add(new Line2D.Float(centerPos.x, centerPos.y + 18.0F, jointPos.x, jointPos.y - jointPos.height * perspectiveMod));
                    this.shadowLines.add(new Line2D.Float(jointPos.x, jointPos.y - jointPos.height * perspectiveMod, this.x, this.y));
                }

                this.ik = InverseKinematics.startFromPoints(centerPos.x, centerPos.y - centerPos.height * perspectiveMod - jumpHeight, jointPos.x, jointPos.y - jointPos.height * perspectiveMod - jumpHeight, this.maxLeftAngle, this.maxRightAngle);
                this.ik.addJointPoint(footPos.x, footPos.y - footPos.height * perspectiveMod - jumpHeight);
                this.ik.apply(this.x, this.y - this.getCurrentLegLift() - Math.max(jumpHeight - 150.0F, jumpHeight / 4.0F), 0.0F, 2.0F, 500);
            }
        }

        public float getCurrentLegLift() {
            double startDist = (new Point2D.Float(this.startX, this.startY)).distance((double)this.nextX, (double)this.nextY);
            float lift = Math.min((float)startDist / 40.0F, 1.0F) * 20.0F + 5.0F;
            double currentDist = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
            double progress = GameMath.limit(currentDist / startDist, 0.0, 1.0);
            return GameMath.sin((float)progress * 180.0F) * lift;
        }

        public abstract float getJumpHeight();

        public abstract GamePoint3D getDesiredPosition();

        public abstract GamePoint3D getCenterPosition();

        private static Shape generateShadowShape(Iterable<Line2D.Float> lines, float radius) {
            LinkedList<Line2D.Float> reverse = new LinkedList();
            Path2D.Float path = new Path2D.Float();
            Line2D.Float first = null;
            Line2D.Float last = null;
            Iterator var6 = lines.iterator();

            Line2D.Float line;
            Point2D.Float dir;
            Point2D.Float p1;
            Point2D.Float p2;
            Point2D.Float lastDir;
            Point2D.Float lastP1;
            Point2D.Float lastP2;
            Point2D intersectionPoint;
            while(var6.hasNext()) {
                line = (Line2D.Float)var6.next();
                if (first == null) {
                    first = line;
                }

                dir = GameMath.normalize(line.x1 - line.x2, line.y1 - line.y2);
                p1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, dir);
                p2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, dir);
                if (last != null) {
                    lastDir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                    lastP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir);
                    lastP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir);
                    intersectionPoint = GameMath.getIntersectionPoint(new Line2D.Float(p1, p2), new Line2D.Float(lastP1, lastP2), false);
                    if (intersectionPoint != null) {
                        path.lineTo(intersectionPoint.getX(), intersectionPoint.getY());
                    } else {
                        path.lineTo(lastP2.x, lastP2.y);
                        path.lineTo(p1.x, p1.y);
                    }
                } else {
                    path.moveTo(p1.x, p1.y);
                }

                last = line;
                reverse.addFirst(line);
            }

            if (last != null) {
                lastDir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                lastP1 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir);
                path.lineTo(lastP1.x, lastP1.y);
            }

            last = null;

            for(var6 = reverse.iterator(); var6.hasNext(); last = line) {
                line = (Line2D.Float)var6.next();
                dir = GameMath.normalize(line.x2 - line.x1, line.y2 - line.y1);
                p1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, dir);
                p2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, dir);
                if (last != null) {
                    lastDir = GameMath.normalize(last.x2 - last.x1, last.y2 - last.y1);
                    lastP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir);
                    lastP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir);
                    intersectionPoint = GameMath.getIntersectionPoint(new Line2D.Float(p2, p1), new Line2D.Float(lastP2, lastP1), false);
                    if (intersectionPoint != null) {
                        path.lineTo(intersectionPoint.getX(), intersectionPoint.getY());
                    } else {
                        path.lineTo(lastP1.x, lastP1.y);
                        path.lineTo(p2.x, p2.y);
                    }
                } else {
                    path.lineTo(p2.x, p2.y);
                }
            }

            if (last != null) {
                lastDir = GameMath.normalize(last.x2 - last.x1, last.y2 - last.y1);
                lastP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir);
                path.lineTo(lastP1.x, lastP1.y);
            }

            path.closePath();
            return path;
        }

        private static LinkedList<Point2D.Float> generateShadowTriangles(Iterable<Line2D.Float> lines, float radius) {
            LinkedList<Point2D.Float> out = new LinkedList();
            Line2D.Float last = null;

            Line2D.Float line;
            Point2D.Float rightP2;
            for(Iterator var4 = lines.iterator(); var4.hasNext(); last = line) {
                line = (Line2D.Float)var4.next();
                rightP2 = GameMath.normalize(line.x1 - line.x2, line.y1 - line.y2);
                Point2D.Float leftP1 = GameMath.getPerpendicularPoint(line.x1, line.y1, radius, rightP2);
                Point2D.Float leftP2 = GameMath.getPerpendicularPoint(line.x2, line.y2, radius, rightP2);
                Point2D.Float rightP1 = GameMath.getPerpendicularPoint(line.x1, line.y1, -radius, rightP2);
                rightP2 = GameMath.getPerpendicularPoint(line.x2, line.y2, -radius, rightP2);
                if (last != null) {
                    Point2D.Float lastDir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                    Point2D.Float lastLeftP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, radius, lastDir);
                    Point2D.Float lastLeftP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, lastDir);
                    Point2D leftIntersection = GameMath.getIntersectionPoint(new Line2D.Float(leftP1, leftP2), new Line2D.Float(lastLeftP1, lastLeftP2), false);
                    Point2D.Float lastRightP1 = GameMath.getPerpendicularPoint(last.x1, last.y1, -radius, lastDir);
                    Point2D.Float lastRightP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, -radius, lastDir);
                    Point2D rightIntersection = GameMath.getIntersectionPoint(new Line2D.Float(rightP1, rightP2), new Line2D.Float(lastRightP1, lastRightP2), false);
                    if (leftIntersection != null) {
                        out.add(new Point2D.Float((float)leftIntersection.getX(), (float)leftIntersection.getY()));
                        out.add(lastRightP2);
                        out.add(new Point2D.Float((float)leftIntersection.getX(), (float)leftIntersection.getY()));
                        out.add(rightP1);
                    } else if (rightIntersection != null) {
                        out.add(lastLeftP2);
                        out.add(new Point2D.Float((float)rightIntersection.getX(), (float)rightIntersection.getY()));
                        out.add(leftP1);
                        out.add(new Point2D.Float((float)rightIntersection.getX(), (float)rightIntersection.getY()));
                    } else {
                        out.add(leftP1);
                        out.add(rightP1);
                    }
                } else {
                    out.add(leftP1);
                    out.add(rightP1);
                }
            }

            if (last != null) {
                Point2D.Float dir = GameMath.normalize(last.x1 - last.x2, last.y1 - last.y2);
                Point2D.Float leftP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, radius, dir);
                rightP2 = GameMath.getPerpendicularPoint(last.x2, last.y2, -radius, dir);
                out.add(leftP2);
                out.add(rightP2);
            }

            return out;
        }

        public void addDrawOptions(DrawOptionsList legsShadows, DrawOptionsList legsDrawBottom, DrawOptionsList legsDrawsTop, Level level, GameCamera camera) {
            if (!this.mob.isServer()) {
                int legShadowWidth = MobRegistry.Textures.queenSpiderLeg_shadow.getWidth();
                synchronized(this) {
                    Iterator var8 = this.shadowLines.iterator();

                    while(true) {
                        if (!var8.hasNext()) {
                            break;
                        }

                        Line2D.Float shadowLine = (Line2D.Float)var8.next();
                        GameLight light = level.getLightLevel((int)((shadowLine.x1 + shadowLine.x2) / 2.0F / 32.0F), (int)((shadowLine.y1 + shadowLine.y2) / 2.0F / 32.0F));
                        float angle = GameMath.getAngle(new Point2D.Float(shadowLine.x1 - shadowLine.x2, shadowLine.y1 - shadowLine.y2));
                        float length = (float)shadowLine.getP1().distance(shadowLine.getP2());
                        TextureDrawOptions drawOptions = MobRegistry.Textures.queenSpiderLeg_shadow.initDraw().rotate(angle + 90.0F, legShadowWidth / 2, 6).light(light).size(legShadowWidth, (int)length + 16).pos(camera.getDrawX(shadowLine.x1 - (float)legShadowWidth / 2.0F), camera.getDrawY(shadowLine.y1));
                        legsShadows.add(drawOptions);
                    }
                }

                float jumpHeight = this.getJumpHeight();
                int legTextureWidth = MobRegistry.Textures.fromFile("aoatoxicdevourerleg").getWidth();
                Iterator var17 = this.ik.limbs.iterator();

                while(var17.hasNext()) {
                    InverseKinematics.Limb limb = (InverseKinematics.Limb)var17.next();
                    GameLight light = level.getLightLevel((int)((limb.inboundX + limb.outboundX) / 2.0F / 32.0F), (int)(((limb.inboundY + limb.outboundY) / 2.0F + jumpHeight) / 32.0F));
                    TextureDrawOptions drawOptions = MobRegistry.Textures.fromFile("aoatoxicdevourerleg").initDraw().rotate(limb.angle - 90.0F, legTextureWidth / 2, 4).light(light).size(legTextureWidth, (int)limb.length + 16).pos(camera.getDrawX(limb.inboundX - (float)legTextureWidth / 2.0F), camera.getDrawY(limb.inboundY) - 8);
                    if (this.ik.limbs.getLast() == limb) {
                        legsDrawBottom.add(drawOptions);
                    } else {
                        legsDrawsTop.add(drawOptions);
                    }
                }

                if (GlobalData.debugActive()) {
                    legsDrawsTop.add(() -> {
                        Screen.drawCircle(camera.getDrawX(this.x), camera.getDrawY(this.y - this.getCurrentLegLift()), 4, 12, 1.0F, 0.0F, 0.0F, 1.0F, false);
                    });
                    legsDrawsTop.add(() -> {
                        this.ik.drawDebug(camera, Color.RED, Color.GREEN);
                    });
                }

            }
        }
    }
}
