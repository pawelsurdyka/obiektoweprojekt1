package agh.ics.oop;


import agh.ics.oop.interfaces.*;
import agh.ics.oop.elements.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

abstract public class AbstractWorldMap implements IPositionChangeObserver {
    protected Map<Vector2d, Grass> grasses = new HashMap<>();
    protected CopyOnWriteArrayList<Animal> A = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<Grass> G = new CopyOnWriteArrayList<>();
    protected ArrayList<Vector2d> noGrassJungle = new ArrayList<>();
    protected ArrayList<Vector2d> noGrassStep = new ArrayList<>();
    private final CopyOnWriteArrayList<String> genotypes = new CopyOnWriteArrayList<>();
    private final int width;
    private final int height;
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    protected int startingGrassNo;
    protected int grassPerDay;
    protected int whenIsFull;
    private final int energyToChild;
    protected int minMutation;
    protected int maxMutation;
    private final int lengthOfGenome;
    private float deadA;
    private float sumAge;
    private int days;
    public Vector2d jungleLL;
    public Vector2d jungleUR;
    private IPositionChangeObserver observer;
    private Animal trackedAnimal;
    private boolean crazy;
    private boolean randomMutation;
    private boolean slightMutation;
    private boolean toxicCorpse;
    private int numOfFreeLand;
    int[] toxicMap = {};
    int[] occupiedMap = {};

    int[] trackedInfo = new int[4];//children, eaten grass, when died, age
    boolean isThisRoundedMap;

