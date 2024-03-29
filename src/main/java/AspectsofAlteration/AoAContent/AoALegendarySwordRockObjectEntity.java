package AspectsofAlteration.AoAContent;

import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class AoALegendarySwordRockObjectEntity extends ObjectEntity {
    private final String[] mobList = new String[]{"deepcavespirit"};
    public boolean isBroken = false;

    public AoALegendarySwordRockObjectEntity(Level level, int x, int y) {
        super(level, "aoalegswordrock", x, y);
    }

    public void breakEgg() {
        if (!this.isBroken) {
            this.spawnContainedMob(this.getLevel());
            this.isBroken = true;
        }

    }

    private void spawnContainedMob(Level level) {
        for(int i = 0; i < GameRandom.globalRandom.getIntBetween(2, 10); ++i) {
            String containedMob = (String)GameRandom.globalRandom.getOneOf(this.mobList);
            level.entityManager.addMob(MobRegistry.getMob(containedMob, level), (float)(this.getX() * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(this.getY() * 32 + GameRandom.globalRandom.getIntBetween(8, 24)));
        }

    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addBoolean("isBroken", this.isBroken);
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.isBroken = save.getBoolean("isBroken", false);
    }
}
