package AspectsofAlteration.AoAContent;

import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AoAWallLightObject extends GameObject {
    public GameTexture texture;

    public AoAWallLightObject() {
        this.mapColor = new Color(255, 255, 255);
        this.displayMapTooltip = true;
        this.lightLevel = 150;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.roomProperties.add("lights");
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/aoawalllight");
    }


    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record(tickManager, "torchSetup", () -> {
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            byte rotation = level.getObjectRotation(tileX, tileY);
            int sprite = this.getSprite(level, tileX, tileY, rotation);
            boolean active = this.isActive(level, tileX, tileY);
            if (sprite == 0) {
                drawY -= 16;
            }

            final TextureDrawOptions options = this.texture.initDraw().sprite(active ? 0 : 1, sprite, 32).light(light).pos(drawX, drawY - 16);
            final byte sortY;
            if (sprite == 0) {
                sortY = 0;
            } else if (sprite == 2) {
                sortY = 32;
            } else {
                sortY = 16;
            }

            list.add(new LevelSortedDrawable(this, tileX, tileY) {
                public int getSortY() {
                    return sortY;
                }

                public void draw(TickManager tickManager) {
                    TextureDrawOptions var10002 = options;
                    Objects.requireNonNull(var10002);
                    Performance.record(tickManager, "torchDraw", var10002::draw);
                }
            });
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int sprite = this.getSprite(level, tileX, tileY, rotation);
        if (sprite == 0) {
            drawY -= 16;
        }


        this.texture.initDraw().sprite(0, sprite, 32).alpha(alpha).draw(drawX, drawY - 16);
    }

    private int getSprite(Level level, int tileX, int tileY, int rotation) {
        GameObject topObject = level.getObject(tileX, tileY - 1);
        GameObject rightObject = level.getObject(tileX + 1, tileY);
        GameObject botObject = level.getObject(tileX, tileY + 1);
        GameObject leftObject = level.getObject(tileX - 1, tileY);
        boolean attachTop = topObject.isWall && !topObject.isDoor;
        boolean attachRight = rightObject.isWall && !rightObject.isDoor;
        boolean attachBot = botObject.isWall && !botObject.isDoor;
        boolean attachLeft = leftObject.isWall && !leftObject.isDoor;
        if (attachTop) {
            if (rotation == 0 && attachBot) {
                return 2;
            } else if (rotation == 1 && attachRight) {
                return 1;
            } else {
                return rotation == 3 && attachLeft ? 3 : 0;
            }
        } else if (attachBot) {
            if (rotation == 1 && attachRight) {
                return 1;
            } else {
                return rotation == 3 && attachLeft ? 3 : 2;
            }
        } else if (attachRight) {
            return rotation == 3 && attachLeft ? 3 : 1;
        } else {
            return attachLeft ? 3 : 0;
        }
    }

    public int getPlaceRotation(Level level, int levelX, int levelY, PlayerMob player, int playerDir) {
        if (playerDir == 1) {
            return 3;
        } else {
            return playerDir == 3 ? 1 : super.getPlaceRotation(level, levelX, levelY, player, playerDir);
        }
    }

    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        int sprite = this.getSprite(level, tileX, tileY, rotation);
        if (sprite == 0) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -32, 32, 32, 0));
        } else if (sprite == 1 || sprite == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16, 0));
        }

        return list;
    }

    public String canPlace(Level level, int x, int y, int rotation) {
        if (level.getObjectID(x, y) != 0 && !level.getObject(x, y).isGrass) {
            return "occupied";
        } else {
            boolean hasWall = false;
            if (level.getObject(x - 1, y).isWall && !level.getObject(x - 1, y).isDoor) {
                hasWall = true;
            } else if (level.getObject(x + 1, y).isWall && !level.getObject(x + 1, y).isDoor) {
                hasWall = true;
            } else if (level.getObject(x, y - 1).isWall && !level.getObject(x, y - 1).isDoor) {
                hasWall = true;
            } else if (level.getObject(x, y + 1).isWall && !level.getObject(x, y + 1).isDoor) {
                hasWall = true;
            }

            return !hasWall ? "nowall" : null;
        }
    }

    public boolean isValid(Level level, int x, int y) {
        boolean hasWall = false;
        if (level.getObject(x - 1, y).isWall && !level.getObject(x - 1, y).isDoor) {
            hasWall = true;
        } else if (level.getObject(x + 1, y).isWall && !level.getObject(x + 1, y).isDoor) {
            hasWall = true;
        } else if (level.getObject(x, y - 1).isWall && !level.getObject(x, y - 1).isDoor) {
            hasWall = true;
        } else if (level.getObject(x, y + 1).isWall && !level.getObject(x, y + 1).isDoor) {
            hasWall = true;
        }

        return hasWall;
    }

    public int getLightLevel(Level level, int x, int y) {
        return this.isActive(level, x, y) ? 150 : 0;
    }

    public boolean isActive(Level level, int x, int y) {
        byte rotation = level.getObjectRotation(x, y);
        return this.getMultiTile(rotation).streamIDs(x, y).noneMatch((c) -> {
            return level.wireManager.isWireActiveAny(c.tileX, c.tileY);
        });
    }

    public void onWireUpdate(Level level, int x, int y, int wireID, boolean active) {
        byte rotation = level.getObjectRotation(x, y);
        Rectangle rect = this.getMultiTile(rotation).getTileRectangle(x, y);
        level.lightManager.updateStaticLight(rect.x, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1, true);
    }
}
