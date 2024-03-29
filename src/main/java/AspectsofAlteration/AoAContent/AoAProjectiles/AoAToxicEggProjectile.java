package AspectsofAlteration.AoAContent.AoAProjectiles;

import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class AoAToxicEggProjectile extends Projectile {
    protected long spawnTime;

    public AoAToxicEggProjectile() {
    }

    public AoAToxicEggProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
    }

    public AoAToxicEggProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback);
    }

    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.trailOffset = 0.0F;
    }

    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
        float travelPercInv = Math.abs(travelPerc - 1.0F);
        float heightF = GameMath.sin(travelPerc * 180.0F);
        this.height = (float)((int)(heightF * 200.0F + 50.0F * travelPercInv));
        return out;
    }

    public Color getParticleColor() {
        return new Color(164, 141, 197);
    }

    public Trail getTrail() {
        return null;
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            Mob owner = this.getOwner();
            if (owner != null && !owner.removed()) {
                Mob spiderHatchling = MobRegistry.getMob("aoatoxicspider", this.getLevel());
                if (!spiderHatchling.collidesWith(this.getLevel(), (int)x, (int)y)) {
                    this.getLevel().entityManager.addMob(spiderHatchling, (float)((int)x), (float)((int)y));
                }
            }

        }
    }

    protected void spawnDeathParticles() {
        for(int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), this.texture, GameRandom.globalRandom.nextInt(4), 1, 32, this.x, this.y, 10.0F, this.dx, this.dy), Particle.GType.IMPORTANT_COSMETIC);
        }

        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            for(int i = 0; i < 10; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir((float)angle);
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.y).color(this.getParticleColor()).height(this.getHeight());
            }
        }

        Float pitch = (Float)GameRandom.globalRandom.getOneOf(new Float[]{0.9F, 0.95F, 1.0F});
        Screen.playSound(GameResources.crackdeath, SoundEffect.effect(this.x, this.y).volume(0.7F).pitch(pitch));
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
            int drawX = camera.getDrawX(this.x) - 16;
            int drawY = camera.getDrawY(this.y) - 16;
            float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
            if (this.dx < 0.0F) {
                angle = -angle;
            }

            float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F);
            int sprite = (int)(travelPerc * 4.0F);
            TextureDrawOptions options = this.texture.initDraw().sprite(sprite, 0, 32).light(light).rotate(angle, 16, 16).pos(drawX, drawY - (int)this.getHeight());
            float shadowAlpha = Math.abs(GameMath.limit(this.height / 300.0F, 0.0F, 1.0F) - 1.0F);
            int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
            int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().light(light).rotate(angle).alpha(shadowAlpha).pos(shadowX, shadowY);
            topList.add((tm) -> {
                shadowOptions.draw();
                options.draw();
            });
        }
    }
}
