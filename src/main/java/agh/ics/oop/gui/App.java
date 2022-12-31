package agh.ics.oop.gui;

import agh.ics.oop.*;
import agh.ics.oop.interfaces.*;
import agh.ics.oop.elements.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class App extends Application implements IPositionChangeObserver {
    private int startingAnimalsNo;
    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private int startingGrassNo;
    private int grassPerDay;
    private int whenIsFull;
    private int energyToChild;
    private int minMutation;
    private int maxMutation;
    private int lengthOfGenome;
    private int fieldSize;
    private int chartValueNo;
    private int delay;
    private AbstractWorldMap map1;
    private SimulationEngine engine1;
    private AbstractWorldMap map2;
    private SimulationEngine engine2;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private final HashMap<MapDirection, Image> imageAnimals = new HashMap<>();
    private Image imageGrass;
    private Image imageNothing;

    private final ArrayList<XYChart.Series<Number,Number>> allChartSeries1 = new ArrayList<>();
    private final ArrayList<XYChart.Series<Number,Number>> allChartSeries2 = new ArrayList<>();
    private final GridPane grid1 = new GridPane();
    private final GridPane grid2 = new GridPane();
    private final GridPane trackingGrid1 = new GridPane();
    private final GridPane trackingGrid2 = new GridPane();

    private GuiElementBox[][] matrix1;
    private GuiElementBox[][] matrix2;

    private boolean crazy;
    private boolean randomMutation;
    private boolean slightMutauion;
    private boolean toxicCorpse;

    private final Label labelGens1 = new Label();
    private final Label labelGens2 = new Label();

    private String genotype1 = "";
    private String genotype2 = "";
    boolean isThisRoundedMap;


    void stats(SimulationEngine engine, AbstractWorldMap map, Label labelGens, ArrayList<XYChart.Series<Number,Number>> allChartSeries){
        ArrayList<String> chartsOrder = engine.getChartsOrder();
        ArrayList<ArrayList<Double>> chartsInfo = engine.getChartsInfo();

        CopyOnWriteArrayList<String> genotypesToCheck = map.getGenotypes();
        HashMap<String,Integer> Count = new HashMap<>();
        String mostDominantGenotype = "";
        double all = map.getA().size();
        int x;
        double mostDominantCount = 0;
        for(String actualGenotype:genotypesToCheck){
            if(Count.containsKey(actualGenotype)){
                x = Count.remove(actualGenotype);
                Count.put(actualGenotype,x+1);
                if(x + 1 > mostDominantCount){
                    mostDominantGenotype = actualGenotype;
                    mostDominantCount = x+1;
                }
            }
            else{
                Count.put(actualGenotype,1);
                if(mostDominantGenotype.equals("")){
                    mostDominantGenotype = actualGenotype;
                    mostDominantCount = 1;
                }
            }
        }
        if(all == 0){
            labelGens.setText("All animals are dead :(");
        }
        else {
            if(map == map1)
                genotype1 = mostDominantGenotype;
            else
                genotype2 = mostDominantGenotype;
            for (int i = 0; i < chartsOrder.size(); i++) {
                allChartSeries.get(i).getData().clear();
                for (int k = chartsInfo.get(i).size() - 1; k >= chartsInfo.get(i).size() - chartValueNo; k--) {
                    allChartSeries.get(i).getData().add(new XYChart.Data<>((k), chartsInfo.get(i).get(k)));
                }
            }
            labelGens.setText(mostDominantGenotype + "; " + df.format(mostDominantCount * 100 / all) + "%");
        }
    }

    void createGrid(GridPane grid, GuiElementBox[][] matrix){
        grid.setGridLinesVisible(true);
        for(int i=0;i<width;i++){
            grid.getColumnConstraints().add(new ColumnConstraints(fieldSize));
        }
        for(int i=0;i<height;i++){
            grid.getRowConstraints().add(new RowConstraints(fieldSize));
        }
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                matrix[i][j] = new GuiElementBox(fieldSize,imageNothing, -1, startEnergy);
                GridPane.setConstraints(matrix[i][j].view, i, j);
                grid.getChildren().add(matrix[i][j].view);
            }
        }
    }

    void prepareGrid(GuiElementBox[][] matrix, AbstractWorldMap map){
        CopyOnWriteArrayList<Animal> A = map.getA();
        for(Animal a:A){
            int x = a.getPosition().x;
            int y = a.getPosition().y;
            matrix[x][y].update(imageAnimals.get(a.getDirection()), a.getEnergy());
        }
    }

    void updateGrid(AbstractWorldMap map, GuiElementBox[][] matrix, Vector2d position, SimulationEngine engine, GridPane trackingGrid){
        int x = position.x;
        int y = position.y;
        Object object = map.objectAt(position);
        if(object instanceof Animal a) {
            matrix[x][y].update(imageAnimals.get(a.getDirection()), a.getEnergy());
            matrix[x][y].view.setOnMouseClicked(event -> {
                if (!engine.getRunning()) {
                    map.setTrackedAnimal(a);
                    updateTracking(map, trackingGrid);
                }
            });
        }
        else if(object instanceof Grass g) {
            if(g.getPosition().follows(map.jungleLL) && g.getPosition().precedes(map.jungleUR))
                matrix[x][y].update(imageGrass, -3);
            else
                matrix[x][y].update(imageGrass, -1);
        }
        else
            matrix[x][y].update(imageNothing, -1);
    }

    void loadResources(){
        MapDirection[] directions = new MapDirection[8];
        for(int i=0;i<8;i++) {
            directions[i] = MapDirection.NORTH;
            for(int k=0;k<i;k++)
                directions[i] = directions[i].next();
            imageAnimals.put(directions[i], new Image(directions[i].getImage()));
        }
        imageGrass = new Image("file:src/main/resources/grass.png");
        imageNothing = new Image("file:src/main/resources/nothing.png");
    }

    VBox prepareInitScene(CheckBox checkBox1, CheckBox checkBox2,CheckBox checkBox3, CheckBox checkBox4, ArrayList<TextField> textFields, Button startButton){
        VBox vbox = new VBox();
        Label scenetitle = new Label("Enter initial data");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        vbox.getChildren().add(scenetitle);

        ArrayList<String> labels = new ArrayList<>(){
            {
                add("Number of animals: ");
                add("Map width: ");
                add("Map height: ");
                add("Animal starting energy: ");
                add("Grass energy: ");
                add("Number of Grass");
                add("Grass per day");
                add("When animal is full");
                add("Energy transferred to child [%]:");
                add("Minimum mutations");
                add("Maximum mutations");
                add("Length of genome");
            }
        };
        textFields.add(new TextField("20"));
        textFields.add(new TextField("20"));
        textFields.add(new TextField("10"));
        textFields.add(new TextField("50"));
        textFields.add(new TextField("100"));
        textFields.add(new TextField("10"));
        textFields.add(new TextField("2"));
        textFields.add(new TextField("10"));
        textFields.add(new TextField("20"));
        textFields.add(new TextField("0"));
        textFields.add(new TextField("2"));
        textFields.add(new TextField("20"));

        ArrayList<HBox> hboxes = new ArrayList<>(){
            {
                for(int i=0;i<labels.size();i++)
                    add(new HBox());
            }
        };

        for(int i=0;i<labels.size();i++){
            hboxes.get(i).getChildren().add(new Label(labels.get(i)));
            hboxes.get(i).getChildren().add(textFields.get(i));
            vbox.getChildren().add(hboxes.get(i));
            hboxes.get(i).setAlignment(Pos.CENTER);
        }

        vbox.getChildren().add(checkBox1);
        vbox.getChildren().add(checkBox2);
        vbox.getChildren().add(checkBox3);
        vbox.getChildren().add(checkBox4);

        startButton.setDefaultButton(true);
        vbox.getChildren().add(startButton);

        vbox.setSpacing(20);
        VBox.setMargin(vbox, new Insets(25, 25, 25, 25));
        return vbox;
    }

    @Override
    public void start(Stage primaryStage){
        loadResources();

        VBox root = new VBox();
        primaryStage.setTitle("Starting screen");

        ArrayList<TextField> textFields = new ArrayList<>();
        CheckBox checkBox1 = new CheckBox("Random Mutations");
        CheckBox checkBox2 = new CheckBox("Slight Mutations(override Random Mutations if checked)");
        CheckBox checkBox3 = new CheckBox("20% chance to miss a gen(override full predestination)");
        CheckBox checkBox4 = new CheckBox("Map with toxic corpses(override forested equators)");
        Button startButton = new Button("Start");
        root.getChildren().add(prepareInitScene(checkBox1, checkBox2,checkBox3,checkBox4, textFields, startButton));

        Scene scene = new Scene(root, 550, 900);

        primaryStage.setScene(scene);
        primaryStage.show();

        startButton.setOnAction(e -> {
            ArrayList<Integer> values = new ArrayList<>();
            for(TextField t:textFields){
                values.add(Integer.parseInt(t.getText()));
            }
            this.startingAnimalsNo = values.get(0);
            this.width = values.get(1);
            this.height = values.get(2);
            this.startEnergy = values.get(3);
            this.moveEnergy = 3;
            this.plantEnergy = values.get(4);
            this.startingGrassNo = values.get(5);
            this.grassPerDay = values.get(6);
            this.whenIsFull = values.get(7);
            this.energyToChild = values.get(8);
            this.minMutation = values.get(9);
            this.maxMutation = values.get(10);
            this.lengthOfGenome = values.get(11);
            this.chartValueNo = 100;
            this.delay = 50;

            this.fieldSize = Math.min((1500-50)/(2*this.width+2), (800/2)/(this.height+1));
            randomMutation = checkBox1.isSelected();
            slightMutauion = checkBox2.isSelected();
            if(slightMutauion)
                randomMutation = false;
            crazy = checkBox3.isSelected();
            toxicCorpse = checkBox4.isSelected();


            map1 = new RoundedMap(this.width, this.height, this.startEnergy, this.moveEnergy, this.plantEnergy,
                    this.startingGrassNo,this.grassPerDay,this.whenIsFull, this.energyToChild,this.minMutation,
                    this.maxMutation,this.lengthOfGenome,this.randomMutation,this.slightMutauion,this.crazy,
                    this.toxicCorpse,this.isThisRoundedMap);
            if(crazy)
                map1.setCrazyToTrue();
            if(randomMutation)
                map1.setRandomMutationToTrue();
            if(slightMutauion)
                map1.setSlightMutauionToTrue();
            if(toxicCorpse)
                map1.setToxicCorpsetoTreu();
            map1.setRoundedToTrue();
            engine1 = new SimulationEngine(map1, this.startingAnimalsNo,this.startingGrassNo, this.startEnergy, this.chartValueNo, this.delay);
            engine1.setObserver(this);


            map2 = new DevilMap(this.width, this.height, this.startEnergy, this.moveEnergy, this.plantEnergy,
                    this.startingGrassNo,this.grassPerDay,this.whenIsFull, this.energyToChild,this.minMutation,
                    this.maxMutation,this.lengthOfGenome,this.randomMutation,this.slightMutauion,this.crazy,
                    this.toxicCorpse,this.isThisRoundedMap);
            if(crazy)
                map2.setCrazyToTrue();
            if(randomMutation)
                map2.setRandomMutationToTrue();
            if(slightMutauion)
                map2.setSlightMutauionToTrue();
            if(toxicCorpse)
                map2.setToxicCorpsetoTreu();
            engine2 = new SimulationEngine(map2, this.startingAnimalsNo,this.startingGrassNo, this.startEnergy, this.chartValueNo, this.delay);
            engine2.setObserver(this);

            grid1.setAlignment(Pos.CENTER);
            grid2.setAlignment(Pos.CENTER);

            matrix1 = new GuiElementBox[width][height];
            matrix2 = new GuiElementBox[width][height];
            createGrid(grid1, matrix1);
            createGrid(grid2, matrix2);
            prepareGrid(matrix1, map1);
            prepareGrid(matrix2, map2);
            primaryStage.close();
            simulate(primaryStage);
        });
    }

    VBox createCharts(ArrayList<String> chartsOrder, ArrayList<XYChart.Series<Number,Number>> allChartSeries){

        VBox boxCharts = new VBox();

        ArrayList<NumberAxis> allxAxis = new ArrayList<>();
        ArrayList<NumberAxis> allyAxis = new ArrayList<>();
        ArrayList<LineChart<Number,Number>> allCharts = new ArrayList<>();

        for(int i=0;i<4;i++) {
            allxAxis.add(new NumberAxis());
            allxAxis.get(i).setForceZeroInRange(false);
            allyAxis.add(new NumberAxis());
            allCharts.add(new LineChart<>(allxAxis.get(i), allyAxis.get(i)));
            allCharts.get(i).setCreateSymbols(false);
        }
        for(int i=0;i<2;i++) {
            allChartSeries.add(new XYChart.Series<>());
            allCharts.get(0).getData().add(allChartSeries.get(i));
            allCharts.get(0).setAnimated(false);
        }
        for(int i=2;i<5;i++) {
            allChartSeries.add(new XYChart.Series<>());
            allCharts.get(i-1).getData().add(allChartSeries.get(i));
            allCharts.get(i-1).setAnimated(false);
        }

        for(int i=0;i<5;i++){
            allChartSeries.get(i).setName(chartsOrder.get(i));
        }
        HBox chartsFirstRow = new HBox();
        for(int i=0;i<2;i++)
            chartsFirstRow.getChildren().add(allCharts.get(i));
        HBox chartsSecondRow = new HBox();
        for(int i=2;i<4;i++)
            chartsSecondRow.getChildren().add(allCharts.get(i));
        boxCharts.getChildren().add(chartsFirstRow);
        boxCharts.getChildren().add(chartsSecondRow);
        return boxCharts;
    }

    VBox createInfo(Label name, GridPane grid, GridPane trackingGrid, SimulationEngine engine, Label labelStats, AbstractWorldMap map,
                    GuiElementBox[][] matrix, Label labelGens, int mapNo){
        VBox boxInfo = new VBox();

        boxInfo.getChildren().add(name);
        boxInfo.getChildren().add(grid);
        boxInfo.getChildren().add(trackingGrid);

        ToggleButton startStop = new ToggleButton("Stop");
        startStop.setSelected(false);
        startStop.setOnAction(e -> {
            if(startStop.isSelected()){
                engine.setRunning(false);
                startStop.setText("Start");
            }
            else{
                engine.setRunning(true);
                startStop.setText("Stop");
            }
        });

        Button buttonGenotype = new Button("Show animals with this genotype");
        buttonGenotype.setOnAction(e -> {
            if(engine.getRunning()) {
                labelStats.setText("First stop the simulation!");
            }
            else {
                showGenotypes(map, mapNo, matrix);
                labelStats.setText("Animals showed");
            }
        });

        HBox gensInfo = new HBox();
        gensInfo.getChildren().add(labelGens);
        gensInfo.getChildren().add(buttonGenotype);
        gensInfo.setAlignment(Pos.CENTER);
        boxInfo.getChildren().add(gensInfo);

        HBox buttonStop = new HBox();
        buttonStop.getChildren().add(startStop);
        buttonStop.getChildren().add(labelStats);
        buttonStop.setAlignment(Pos.CENTER);
        boxInfo.getChildren().add(buttonStop);

        boxInfo.setAlignment(Pos.CENTER);
        return boxInfo;
    }

    void simulate(Stage primaryStage){
        primaryStage.setTitle("World");
        Button exit = new Button("Exit");
        exit.setMinSize(50,25);
        HBox maps = new HBox();
        maps.setAlignment(Pos.CENTER);

        ArrayList<String> chartsOrder = new ArrayList<>() {
            {
                add("Number of animals");
                add("Number of grass");
                add("Average energy of living animals");
                add("Average lifespan of dead animals");
                add("Number of unoccupied fields");
            }
        };
        Thread engineThread1;
        engineThread1 = new Thread(engine1);
        engine1.setChartsInfo(chartsOrder);

        VBox first = new VBox();
        Label name1;
        name1 = new Label("rounded map with equator and full predestination");

        if(toxicCorpse) {
            name1 = new Label("rounded map with toxic corpses with full predestination");
            if (crazy) {
                name1 = new Label("rounded map with toxic corpses without full predestination");
                if (randomMutation)
                    name1 = new Label("rounded map with toxic corpses and random mutatuions without full predestination");
                if (slightMutauion)
                    name1 = new Label("rounded map with toxic corpses and slight mutatuions without full predestination");
            } else {
                if (randomMutation)
                    name1 = new Label("rounded map with toxic corpses and random mutatuions and full predestination");
                if (slightMutauion)
                    name1 = new Label("rounded map with toxic corpses and slight mutatuions and full predestination");
            }
        }
        else {
            name1 = new Label("rounded map with equator with full predestination");
            if (crazy) {
                name1 = new Label("rounded map with equator without full predestination");
                if (randomMutation)
                    name1 = new Label("rounded map with equator and random mutatuions without full predestination");
                if (slightMutauion)
                    name1 = new Label("rounded map with equator and slight mutatuions without full predestination");
            } else {
                if (randomMutation)
                    name1 = new Label("rounded map with equator and random mutatuions and full predestination");
                if (slightMutauion)
                    name1 = new Label("rounded map with equator and slight mutatuions and full predestination");
            }
        }



        first.getChildren().add(createInfo(name1, grid1, trackingGrid1, engine1, new Label(), map1, matrix1, labelGens1,1));
        first.getChildren().add(createCharts(chartsOrder, allChartSeries1));
        maps.getChildren().add(first);

        maps.getChildren().add(exit);

        Thread engineThread2;
        engineThread2 = new Thread(engine2);
        engine2.setChartsInfo(chartsOrder);

        VBox second = new VBox();
        Label name2;
        name2 = new Label("devil map with equator and full predestination");

        if(toxicCorpse) {
            name2 = new Label("devil map with toxic corpses with full predestination");
            if (crazy) {
                name2 = new Label("devil map with toxic corpses without full predestination");
                if (randomMutation)
                    name2 = new Label("devil map with toxic corpses and random mutatuions without full predestination");
                if (slightMutauion)
                    name2 = new Label("devil map with toxic corpses and slight mutatuions without full predestination");
            } else {
                if (randomMutation)
                    name2 = new Label("devil map with toxic corpses and random mutatuions and full predestination");
                if (slightMutauion)
                    name2 = new Label("devil map with toxic corpses and slight mutatuions and full predestination");
            }
        }
        else {
            name2 = new Label("devil map with equator with full predestination");
            if (crazy) {
                name2 = new Label("devil map with equator without full predestination");
                if (randomMutation)
                    name2 = new Label("devil map with equator and random mutatuions without full predestination");
                if (slightMutauion)
                    name2 = new Label("devil map with equator and slight mutatuions without full predestination");
            } else {
                if (randomMutation)
                    name2 = new Label("devil map with equator and random mutatuions and full predestination");
                if (slightMutauion)
                    name2 = new Label("devil map with equator and slight mutatuions and full predestination");
            }
        }

        second.getChildren().add(createInfo(name2, grid2, trackingGrid2, engine2, new Label(), map2, matrix2, labelGens2,2));
        second.getChildren().add(createCharts(chartsOrder, allChartSeries2));
        maps.getChildren().add(second);

        Scene scene = new Scene(maps, 1500, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
        engineThread1.start();
        engineThread2.start();

        exit.setOnAction(e -> {
            engineThread1.stop();
            engineThread2.stop();
            primaryStage.close();
        });
    }

    void showGenotypes(AbstractWorldMap map, int mapNo, GuiElementBox[][] matrix){
        String genotype;
        if(mapNo==1)
            genotype = genotype1;
        else
            genotype = genotype2;
        CopyOnWriteArrayList<Animal> A = map.getA();
        for(Animal a:A){
            if(a.getStringGens().equals(genotype)){
                Platform.runLater(() -> matrix[a.getPosition().x][a.getPosition().y].update(imageAnimals.get(a.getDirection()), -2));
            }
        }
    }


    void updateTracking(AbstractWorldMap map, GridPane trackingGrid){
        trackingGrid.setGridLinesVisible(false);
        trackingGrid.getChildren().clear();
        trackingGrid.getColumnConstraints().clear();
        trackingGrid.getRowConstraints().clear();
        trackingGrid.setGridLinesVisible(true);
        for(int i=0;i<2;i++)
            trackingGrid.getRowConstraints().add(new RowConstraints(30));
        for(int i=0;i<6;i++)
            trackingGrid.getColumnConstraints().add(new ColumnConstraints(90));
        trackingGrid.getColumnConstraints().add(new ColumnConstraints(170));
        trackingGrid.add(new Label("Children no.:"),0,0);
        trackingGrid.add(new Label("Eaten Grass no.:"),1,0);
        trackingGrid.add(new Label("Day of death:"),2,0);
        trackingGrid.add(new Label("Age:"),3,0);
        trackingGrid.add(new Label("Energy:"),4,0);
        trackingGrid.add(new Label("Active gen:"),5,0);
        trackingGrid.add(new Label("Genotype:"),6,0);
        int[] info = map.getTrackedInfo();
        if(map.getTrackedAnimal() == null){
            for(int i=0;i<7;i++)
                trackingGrid.add(new Label("-"),i,1);
        }
        else {
            for (int i = 0; i < 4; i++) {
                if(info[i]==-1)
                    trackingGrid.add(new Label("It's still alive!"), i, 1);
                else
                    trackingGrid.add(new Label(Integer.toString(info[i])), i, 1);
            }
            trackingGrid.add(new Label(map.getTrackedAnimal().getStringEnergy()), 4, 1);
            trackingGrid.add(new Label(map.getTrackedAnimal().getStringCurrGen()), 5, 1);
            trackingGrid.add(new Label(map.getTrackedAnimal().getStringGens()), 6, 1);
        }
        trackingGrid.setAlignment(Pos.CENTER);
    }

    public void dayEnded(SimulationEngine engine, AbstractWorldMap map){
        if(map == map1) {
            Platform.runLater(() -> stats(engine, map1, labelGens1, allChartSeries1));
            Platform.runLater(() -> updateTracking(map, trackingGrid1));
        }
        else {
            Platform.runLater(() -> stats(engine, map2, labelGens2, allChartSeries2));
            Platform.runLater(() -> updateTracking(map, trackingGrid2));
        }
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, AbstractWorldMap map) {
        if(map == map1) {
            Platform.runLater(() -> {
                updateGrid(map1, matrix1, oldPosition, engine1, trackingGrid1);
                updateGrid(map1, matrix1, newPosition, engine1, trackingGrid1);
            });
        }
        if(map == map2){
            Platform.runLater(() -> {
                updateGrid(map2, matrix2, oldPosition, engine2, trackingGrid2);
                updateGrid(map2, matrix2, newPosition, engine2, trackingGrid2);
            });
        }
    }


}