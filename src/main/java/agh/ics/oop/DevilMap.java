package agh.ics.oop;
import agh.ics.oop.interfaces.*;
import agh.ics.oop.elements.*;

public class DevilMap extends AbstractWorldMap implements IPositionChangeObserver{
    public DevilMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy,int startingGrassNo,
                    int grassPerDay,int whenIsFull,int energyToChild,int minMutation,int maxMutation,
                    int lengthOfGenome,boolean randomMutation,boolean slightMutauion,boolean crazy,
                    boolean toxicCorpse,boolean isThisRoundedMap) {
        super(width, height, startEnergy, moveEnergy, plantEnergy,startingGrassNo,grassPerDay,whenIsFull,
                energyToChild,minMutation,maxMutation,lengthOfGenome,randomMutation,slightMutauion,crazy,toxicCorpse,
                isThisRoundedMap);
    }

    @Override
    public boolean canMoveTo(Vector2d position)  {
        return position.precedes(new Vector2d(this.getWidth() - 1, this.getHeight() - 1)) && position.follows(new Vector2d(0, 0));
    }
}
