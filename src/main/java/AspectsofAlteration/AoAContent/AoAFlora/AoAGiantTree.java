package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AoAGiantTree extends GameObject implements ForestryJobObject {
    public int weaveTime = 250;
    public float weaveAmount = 0.02F;
    public float weaveHeight = 1.0F;
    public float waveHeightOffset = 0.1F;
    public int leavesCenterWidth;
    public int leavesMinHeight;
    public int leavesMaxHeight;
    public String leavesTextureName;
    public Supplier<GameTextureSection> leavesTexture;
    public String logStringID;
    public String saplingStringID;
    protected String textureName;
    protected GameTexture[][] textures;
    protected final GameRandom drawRandom;

    public AoAGiantTree(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(new Rectangle(32, 32 ));
        this.textureName = textureName;
        this.logStringID = logStringID;
        this.saplingStringID = saplingStringID;
        this.mapColor = mapColor;
        this.leavesCenterWidth = leavesCenterWidth;
        this.leavesMinHeight = leavesMinHeight;
        this.leavesMaxHeight = leavesMaxHeight;
        this.leavesTextureName = leavesTextureName;
        this.hoverHitbox = new Rectangle(0, 16, 32, 32);
        this.displayMapTooltip = true;
        this.isTree = true;
        this.toolType = ToolType.AXE;
        this.drawRandom = new GameRandom();
        this.replaceCategories.add("tree");
        this.replaceRotations = false;
    }

    public void loadTextures() {
        super.loadTextures();
        GameTexture texture = GameTexture.fromFile("objects/" + this.textureName);
        this.textures = new GameTexture[texture.getWidth() / 128][texture.getHeight() / 256];

        int j;
        for(int i = 0; i < this.textures.length; ++i) {
            for(j = 0; j < this.textures[i].length; ++j) {
                this.textures[i][j] = new GameTexture(texture, 128 * i, 256 * j, 128, 256);
            }
        }

        try {
            GameTexture particleTexture = GameTexture.fromFileRaw("particles/" + this.leavesTextureName);
            j = particleTexture.getHeight();
            int leafSprites = particleTexture.getWidth() / j;
            GameTextureSection particleSection = GameResources.particlesTextureGenerator.addTexture(particleTexture);
            int finalJ = j;
            this.leavesTexture = () -> {
                return particleSection.sprite(GameRandom.globalRandom.nextInt(leafSprites), 0, finalJ);
            };
        } catch (FileNotFoundException var6) {
            this.leavesTexture = null;
        }

    }

    public LootTable getLootTable(Level level, int tileX, int tileY) {
        LootTable lootTable = new LootTable(new LootItemInterface[]{
        });
        if (this.saplingStringID != null) {
            lootTable.items.add(LootItem.between(this.saplingStringID, 1, 2));
        }

        if (this.logStringID != null) {
            lootTable.items.add(LootItem.between(this.logStringID, 20, 80));
        }

        return lootTable;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record(tickManager, "treeSetup", () -> {
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            float alpha = 1.0F;
            if (perspective != null && !Settings.hideUI) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 48, tileY * 32 - 100, 128, 256);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 1.0F;
                } else if (alphaRec.contains(camera.getX() + Screen.mousePos().sceneX, camera.getY() + Screen.mousePos().sceneY)) {
                    alpha = 1.0F;
                }
            }

            int spriteX = 0;
            if (this.textures.length > 1 && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
                spriteX = 1;
            }

            final WaveShader.WaveState waveState = GameResources.waveShader.setupGrassWave(level, tileX, tileY, (long)this.weaveTime, this.weaveAmount, this.weaveHeight, 2, this.waveHeightOffset, this.drawRandom, this.getTileSeed(tileX, tileY, 0));
            int spriteY = 0;
            boolean mirror;
            synchronized(this.drawRandom) {
                this.drawRandom.setSeed(this.getTileSeed(tileX, tileY));
                if (this.textures.length > 1) {
                    spriteY = this.drawRandom.nextInt(this.textures[spriteX].length);
                }

                mirror = this.drawRandom.nextBoolean();
            }

            final TextureDrawOptions options = this.textures[spriteX][spriteY].initDraw().alpha(alpha).light(light).mirror(mirror, false).pos(drawX - 48, drawY - 225);
            list.add(new LevelSortedDrawable(this, tileX, tileY) {
                public int getSortY() {
                    return 16;
                }

                public void draw(TickManager tickManager) {
                    Performance.record(tickManager, "treeDraw", () -> {
                        if (waveState != null) {
                            waveState.start();
                        }

                        options.draw();
                        if (waveState != null) {
                            waveState.end();
                        }

                    });
                }
            });
        });
    }

    public void onDestroyed(Level level, int x, int y, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onDestroyed(level, x, y, client, itemsDropped);
        if (!level.isServerLevel()) {
            int leaves = GameRandom.globalRandom.getIntBetween(15, 20);
            this.spawnLeafParticles(level, x, y, 20, leaves);
        }

    }

    public boolean onDamaged(Level level, int x, int y, int damage, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        boolean out = super.onDamaged(level, x, y, damage, client, showEffect, mouseX, mouseY);
        if (showEffect) {
            level.makeGrassWeave(x, y, this.weaveTime, true);
            if (!level.isServerLevel()) {
                int leaves = GameRandom.globalRandom.getIntBetween(0, 2);
                this.spawnLeafParticles(level, x, y, this.leavesMinHeight, leaves);
            }
        }

        return out;
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount) {
        if (this.leavesTexture != null) {
            spawnLeafParticles(level, x, y, this.leavesCenterWidth, minStartHeight, this.leavesMaxHeight, amount, this.leavesTexture);
        }
    }

    public static void spawnLeafParticles(Level level, int x, int y, int centerWidth, int minStartHeight, int maxStartHeight, int amount, Supplier<GameTextureSection> textureSupplier) {
        boolean alternate = GameRandom.globalRandom.nextBoolean();

        for(int i = 0; i < amount; ++i) {
            float posX = (float)(x * 32 + 16) + (alternate ? GameRandom.globalRandom.getFloatBetween(-1.0F, 0.0F) : GameRandom.globalRandom.getFloatBetween(0.0F, 1.0F)) * (float)centerWidth;
            alternate = !alternate;
            float posY = (float)(y * 32 - 128);
            float startHeight = GameRandom.globalRandom.getFloatBetween((float)minStartHeight, (float)maxStartHeight);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0F, 60.0F);
            float endHeight = GameRandom.globalRandom.getFloatBetween(-10.0F, -5.0F);
            float gravity = GameRandom.globalRandom.getFloatBetween(8.0F, 20.0F);
            boolean mirror = GameRandom.globalRandom.nextBoolean();
            float rotation = GameRandom.globalRandom.getFloatBetween(-100.0F, 100.0F);
            float moveX = GameRandom.globalRandom.floatGaussian() * 5.0F;
            float moveY = GameRandom.globalRandom.floatGaussian() * 2.0F;
            int timeToLive = GameRandom.globalRandom.getIntBetween(3000, 8000);
            int timeToFadeOut = GameRandom.globalRandom.getIntBetween(1000, 2000);
            int totalTime = timeToLive + timeToFadeOut;
            ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0F, endHeight, 0.0F);
            AtomicReference<Float> floatingTime = new AtomicReference(0.0F);
            level.entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite((GameTextureSection)textureSupplier.get()).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(15, 20, 100, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    floatingTime.set((Float)floatingTime.get() + delta);
                }

            }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                float angle = GameMath.sin((Float)floatingTime.get() / 5.0F) * rotation;
                options.rotate(angle, 10, -4);
            }).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    float deltaSpeed = delta / 250.0F;
                    pos.x += moveX * deltaSpeed;
                    pos.y += moveY * deltaSpeed;
                }

            }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                options.mirror(mirror, false);
            }).lifeTime(totalTime);
        }

    }

    public String canPlace(Level level, int x, int y, int rotation) {
        String error = super.canPlace(level, x, y, rotation);
        if (error != null) {
            return error;
        } else {
            GameObject[] adj = level.getAdjacentObjects(x, y);
            GameObject[] var7 = adj;
            int var8 = adj.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                GameObject obj = var7[var9];
                if (obj.isTree) {
                    return "treenear";
                }
            }

            return null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        if (super.isValid(level, x, y)) {
            GameObject[] adj = level.getAdjacentObjects(x, y);
            GameObject[] var5 = adj;
            int var6 = adj.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                GameObject obj = var5[var7];
                if (obj.isTree) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public String getSaplingStringID() {
        return this.saplingStringID;
    }
}
