package AspectsofAlteration.AoAContent.AoAMobs.AoABosses;

import AspectsofAlteration.AoA;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.hostile.bosses.BossWormMobBody;
import AspectsofAlteration.AoAContent.AoAMobs.AoABosses.AoAGiantSerpentHead;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class AoAGiantSerpentBody extends BossWormMobBody<AoAGiantSerpentHead, AoAGiantSerpentBody> {
    public Point sprite = new Point(0, 0);
    public int shadowSprite = 0;
    private TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(30);

    public AoAGiantSerpentBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-20, -15, 40, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -60, 64, 64);
    }

    public GameMessage getLocalization() {
        return new LocalMessage("mob", "aoagiantseaserpentmob");
    }

    public GameDamage getCollisionDamage(Mob target) {
        return AoAGiantSerpentHead.bodyCollisionDamage;
    }

    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();

            while(true) {
                while(this.particleSpawner.shouldTick()) {
                    ComputedValue<GameObject> obj = new ComputedValue(() -> {
                        return this.getLevel().getObject(this.getX() / 32, this.getY() / 32);
                    });
                    if (this.height < 20.0F && (((GameObject)obj.get()).isWall || ((GameObject)obj.get()).isRock)) {
                        this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
                    } else if (this.height < 0.0F) {
                        this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 10.0F + 5.0F, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(200);
                    }
                }

                return;
            }
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (this.isVisible()) {
            for(int i = 0; i < 4; ++i) {
                this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fromFile("aoagiantserpentmob"), GameRandom.globalRandom.nextInt(6), 6, 32, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
            }

        }
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y);
            WormMobHead.addDrawable(list, new GameSprite(MobRegistry.Textures.fromFile("aoagiantserpentmob"), this.sprite.x, this.sprite.y, 96), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, drawX, drawY, 64);
            this.addShadowDrawables(tileList, x, y, light, camera);
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(this.shadowSprite, 0, res).light(light).pos(drawX, drawY);
    }
}
