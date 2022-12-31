package agh.ics.oop;


import agh.ics.oop.interfaces.*;
import agh.ics.oop.elements.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulationEngine implements Runnable, IPositionChangeObserver {
    private List<Animal> A;
    private final AbstractWorldMap map;
    private final int startEnergy;
    private final int startingAnimalsNo;
    private final int startingGrassNo;
    private boolean running;
    private IPositionChangeObserver observer;
    private final int delay;
    private final int chartValueNo;

    private ArrayList<ArrayList<Double>> chartsInfo = new ArrayList<>();
    private Double[] chartsSummarize;
    private ArrayList<String> chartsOrder = new ArrayList<>();


    public SimulationEngine(AbstractWorldMap map, int startingAnimalsNo,int startingGrassNo, int startEnergy, int chartValueNo, int delay) {
        this.map = map;
        this.map.setObserver(this);
        this.startingAnimalsNo = startingAnimalsNo;
        this.startingGrassNo = startingGrassNo;
        this.startEnergy = startEnergy;
        addAnimals();
        this.A = map.getA();
        this.running = true;
        this.delay = delay;
        this.chartValueNo = chartValueNo;
    }

    public void setChartsInfo(ArrayList<String> chartsOrder){
        this.chartsOrder = chartsOrder;
        chartsSummarize = new Double[chartsOrder.size()];
        for(int k=0;k<chartsOrder.size();k++) {
            chartsSummarize[k] = 0.0;
            chartsInfo.add(new ArrayList<>());
            for (int i = 0; i < chartValueNo; i++) {
                chartsInfo.get(k).add(0.0);
            }
        }
    }
    public ArrayList<ArrayList<Double>> getChartsInfo(){return chartsInfo;}

    public ArrayList<String> getChartsOrder(){return  chartsOrder;}
    public void setRunning(boolean bool){this.running = bool;}
    public boolean getRunning(){return this.running;}

    public void setObserver(IPositionChangeObserver observer){this.observer = observer;}

    public void addAnimals() {
        Vector2d[] positions = new Vector2d[startingAnimalsNo];
        int width = map.getWidth();
        int height = map.getHeight();
        int len = map.getLengthOfGenome();
        ArrayList<ArrayList<Integer>> gens = new ArrayList<>();
        ArrayList<Integer> freePositions = new ArrayList<>();
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){
                freePositions.add(j*width+i);
            }
        }
        for (int j = 0; j < startingAnimalsNo; j++) {
            Random rand = new Random();
            ArrayList<Integer> gensi = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                int g = rand.nextInt(8);
                gensi.add(g);
            }
            gens.add(gensi);

            int id = rand.nextInt(freePositions.size());
            int x = freePositions.get(id);
            freePositions.remove(id);
            positions[j] = new Vector2d(x%width, x/width);
        }
        for (int i = 0; i < startingAnimalsNo; i++) {
            map.place(new Animal(map, positions[i], gens.get(i), startEnergy, false), true);
        }
    }

    void refreshData(){
        double e=0;
        CopyOnWriteArrayList<Animal> animals = map.getA();
        for(Animal a:animals) {
            e += a.getEnergy();
        }
        if(A.size()>0) {
            e /= A.size();
        }
        else{
            e = 0;
        }
        for(int i=0;i<chartsOrder.size();i++){
            switch (chartsOrder.get(i)) {
                case "Number of animals" -> chartsInfo.get(i).add((double) A.size());
                case "Number of grass" -> chartsInfo.get(i).add((double) map.getG().size());
                case "Average energy of living animals" -> chartsInfo.get(i).add(e);
                case "Average lifespan of dead animals" -> chartsInfo.get(i).add(map.avgAge());
                case "Number of unoccupied fields" -> chartsInfo.get(i).add((double) map.getNumOfFreeLand());
            }
            chartsSummarize[i] += chartsInfo.get(i).get(chartsInfo.get(i).size()-1);
        }
    }
    File fileCSV;

    public void statistics()throws IOException {
        ArrayList<ArrayList<Double>> data = chartsInfo;
        ArrayList<String> columns = chartsOrder;
        if(map.isThisRoundedMap)
        {
            fileCSV = new File("RoundStatistics.csv");
        }
        else
            fileCSV = new File("DevilStatistics.csv");
        ArrayList<String[]> dataLine = new ArrayList<>();
        String[] S = new String[data.size()+1];
        S[0] = "Number of days:";
        for (int k = 0; k < data.size(); k++) {
            S[k+1] = columns.get(k);
        }
        dataLine.add(S);
        for(int i=chartValueNo;i<data.get(0).size();i++) {
            S = new String[data.size()+1];
            S[0] = String.valueOf(i-chartValueNo);
            for (int k = 0; k < data.size(); k++) {
                S[k+1] = String.valueOf(data.get(k).get(i));
            }
            dataLine.add(S);
        }
        Double[] average = chartsSummarize;
        S = new String[data.size()+1];
        S[0] = "Average";
        for (int k = 0; k < data.size(); k++) {
            S[k+1] = String.valueOf(average[k]);
        }
        dataLine.add(S);
        try (PrintWriter pw = new PrintWriter(fileCSV)) {
            dataLine.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    @Override
    public void run() {
        try{
            while(true){
                if(running){
                    if(map.getDays()==0){//       startingGrass
                        for(int i = 0;i<map.getStartingGrassNo();i++){
                            map.newGrass();
                        }
                    }
                    A = map.getA();
                    map.remove();       //        removeDead();
                    for (Animal a : A){ //        move();
                        a.move();
                    }
                    map.eat();          //        eating();
                    map.copulate();     //        copulating();
                    map.calcFree();
                    if(map.getToxicCorpse()){
                        map.newToxicGrass();
                    }
                    else {
                        map.newGrass();     //        newGrass();
                    }
                    for (Animal a : A) {
                        a.oldering();
                        a.nextGen();
                    }
                    map.nextDay();
                    refreshData();
                    statistics();
                    observer.dayEnded(this, this.map);
                    Thread.sleep(delay);
                }
                else
                    Thread.sleep(5);
            }
        }catch (InterruptedException | IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, AbstractWorldMap map) {
        this.observer.positionChanged(oldPosition, newPosition, map);
    }

    }