package agh.ics.oop;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapBoundary implements IPositionChangeObserver{
    List<IMapElement> objectsX = new CopyOnWriteArrayList<>();
    List<IMapElement> objectsY = new CopyOnWriteArrayList<>();
    void putObject(IMapElement object){
        objectsX.add(object);
        objectsY.add(object);
    }

    void deleteG(Grass g){
        Vector2d pos = g.getPosition();
        for(int i=0;i<objectsX.size();i++){
            if(objectsX.get(i) instanceof Grass && objectsX.get(i).getPosition().x == pos.x){
                objectsX.remove(i);
                break;
            }
        }
        for(int i=0;i<objectsY.size();i++){
            if(objectsY.get(i) instanceof Grass && objectsY.get(i).getPosition().y == pos.y){
                objectsY.remove(i);
                break;
            }
        }
    }

    public Vector2d getLL(){
        objectsX.sort(new cartOrdX());
        objectsY.sort(new cartOrdY());
//        for (IMapElement x : objectsX) {
//            System.out.println(x.getPosition());
//        }
//        System.out.println();
//        for (IMapElement iMapElement : objectsY) {
//            System.out.println(iMapElement.getPosition());
//        }
        return new Vector2d(objectsX.get(0).getPosition().x,objectsY.get(0).getPosition().y);
    }
    public Vector2d getUR(){
        int end = objectsX.size()-1;
        return new Vector2d(objectsX.get(end).getPosition().x,objectsY.get(end).getPosition().y);
    }
    @Override
    public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        return false;
    }
}

class cartOrdX implements Comparator<IMapElement> {
    public int compare(IMapElement a, IMapElement b){
        if(a.getPosition().x== b.getPosition().x){
            if(a.getPosition().y== b.getPosition().y){
                if(a instanceof Animal)
                    return -1;
                else
                    return 1;
            }
            else
                return a.getPosition().y- b.getPosition().y;
        }
        else
            return a.getPosition().x- b.getPosition().x;
    }
}

class cartOrdY implements Comparator<IMapElement> {
    public int compare(IMapElement a, IMapElement b){
        if(a.getPosition().y== b.getPosition().y){
            if(a.getPosition().x== b.getPosition().x){
                if(a instanceof Animal)
                    return -1;
                else
                    return 1;
            }
            else
                return a.getPosition().x- b.getPosition().x;
        }
        else
            return a.getPosition().y- b.getPosition().y;
    }
}








