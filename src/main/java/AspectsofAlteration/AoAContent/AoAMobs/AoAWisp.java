package AspectsofAlteration.AoAContent.AoAMobs;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

public class AoAWisp extends HostileMob {

    // Loaded in examplemod.ExampleMod.initResources()
    public static GameTexture texture;
    public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(60);
    public ParticleTypeSwitcher particleTypes;
    public static LootTable lootTable = new LootTable(
    );

    // MUST HAVE an empty constructor
    public AoAWisp() {
        super(50);
        setSpeed(25);
        setFriction(3);
        this.particleTypes = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

        // Hitbox, collision box, and select box (for hovering)
        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-14, -12, 28, 24);
        selectBox = new Rectangle(-14, -7 - 34, 28, 48);
    }

    public void clientTick() {
        super.clientTick();
        this.particleTicks.gameTick();
        while(this.particleTicks.shouldTick()) {
            this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 150.0F, -150.0F);
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0F, (this.y + 20) + GameRandom.globalRandom.floatGaussian() * 5.0F, this.particleTypes.next()).movesConstant(this.dx / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, this.dy / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F).color(new Color(104, 179, 215)).height(20.0F).lifeTime(1000);
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0F, (this.y + 20) + GameRandom.globalRandom.floatGaussian() * 5.0F, this.particleTypes.next()).movesConstant(this.dx / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, this.dy / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F).color(new Color(0, 255, 178)).height(20.0F).lifeTime(1000);
        }

    }
    @Override
    public void init() {
        super.init();
        // Setup AI
        ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 12 * 32, new GameDamage(25), 25, 40000));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        // Spawn flesh debris particles
        for (int i = 0; i < 4; i++) {
            getLevel().entityManager.addParticle(new FleshParticle(
                    getLevel(), texture,
                    GameRandom.globalRandom.nextInt(5), // Randomize between the debris sprites
                    8,
                    32,
                    x, y, 0f, // Position
                    knockbackX, knockbackY // Basically start speed of the particles
            ), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        // Tile positions are basically level positions divided by 32. getTileX() does this for us etc.
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 32;

        // A helper method to get the sprite of the current animation/direction of this mob
        Point sprite = getAnimSprite(x, y, dir);

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64)
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        addShadowDrawables(tileList, x, y, light, camera);
    }

    @Override
    public int getRockSpeed() {
        // Change the speed at which this mobs animation plays
        return 20;
    }
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of((new ModifierValue(BuffModifiers.FRICTION, 0.0F)).min(0.75F),new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F).max(0.0F));
    }


}
