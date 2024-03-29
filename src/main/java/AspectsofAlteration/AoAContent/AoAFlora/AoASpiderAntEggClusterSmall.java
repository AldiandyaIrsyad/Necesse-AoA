package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.Screen;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SpiderEggObjectEntity;
import necesse.entity.particle.Particle.GType;
import necesse.entity.particle.SpiderEggBrokenParticle;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AoASpiderAntEggClusterSmall extends GameObject {
    private GameTexture texture;
    private GameTexture brokenTexture;

    public AoASpiderAntEggClusterSmall() {
        super(new Rectangle(32, 32));
        this.drawDamage = false;
        this.objectHealth = 10;
        this.attackThrough = true;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoaantspidereggsmallcluster");
        this.brokenTexture = GameTexture.fromFile("objects/aoaantspidereggsmallcluster_broken");
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture drawnTexture = this.texture;
        ObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX, tileY);
        if (objectEntity instanceof AoASpiderAntEggObjectEntity && ((AoASpiderAntEggObjectEntity)objectEntity).isBroken) {
            drawnTexture = this.brokenTexture;
        }

        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 24;
        final TextureDrawOptions options = drawnTexture.initDraw().sprite(0, 0, 64).light(light).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new AoASpiderAntEggObjectEntity(level, x, y);
    }

    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        this.playDamageSound(level, x, y, true);
    }

    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        Screen.playSound(GameResources.slimesplash, SoundEffect.effect((float)(x * 32 + 16), (float)(y * 32 + 16)));
    }

    public void onDestroyed(Level level, int x, int y, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onDestroyed(level, x, y, client, itemsDropped);
        if (client != null) {
            ObjectEntity objectEntity = this.getCurrentObjectEntity(level, x, y);
            if (objectEntity instanceof AoASpiderAntEggObjectEntity) {
                AoASpiderAntEggObjectEntity spiderEggObjectEntity = (AoASpiderAntEggObjectEntity)objectEntity;
                if (!spiderEggObjectEntity.isBroken) {
                    spiderEggObjectEntity.breakEgg();
                }
            }
        }

        if (level.isClient()) {
            level.entityManager.addParticle(new SpiderEggBrokenParticle(level, (float)(x * 32 + 16), (float)(y * 32 + 16), 5000L, this.brokenTexture), GType.CRITICAL);

            for(int i = 0; i < 40; ++i) {
                level.entityManager.addParticle((float)(x * 32 + 16), (float)(y * 32 + 16), GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1000).sizeFades(30, 50).movesFriction((float)(GameRandom.globalRandom.getIntBetween(-10, 10) * (GameRandom.globalRandom.nextBoolean() ? -3 : 3)), (float)(GameRandom.globalRandom.getIntBetween(5, 15) * (GameRandom.globalRandom.nextBoolean() ? -1 : -3)), 0.5F).color(new Color(181, 187, 159)).height(10.0F);
            }
        }

    }
}
