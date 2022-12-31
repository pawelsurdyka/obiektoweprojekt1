package agh.ics.oop.elements;

import agh.ics.oop.*;
import agh.ics.oop.interfaces.*;

public class Grass implements IMapElement {
    private final Vector2d position;
    public Grass(Vector2d position){
        this.position = position;
    }
    @Override
    public Vector2d getPosition(){
        return position;
    }
}
