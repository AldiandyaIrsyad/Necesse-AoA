package AspectsofAlteration.AoAContent.AoAFlora;

import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.SaplingObject;

public class AoATreeSaplingObject extends AoASaplingObject implements ForestrySaplingObject {
    public AoATreeSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, String... validTiles) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, validTiles);
    }

    public AoATreeSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient);
    }

    public String getForestryResultObjectStringID() {
        return this.resultObjectStringID;
    }
}
