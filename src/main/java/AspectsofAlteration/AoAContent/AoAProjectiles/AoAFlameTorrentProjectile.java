package AspectsofAlteration.AoAContent.AoAProjectiles;

import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

    public class AoAFlameTorrentProjectile extends FollowingProjectile {
        public AoAFlameTorrentProjectile() {
        }

        public AoAFlameTorrentProjectile(Level level, Mob owner, float x, float y, Mob target, GameDamage damage) {
            this.setLevel(level);
            this.x = x;
            this.y = y;
            this.speed = 130.0F;
            this.setTarget(target.x, target.y);
            this.target = target;
            this.setDamage(damage);
            this.knockback = 0;
            this.setDistance(1200);
            this.setOwner(owner);
        }

        public AoAFlameTorrentProjectile(Level level, Mob owner, Mob target, GameDamage damage) {
            this(level, owner, owner.x, owner.y, target, damage);
        }

        public void init() {
            super.init();
            this.turnSpeed = 0.13F;
            this.givesLight = true;
            this.height = 16.0F;
            this.piercing = 0;
            this.isSolid = false;
            this.particleDirOffset = -24.0F;
            this.trailOffset = -24.0F;
            this.setWidth(8.0F);
        }

        public float getTurnSpeed(int targetX, int targetY, float delta) {
            return this.getTurnSpeed(delta) * this.invDynamicTurnSpeedMod(targetX, targetY, (float)this.getTurnRadius());
        }

        public Color getParticleColor() {
            return new Color(232, 76, 17);
        }

        public Trail getTrail() {
            return new Trail(this, this.getLevel(), new Color(253, 46, 0), 26.0F, 500, 18.0F);
        }

        public void updateTarget() {
            if (!this.isClient()) {
                if (this.target != null && this.target != this.getOwner()) {
                    if (!this.isSamePlace(this.target) || ((Mob)this.target).getDistance(this.getOwner()) > 960.0F) {
                        this.target = this.getOwner();
                        this.sendServerTargetUpdate();
                    }
                } else {
                    ServerClient target = (ServerClient) GameUtils.streamServerClients(this.getLevel()).min(Comparator.comparing((m) -> {
                        return m.playerMob.getDistance(this.getOwner());
                    })).orElse((ServerClient) null);
                    if (target != null && target.playerMob != this.getOwner() && target.playerMob.getDistance(this.getOwner()) < 960.0F) {
                        this.target = target.playerMob;
                        this.sendServerTargetUpdate();
                    }
                }

            }
        }

        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            if (!this.removed()) {
                GameLight light = level.getLightLevel(this);
                int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
                int drawY = camera.getDrawY(this.y);
                final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
                list.add(new EntityDrawable(this) {
                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                });
                this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
            }
        }
    }
