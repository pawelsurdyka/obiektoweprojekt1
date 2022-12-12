package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimulationEngine implements IEngine, Runnable{
    ArrayList<MoveDirection> moves;
    IWorldMap map;
    Vector2d[] startpos;
    List<Animal> A = new CopyOnWriteArrayList<>();
    List<Grass> G = new CopyOnWriteArrayList<>();
    GrassField gmap;
    int moveDelay=300;
    public List<Animal> getA(){
        return A;
    }
    public List<Grass> getG(){
        return G;
    }
    public Vector2d[] getBorder(){
        Vector2d[] Border = {gmap.bond.getLL(),gmap.bond.getUR()};
        return Border;
    }
    public void setDirections(ArrayList<MoveDirection> directions){this.moves = directions;}
    public SimulationEngine(IWorldMap map, Vector2d[] startpos){
//    this.moves = moves;
        this.map = map;
        gmap = (GrassField) map;
        Map<Vector2d, Animal> animals = gmap.getAnimals();
        A = gmap.getA();
        G = gmap.getG();
        this.startpos = startpos;
        addAnimals();
    }
    public void addAnimals() {
        for (Vector2d ps : startpos) {
            map.place(new Animal(map, ps));
        }
    }
    @Override
    public void run()  {
        System.out.println("Thread started.");
        int id=0;

//        System.out.println(gmap);
        while(id<moves.size()){
//            for(Animal i:animals.values()){
            for(Animal i:A){
                if(id>=moves.size())
                    break;
                i.move(moves.get(id));
//                if(gmap.update){
//
//                }
//                System.out.println(moves.get(id));
//                System.out.println(i);
//                System.out.println(id);
                System.out.println(gmap);
                id+=1;
            }
            try {
                Thread.sleep(moveDelay);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
        map = gmap;
    }
}