    public AbstractWorldMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy,int startingGrassNo,
                            int grassPerDay,int whenIsFull,int energyToChild,int minMutation,int maxMutation,
                            int lengthOfGenome,boolean randomMutation,boolean slightMutauion,boolean crazy,
                            boolean toxicCorpse,boolean isThisRoundedMap){
        this.deadA = 0;
        this.sumAge = 0;
        this.width = width;
        this.height = height;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.startingGrassNo = startingGrassNo;
        this.grassPerDay = grassPerDay;
        this.whenIsFull = whenIsFull;
        this.energyToChild = energyToChild;
        this.minMutation = minMutation;
        this.maxMutation = maxMutation;
        this.lengthOfGenome = lengthOfGenome;
        this.randomMutation = false;
        this.slightMutation = false;
        this.crazy = false;
        this.toxicCorpse = false;
        this.isThisRoundedMap = false;
        this.days = 0;
        this.numOfFreeLand = 0;
        this.toxicMap = new int[this.height*this.width];
        this.occupiedMap = new int[this.height*this.width];
        jungleLL = new Vector2d(0,((4*height)/10));
        jungleUR = new Vector2d(width-1,((6*height)/10)-1);
        for(int j=0;j<height;j++)
            for(int i=0;i<width;i++) {
                Vector2d pos = new Vector2d(i, j);
                if (pos.precedes(jungleUR) && pos.follows(jungleLL))
                    this.noGrassJungle.add(pos);
                else
                    this.noGrassStep.add(pos);
            }
    }

    public CopyOnWriteArrayList<Animal> getA(){
        return A;
    }
    public CopyOnWriteArrayList<Grass> getG(){
        return G;
    }
    public boolean getIsRounded(){return isThisRoundedMap;}
    public int getWidth(){return  width;}
    public int getHeight(){return height;}
    public int getDays(){return days;}
    public int getMinMutation(){return minMutation;}
    public int getMaxMutation(){return maxMutation;}
    public int getEnergyToMove(){return moveEnergy;}
    public int getStartingGrassNo(){return startingGrassNo;}
    public int getEnergyToChild(){return energyToChild;}
    public int getLengthOfGenome(){return lengthOfGenome;}
    public void setRoundedToTrue(){this.isThisRoundedMap = true;}

    public void setCrazyToTrue(){
        this.crazy = true;
    }
    public void setRandomMutationToTrue(){
        this.randomMutation = true;
    }
    public void setSlightMutauionToTrue(){
        this.slightMutation = true;
    }
    public void setToxicCorpsetoTreu(){this.toxicCorpse = true;}

    public boolean getCrazy(){return crazy;}
    public boolean getRandomMutation(){return this.randomMutation;}
    public boolean getSlightMutauion(){return this.slightMutation;}
    public boolean getToxicCorpse(){return this.toxicCorpse;}

    public CopyOnWriteArrayList<String> getGenotypes(){
        return genotypes;
    }
    public int[] getTrackedInfo(){ //children, offsprings, when died, how old
        return trackedInfo;
    }
    public Animal getTrackedAnimal(){
        return this.trackedAnimal;
    }
    public int getNumOfFreeLand(){return this.numOfFreeLand;}
    public void calcFree(){
        numOfFreeLand = 0;
        for(Animal a: A){
            int x = a.getPosition().getX();
            int y = a.getPosition().getY();
            occupiedMap[x+y*width] += 1;
        }
        for(Grass g: G){
            int x = g.getPosition().getX();
            int y = g.getPosition().getY();
            occupiedMap[x+y*width] += 1;
        }
        for(int i = 0;i<occupiedMap.length;i++){
            if(occupiedMap[i]==0){
                numOfFreeLand += 1;
            }
        }
        for(int i = 0;i<occupiedMap.length;i++){
            occupiedMap[i] = 0;
        }
    }
    public void setTrackedAnimal(Animal a){
        for(Animal tmp:A){
            tmp.setTracked(false);
        }
        a.setTracked(true);
        this.trackedAnimal = a;
        trackedInfo[0] = 0;
        trackedInfo[1] = a.getEatenGrass();
        trackedInfo[2] = -1;
        trackedInfo[3] = a.getAge();
    }

    public double avgAge(){
        if(deadA == 0)
            return 0;
        return sumAge/deadA;
    }

    public void place(Animal animal, boolean beginning) {
        ArrayList<Integer> newGens = animal.getGens();
        StringBuilder S = new StringBuilder();
        for(int i=0;i<lengthOfGenome;i++){
            S.append(newGens.get(i));
        }
        this.genotypes.add(S.toString());
        Vector2d pos = animal.getPosition();
        for(int i=0;i<G.size();i++){
            if(G.get(i).getPosition().equals(pos)){
                if (G.get(i).getPosition().precedes(jungleUR) && G.get(i).getPosition().follows(jungleLL))
                    noGrassJungle.add(G.get(i).getPosition());
                else
                    noGrassStep.add(G.get(i).getPosition());
                G.remove(i);
                break;
            }
        }
        A.add(animal);
        animal.addObserver(this);
        if(!beginning)
            observer.positionChanged(animal.getPosition(), animal.getPosition(), this);
    }



    public void remove(){
        boolean flag = true;
        while(flag) {
            flag = false;
            for (int i = 0; i < A.size(); i++) {
                if (A.get(i).getEnergy() <= 0) {
                    ArrayList<Integer> deadGens;
                    deadGens = A.get(i).getGens();
                    StringBuilder S = new StringBuilder();
                    for(int k=0;k<lengthOfGenome;k++){
                        S.append(deadGens.get(k));
                    }
                    for(int k=0;k<genotypes.size();k++){
                        if(genotypes.get(k).equals(S.toString())){
                            genotypes.remove(k);
                            break;
                        }
                    }
                    if(A.get(i).equals(trackedAnimal)){
                        trackedInfo[2] = days;
                    }
                    this.deadA+=1;
                    this.sumAge+=A.get(i).getAge();
                    observer.positionChanged(A.get(i).getPosition(),A.get(i).getPosition(), this);
                    int x = A.get(i).getPosition().getX();
                    int y = A.get(i).getPosition().getY();
                    toxicMap[x+y*width] += 1;
                    A.remove(i);
                    flag = true;
                    break;
                }
            }
        }
    }

    public ArrayList<Animal> getStrongest(Vector2d v, boolean if2nd){
        ArrayList<Animal> animals = new ArrayList<>();
        ArrayList<Animal> animalsFinal = new ArrayList<>();
        for(Animal a:A){
            if(a.getPosition().equals(v))
                animals.add(a);
        }
        if(animals.size()>0) {
            int first = animals.get(0).getEnergy();
            int second = -1;
            if(animals.size()>1)
                second = animals.get(0).getEnergy();
            for (Animal a : animals) {
                if (a.getEnergy() >= first)
                    first = a.getEnergy();
                else if (a.getEnergy() > second)
                    second = a.getEnergy();
            }
            for (Animal a : animals) {
                if (a.getEnergy() == first)
                    animalsFinal.add(a);
            }
            if (if2nd) {
                for (Animal a : animals) {
                    if (a.getEnergy() == second)
                        animalsFinal.add(a);
                }
            }
        }
        return animalsFinal;
    }
    public ArrayList<Animal> getOldest(ArrayList<Animal> animals, boolean if2nd){
        ArrayList<Animal> animalsFinal = new ArrayList<>();
        int oldest = animals.get(0).getAge();
        int second = -1;
        if(animals.size()>1)
            second = animals.get(0).getAge();
        for (Animal a : animals) {
            if (a.getAge() >= oldest)
                oldest = a.getAge();
            else if (a.getAge() > second)
                second = a.getAge();
        }
        for (Animal a : animals) {
            if (a.getAge() == oldest)
                animalsFinal.add(a);
        }
        if (if2nd) {
            for (Animal a : animals) {
                if (a.getAge() == second)
                    animalsFinal.add(a);
            }
        }
        return animalsFinal;
    }
    public ArrayList<Animal> get500plus(ArrayList<Animal> animals, boolean if2nd){
        ArrayList<Animal> animalsFinal = new ArrayList<>();
        int biggestFamily = animals.get(0).getChildren();
        int second = -1;
        if(animals.size()>1)
            second = animals.get(0).getChildren();
        for (Animal a : animals) {
            if (a.getChildren() >= biggestFamily)
                biggestFamily = a.getChildren();
            else if (a.getChildren() > second)
                second = a.getChildren();
        }
        for (Animal a : animals) {
            if (a.getChildren() == biggestFamily)
                animalsFinal.add(a);
        }
        if (if2nd) {
            for (Animal a : animals) {
                if (a.getChildren() == second)
                    animalsFinal.add(a);
            }
        }
        return animalsFinal;
    }
    public void eat(){
        if(A.size()>0) {
            List<Pair<Vector2d, Integer>> places = new ArrayList<>();
            Pair<Vector2d, Integer> P = new Pair<>(A.get(0).getPosition(), 1);
            places.add(P);
            for (int i = 1; i < A.size(); i++) {
                for (int j=0;j<places.size();j++){
                    if(places.get(j).first.equals(A.get(i).getPosition())){
                        break;
                    }
                    if(j == places.size()-1){
                        Pair<Vector2d, Integer> P1 = new Pair<>(A.get(i).getPosition(), 1);
                        places.add((P1));
                    }
                }
            }
            for (Pair<Vector2d, Integer> p : places) {
                if(grasses.containsKey(p.first)){
                    ArrayList<Animal> animals1 = getStrongest(p.first,false);
                    ArrayList<Animal> animals2 = getOldest(animals1,false);
                    ArrayList<Animal> animals3 = get500plus(animals2,false);
                    if(animals1.size()>1){
                        if(animals2.size()>1){
                            if(animals3.size()>1){
                                Random rand = new Random();
                                int lucky = rand.nextInt(animals3.size());
                                animals3.get(lucky).feed(this.plantEnergy);
                                animals3.get(lucky).eatGrass();
                                if(animals3.get(lucky).equals(trackedAnimal)){
                                    trackedInfo[1] = animals3.get(lucky).getEatenGrass();
                                }
                            }
                            else{
                                animals3.get(0).feed(this.plantEnergy);
                                animals3.get(0).eatGrass();
                                if(animals3.get(0).equals(trackedAnimal)){
                                    trackedInfo[1] = animals3.get(0).getEatenGrass();
                                }
                            }
                        }
                        else{
                            animals2.get(0).feed(this.plantEnergy);
                            animals2.get(0).eatGrass();
                            if(animals2.get(0).equals(trackedAnimal)){
                                trackedInfo[1] = animals2.get(0).getEatenGrass();
                            }
                        }
                    }
                    else{
                        animals1.get(0).feed(this.plantEnergy);
                        animals1.get(0).eatGrass();
                        if(animals1.get(0).equals(trackedAnimal)){
                            trackedInfo[1] = animals1.get(0).getEatenGrass();
                        }
                    }
                    if (p.first.precedes(jungleUR) && p.first.follows(jungleLL))
                        noGrassJungle.add(p.first);
                    else
                        noGrassStep.add(p.first);
                    for(int i=0;i<G.size();i++){
                        if(G.get(i).getPosition().equals(p.first)) {
                            G.remove(i);
                            break;
                        }
                    }
                    grasses.remove(p.first);
                }
            }
        }
    }
    public ArrayList<Animal> whoCopulate(Vector2d v){
        ArrayList<Animal> animalsFinal = new ArrayList<>();
        ArrayList<Animal> animalsf = getStrongest(v,false);
        ArrayList<Animal> animalsff = getOldest(animalsf,false);
        ArrayList<Animal> animalsfff = get500plus(animalsff,false);

        ArrayList<Animal> animalst = getStrongest(v,true);
        ArrayList<Animal> animalstt = getOldest(animalst,true);
        ArrayList<Animal> animalsttt = get500plus(animalstt,true);

        if(animalsf.size() == 1){
            animalsFinal = animalst;
        }

        if(animalsf.size()==2){
            animalsFinal = animalsf;
        }
        if(animalsf.size()>2){
            if(animalsff.size() == 1) {
                animalsFinal = getOldest(animalsf, true);
            }
            if(animalsff.size() == 2) {
                animalsFinal = animalsff;
            }
            if(animalsff.size() > 2) {
                if (animalsfff.size() == 1) {
                    animalsFinal = get500plus(animalsff,true);;
                }
                if(animalsfff.size() >= 2){
                    animalsFinal = animalsfff;
                }
            }

        }
        return animalsFinal;
    }
    public void copulate(){
        Set<Vector2d> cords = new LinkedHashSet<>();
        for(Animal a:A){
            cords.add(a.getPosition());
        }
        for(Vector2d v:cords){
            List<Animal> animals1 = whoCopulate(v);
            if(animals1.size()>1) {
                if (animals1.get(0).getEnergy() >= whenIsFull  && animals1.get(1).getEnergy() >= whenIsFull) {
                    if(animals1.get(0).getTracked() || animals1.get(1).getTracked()) {
                        if (animals1.get(0).equals(trackedAnimal) || animals1.get(1).equals(trackedAnimal)) {
                            trackedInfo[0] += 1;
                        }
                        place(animals1.get(0).copulate(animals1.get(1), true), false);
                    }
                    else
                        place(animals1.get(0).copulate(animals1.get(1), false), false);
                }
            }
        }
    }

    void newGrassInArea(ArrayList<Vector2d> noGrass){
        if(noGrass.size()>0) {
            Random rand = new Random();
            int x = rand.nextInt(noGrass.size());
            Grass g1 = new Grass(noGrass.get(x));
            G.add(g1);
            observer.positionChanged(g1.getPosition(), g1.getPosition(), this);
            grasses.put(noGrass.get(x), g1);
            for(int i=0;i<noGrass.size();i++)
                if(noGrass.get(i).equals(noGrass.get(x))) {
                    noGrass.remove(i);
                    break;
                }
        }
    }

    public void newGrass(){
        for(int i = 0;i<grassPerDay;i++) {
            Random rand = new Random();
            int whereGrass = rand.nextInt(5);
            if(whereGrass == 0){
                newGrassInArea(noGrassStep);
            }
            else {
                if(noGrassJungle.size() == 0)
                {
                    newGrassInArea(noGrassStep);
                }
                else
                    newGrassInArea(noGrassJungle);
            }

        }
    }

    public void newToxicGrass(){
        ArrayList<Vector2d> ToxicPositions = new ArrayList<>(width*height);
        for(int i = 0;i<width*height;i++){
            // temp = (x1D,ilosc trupów)
            int x = i % width;
            int y = i / width;
            Vector2d temp = new Vector2d(i,toxicMap[i]);
            //ToxicPositions[i] = (x1D,ilość trupów)
            ToxicPositions.add(temp);
        }
        // sortowanie pól po ilości trupów
        for(int i = 0;i<width*height;i++){
            for(int j = 0;j<width*height-i-1;j++){
                if((ToxicPositions.get(j)).getY()>(ToxicPositions.get(j+1)).getY()){
                    Vector2d temp = ToxicPositions.get(j);
                    ToxicPositions.set(j,ToxicPositions.get(j+1));
                    ToxicPositions.set(j+1,temp);
                }
            }
        }
        ArrayList<Vector2d> lessToxic = new ArrayList<>(width*height/5);
        ArrayList<Vector2d> moreToxic = new ArrayList<>((width*height*4)/5);
        for(int i = 0;i<width*height;i++){
            if(i<width*height/5){
                //ToxicPositions[i] = (x1D,ilość trupów)
                int x = (ToxicPositions.get(i)).getX() % width;
                int y = (ToxicPositions.get(i)).getX() / width;
                Vector2d tempVec = new Vector2d(x,y);
                lessToxic.add(tempVec);
                for (Grass g : G)
                    if(Objects.equals(g.getPosition(), tempVec))
                        lessToxic.remove(tempVec);
            }
            else{
                int x = (ToxicPositions.get(i)).getX() % width;
                int y = (ToxicPositions.get(i)).getX() / width;
                Vector2d tempVec = new Vector2d(x,y);
                moreToxic.add(tempVec);
                for (Grass g : G)
                    if(Objects.equals(g.getPosition(), tempVec))
                        moreToxic.remove(tempVec);
            }
        }
        Random rand = new Random();
        int posibility = rand.nextInt(5);
        if (lessToxic.size() > 0){
            if(posibility == 0){
                newGrassInArea(moreToxic);
            }
            else{
                newGrassInArea(lessToxic);
            }
        }
        else{
            newGrassInArea(moreToxic);
        }

    }

    public void nextDay(){
        this.days+=1;
        if(trackedInfo[2]==-1)
            trackedInfo[3]+=1;
    }

    public abstract boolean canMoveTo(Vector2d position);

    public IMapElement objectAt(Vector2d position){
        ArrayList<Animal> animalsOnPosition = getStrongest(position,false);
        if(animalsOnPosition.size()>0)
            return animalsOnPosition.get(0);
        for(Grass g:G){
            if(g.getPosition().equals(position))
                return g;
        }
        return new Nothing();
    }

    public void setObserver(IPositionChangeObserver observer){this.observer=observer;}
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, AbstractWorldMap map) {
        observer.positionChanged(oldPosition, newPosition, this);
    }
}