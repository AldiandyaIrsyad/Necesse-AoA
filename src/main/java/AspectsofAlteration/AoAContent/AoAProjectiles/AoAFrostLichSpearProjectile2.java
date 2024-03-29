package AspectsofAlteration.AoAContent.AoAProjectiles;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class AoAFrostLichSpearProjectile2 extends Projectile {
    private long spawnTime;
    private float startSpeed;

    public AoAFrostLichSpearProjectile2() {
    }

    public AoAFrostLichSpearProjectile2(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.givesLight = true;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startSpeed);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startSpeed = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.height = 18.0F;
        this.startSpeed = this.speed;
        this.setWidth(10.0F);
        this.spawnTime = this.getLevel().getWorldEntity().getTime();
        this.distance = 400;
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        float perc = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F) + 1.0F);
        this.speed = Math.max(10.0F, perc * this.startSpeed);
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (this.getLevel().isServerLevel()) {
            byte projectiles;
            if (mob == null) {
                projectiles = 8;
            } else {
                projectiles = 4;
            }

            float startX = x - this.dx * 2.0F;
            float startY = y - this.dy * 2.0F;
            float angle = (float) GameRandom.globalRandom.nextInt(360);

            for(int i = 0; i < projectiles; ++i) {
                Point2D.Float dir = GameMath.getAngleDir(angle + (float)i * 360.0F / (float)projectiles);
                AoAFrostLichSpearProjectile1 projectile = new AoAFrostLichSpearProjectile1(this.getLevel(), this.getOwner(), startX, startY, startX + dir.x * 100.0F, startY + dir.y * 100.0F, this.startSpeed, 20, this.getDamage().modFinalMultiplier(0.66F), this.knockback);
                if (mob != null) {
                    projectile.startHitCooldown(mob);
                }

                this.getLevel().entityManager.projectiles.add(projectile);
            }

        }
    }

    public Color getParticleColor() {
        return new Color(46, 94, 183);
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 16;
            int drawY = camera.getDrawY(this.y);
            int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 400);
            final TextureDrawOptions options = this.texture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY - (int) this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(anim, 0, 32, 64).light(light).rotate(this.getAngle(), 16, 0).pos(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }
    }

}
