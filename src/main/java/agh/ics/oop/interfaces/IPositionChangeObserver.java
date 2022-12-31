package agh.ics.oop.interfaces;

import agh.ics.oop.*;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Vector2d newPosition, AbstractWorldMap map);

    default void dayEnded(SimulationEngine engine, AbstractWorldMap map){
    }
}