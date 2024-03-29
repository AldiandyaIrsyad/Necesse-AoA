package AspectsofAlteration.AoAContent.AoAMobs.AoABosses;

import AspectsofAlteration.AoA;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.geom.Point2D;
import java.util.List;

public class AoAGiantSerpentTail extends AoAGiantSerpentBody {
    public AoAGiantSerpentTail() {
    }

    private AoAGiantSerpentBody getNextBodyPart(int count) {
        AoAGiantSerpentBody next = (AoAGiantSerpentBody)this.next;

        for(int i = 0; i < count && next.next != null; ++i) {
            next = (AoAGiantSerpentBody)next.next;
        }

        return next;
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.isVisible()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(x) - 48;
            int drawY = camera.getDrawY(y);
            float tailAngle = 0.0F;
            AoAGiantSerpentBody next = this.getNextBodyPart(2);
            if (next != null) {
                tailAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(next.x - (float)x, next.y - (float)y)) + 180.0F);
            }

            WormMobHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.fromFile("aoagiantserpentmob"), 1, 1, 96), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, tailAngle, drawX, drawY, 64);
            this.addShadowDrawables(tileList, x, y, light, camera);
        }
    }
}
