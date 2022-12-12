package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


abstract public class AbstractWorldMap implements IWorldMap, IPositionChangeObserver{

    abstract public Vector2d[] Size();

    public String toString(){
        MapVisualizer S = new MapVisualizer(this);
        Vector2d[] Border = Size();
        return S.draw(Border[0],Border[1]);
    }



}
