package agh.ics.oop.elements;


import agh.ics.oop.*;
import java.util.ArrayList;
import java.util.Collections;
import agh.ics.oop.gui.*;
import agh.ics.oop.interfaces.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

public class Animal implements IMapElement {
    private final AbstractWorldMap map;
    private MapDirection direction;
    private Vector2d position;
    private final ArrayList<Integer> gens;
    private int age = 0;
    private int currGen = 0;
    private int energy;
    private int children;
    private int noOfEatenGrass;
    private IPositionChangeObserver observer;
    private boolean tracked;

    public Animal(AbstractWorldMap map, Vector2d initialPosition, ArrayList<Integer> gens, int energy, boolean tracked){
        this.map = map;
        this.position = initialPosition;
        this.gens = gens;
        this.energy = energy;
        this.children = 0;
        this.noOfEatenGrass = 0;
        this.tracked = tracked;
        Random rand1 = new Random();
        int startgenome = rand1.nextInt(map.getLengthOfGenome());
        this.currGen = startgenome;
        Random rand = new Random();
        int chance = rand.nextInt(8);
        direction = MapDirection.NORTH;
        for(int i=0;i<chance;i++){
            direction = direction.next();
        }
    }
    @Override
    public Vector2d getPosition(){return position;}
    public void setPosition(Vector2d pos){this.position = pos;}
    public MapDirection getDirection(){return this.direction;}
    public ArrayList<Integer> getGens(){return gens;}
    public String getStringGens(){
        StringBuilder S = new StringBuilder();
        for(int g:gens){
            S.append(g);
        }
        return S.toString();
    }
    public String getStringCurrGen(){
        StringBuilder S = new StringBuilder();
        S.append(currGen);
        return S.toString();
    }
    public String getStringEnergy(){
        StringBuilder S = new StringBuilder();
        S.append(energy);
        return S.toString();
    }
    public int getAge(){return age;}
    public int getcurrGen(){return currGen;}
    public int getEnergy(){return energy;}
    public void setEnergy(int energy){this.energy = energy;}
    public boolean getTracked(){return this.tracked;}
    public void setTracked(boolean bool){this.tracked = bool;}
    public void feed(int food){
        this.energy += food;
    }
    public int getChildren(){return children;}
    public void makeChildren(){this.children+=1;}       //zwiekszenie ilosci dzieci
    public void eatGrass(){this.noOfEatenGrass+=1;}
    public int getEatenGrass(){return this.noOfEatenGrass;}

    public void oldering(){
        this.age+=1;
        this.energy = Math.max(this.energy-map.getEnergyToMove(), 0);
    }

    public void nextGen(){
        this.currGen = (this.currGen + 1)%(gens.size());
    }

    public void move(){ //wybieranie kolejnego genu i poruszanie zgodnie z nim
        Vector2d oldPosition = position;
        Random rand = new Random();
        int crazyPossibilities = rand.nextInt(5);
        int newDirection = gens.get(currGen);
        if(map.getCrazy()){
            if(crazyPossibilities == 0){
                this.currGen = (this.currGen + 1)%gens.size();
                newDirection = gens.get(currGen);
            }
        }

        if(newDirection!=0) {
            for (int i = 0; i < newDirection; i++) {
                this.direction = this.direction.next();
            }
        }
        if(map.canMoveTo(position.add(direction.toUnitVector()))){
            if((position.add(direction.toUnitVector())).precedes(new Vector2d(map.getWidth() - 1, map.getHeight() - 1))
                    && (position.add(direction.toUnitVector())).follows(new Vector2d(0, 0))){
                // sprawdzamy czy po ruchu jest dalej na mapie
                position = position.add(direction.toUnitVector());
                this.setPosition(new Vector2d((position.x) , (position.y)));
            }
            else{ // jesteśmy na kuli ziemskiej
                position = position.add(direction.toUnitVector());
                if(position.y == map.getHeight()){
                    position = position.subtract(direction.toUnitVector());
                    this.direction = (((this.direction.next()).next().next()).next());
                }
                if(position.y < 0){
                    position = position.subtract(direction.toUnitVector());
                    this.direction = (((this.direction.next()).next().next()).next());
                }
                if(position.y >= 0 && position.y < map.getHeight()){
                    if(position.x >= map.getWidth()){
                        position = new Vector2d(0,position.y);
                    }
                    if(position.x < 0){
                        position = new Vector2d(map.getWidth()-1,position.y);
                    }
                }
            }

        }
        else{ // czyli jesteśmy na mapie z piekielnymi portalami
            int randomNum1 = ThreadLocalRandom.current().nextInt(0, map.getWidth());
            int randomNum2 = ThreadLocalRandom.current().nextInt(0, map.getHeight());
            position = new Vector2d(randomNum1,randomNum2);
            this.energy = (this.energy*(100-map.getEnergyToChild()))/100;
        }
        observer.positionChanged(oldPosition, this.position, this.map);
    }

    public Animal copulate(Animal ani2, boolean tracked){ //obecne zwierze + argument = nowe zwracane zwierze
        int energy = ani2.getEnergy();
        ArrayList<Integer> gens = ani2.getGens();
        Random rand = new Random();
        int side = rand.nextInt(2);
        ArrayList<Integer> newGens = new ArrayList<>();
        int div = energy/(energy+this.energy);
        for(int i=0;i<map.getLengthOfGenome();i++){
            if((energy>this.energy && side==0)||(energy<=this.energy && side==1)){
                if(i<div)
                    newGens.add(gens.get(i));
                else
                    newGens.add(this.gens.get(i));
            }
            else{
                if(i>=div)
                    newGens.add(gens.get(i));
                else
                    newGens.add(this.gens.get(i));
            }
        }
        Random randg = new Random();
        int noMut = randg.nextInt(map.getMaxMutation()-map.getMinMutation()+1);
        noMut = noMut + map.getMinMutation();
        if(map.getRandomMutation()){
            for(int i = 0;i < noMut;i++){
                Random randmut = new Random();
                int whereMut = randmut.nextInt(gens.size());
                Random mut = new Random();
                int Mut = mut.nextInt(8);
                while(Mut == newGens.get(whereMut)) {
                    Mut = mut.nextInt(8);
                }
                newGens.set(whereMut,Mut);
            }
        }
        if(map.getSlightMutauion()){
            for(int i = 0;i < noMut;i++){
                Random randmut = new Random();
                int whereMut = randmut.nextInt(gens.size());
                Random mut = new Random();
                int Mut = mut.nextInt(2);
                if(Mut == 0){
                    newGens.set(whereMut,newGens.get(whereMut)-1);
                    if(newGens.get(whereMut)==-1){
                        newGens.set(whereMut,7);
                    }
                }
                if(Mut == 1){
                    newGens.set(whereMut,(newGens.get(whereMut)+1)%8);
                }
            }
        }

        int energyTo = map.getEnergyToChild();
        int newEnergy = (this.energy*energyTo)/100 + (energy*energyTo)/100;
        this.energy = (this.energy*(100-energyTo))/100;              //zmiejszenie energii
        ani2.setEnergy((energy*(100-energyTo))/100);
        this.makeChildren();                           //dodanie dzieci
        ani2.makeChildren();
        return new Animal(this.map, this.position, newGens, newEnergy, tracked);
    }
    public void addObserver(IPositionChangeObserver observer){this.observer=observer;}
}
