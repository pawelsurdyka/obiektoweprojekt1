package agh.ics.oop;
import java.lang.Math;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GrassField extends AbstractWorldMap{
    private int grassNo;
    protected Map<Vector2d, Animal> animals = new HashMap<>();
    protected List<Grass> G = new CopyOnWriteArrayList<>();
    protected List<Animal> A = new CopyOnWriteArrayList<>();
    public boolean update = false;

    MapBoundary bond = new MapBoundary();

    public List<Animal> getA(){
        return A;
    }
    public List<Grass> getG(){
        return G;
    }
    public Map<Vector2d, Animal> getAnimals(){
        return animals;
    }

    public GrassField(int n){
        boolean er;
        grassNo = n;
        int mx = 0; //max x
        int my = 0; //max y
        for(int i=0; i<grassNo; i++){
            er = false;
            Random rand = new Random();
            int x = 1;
            int y = 1;
            while(!er) {
                er = true;
                x = rand.nextInt((int) Math.ceil(Math.sqrt(grassNo * 10))+1);
                y = rand.nextInt((int) Math.ceil(Math.sqrt(grassNo * 10))+1);
                for (Grass vec : G) {
                    if (vec.getPosition().equals(new Vector2d(x, y))) {
                        er = false;
                    }
                }
            }
            if(er) {
                G.add(new Grass(new Vector2d(x, y)));
                bond.putObject(new Grass(new Vector2d(x, y)));
            }
        }
    }

    public String toString(){
        return super.toString();
    }

    @Override
    public Vector2d[] Size() {
//        int minx=Integer.MAX_VALUE;
//        int miny=Integer.MAX_VALUE;
//        int maxx=Integer.MIN_VALUE;
//        int maxy=Integer.MIN_VALUE;
//        for(Animal a: animals.values()){
//            minx = Math.min(minx,a.getPosition().x);
//            miny = Math.min(miny,a.getPosition().y);
//            maxx = Math.max(maxx,a.getPosition().x);
//            maxy = Math.max(maxy,a.getPosition().y);
//        }
//        for(Grass g:G){
//            minx = Math.min(minx,g.getPosition().x);
//            miny = Math.min(miny,g.getPosition().y);
//            maxx = Math.max(maxx,g.getPosition().x);
//            maxy = Math.max(maxy,g.getPosition().y);
//        }
//        Vector2d[] Border = {new Vector2d(minx,miny),new Vector2d(maxx,maxy)};
//        return Border;
        Vector2d[] Border = {bond.getLL(),bond.getUR()};
        System.out.println(Border[0]+"  "+Border[1]);
        return Border;
    }

    void makeNewGrass(Vector2d position){
        for(int i=0;i<G.size();i++){
            if(G.get(i).getPosition().equals(position)){
                bond.deleteG(G.get(i));
                G.remove(i);
                boolean er;
                er = false;
                Random rand = new Random();
                int x = 0;
                int y = 0;
                while(!er) {
                    er = true;
                    x = rand.nextInt((int) Math.ceil(Math.sqrt(grassNo * 10)));
                    y = rand.nextInt((int) Math.ceil(Math.sqrt(grassNo * 10)));
                    for (Grass vec : G) {
                        if (vec.getPosition().equals(new Vector2d(x, y))) {
                            er = false;
                        }
                    }
                }
                if(er) {
                    G.add(new Grass(new Vector2d(x, y)));
                    bond.putObject(new Grass(new Vector2d(x, y)));
                }
            }
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        if(objectAt(position) instanceof Animal)
            return false;
        if(objectAt(position) instanceof Grass){
            makeNewGrass(position);
        }
        return !isOccupied(position) || (isOccupied(position) && !(objectAt(position) instanceof Animal));
    }


    @Override
    public boolean positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        if (canMoveTo(newPosition)) {
            animals.put(newPosition, animals.get(oldPosition));
            animals.remove(oldPosition);
            update = true;
            return true;
        }
        update = true;
        return false;
    }


    @Override
    public boolean place(Animal animal) {
        Vector2d pos = animal.getPosition();
        if (canMoveTo(pos)){
            animals.put(pos, animal);
            bond.putObject(animal);
            A.add(animal);
            animal.addObserver(this);
            return true;
        }
        throw new IllegalArgumentException(pos + " is invalid position");
//        return false;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        if(animals.get(position) != null)
            return true;
        for(Grass obj:G){
            if(obj.getPosition().equals(position))   return true;
        }
        return false;
    }

    @Override
    public Object objectAt(Vector2d position) {
        if(animals.get(position) != null)
            return animals.get(position);
        for(Grass obj:G){
            if(obj.getPosition().equals(position))   return obj;
        }
        return null;
    }
}