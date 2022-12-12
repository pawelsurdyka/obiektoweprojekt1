package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class App extends Application {
    List<Animal> A = new CopyOnWriteArrayList<>();
    List<Grass> G = new CopyOnWriteArrayList<>();
    Vector2d[] positions = new Vector2d[2];
    Vector2d[] Border;
    int xs;
    int ys;
    int xe;
    int ye;
    GrassField map;
    SimulationEngine engine;
    Thread engineThread;
    @Override
    public void init(){
        try {
            map = new GrassField(10);
            positions[0] = new Vector2d(2,2);
            positions[1] = new Vector2d(3,4);
            engine = new SimulationEngine(map, positions);
        } catch(IllegalArgumentException ex) {
            System.out.println(ex);
        }
    }

    public void refresh(GridPane grid){
        Platform.runLater(() -> {
            grid.setGridLinesVisible(false);
            grid.getColumnConstraints().clear();
            grid.getRowConstraints().clear();
            grid.getChildren().clear();
            grid.setGridLinesVisible(true);

            A = engine.getA();
            G = engine.getG();
            Border = engine.getBorder();
            xs = Border[0].x;
            ys = Border[0].y;
            xe = Border[1].x;
            ye = Border[1].y;
            for (int i = 0; i <= xe - xs + 1; i++) {
                for (int j = 0; j <= ye - ys + 1; j++) {
                    int X = i + xs - 1;
                    int Y = ye - j + 1;
                    if (i == 0 && j != 0) {
                        grid.getRowConstraints().add(new RowConstraints(50)); // column 1 is 200 wide
                        Label lab1 = new Label("" + Y);
                        GridPane.setHalignment(lab1, HPos.CENTER);
                        grid.add(lab1, i, j, 1, 1);
                    } else if (j == 0 && i != 0) {
                        grid.getColumnConstraints().add(new ColumnConstraints(50)); // column 0 is 100 wide
                        Label lab1 = new Label("" + X);
                        GridPane.setHalignment(lab1, HPos.CENTER);
                        grid.add(lab1, i, j, 1, 1);
                    } else if (i == 0 && j == 0) {
                        grid.getRowConstraints().add(new RowConstraints(50)); // column 1 is 200 wide
                        grid.getColumnConstraints().add(new ColumnConstraints(50)); // column 0 is 100 wide
                        Label lab1 = new Label("x/y");
                        GridPane.setHalignment(lab1, HPos.CENTER);
                        grid.add(lab1, i, j, 1, 1);
                    } else {
                        if(G!=null)
                            for (Grass g : G) {
                                if (g.getPosition().x == X && g.getPosition().y == Y) {
                                    GuiElementBox elem = new GuiElementBox(g);
                                    grid.add(elem.vbox, i, j, 1, 1);
                                }
                            }
                        if(A!=null)
                            for (Animal a : A) {
                                if (a.getPosition().x == X && a.getPosition().y == Y) {
                                    GuiElementBox elem = new GuiElementBox(a);
                                    grid.add(elem.vbox, i, j, 1, 1);
                                }
                            }
                    }
                }
            }
        });
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Świat");

        GridPane grid = new GridPane();

        grid.setGridLinesVisible(true);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Label lab = new Label("Ruchy: ");
        lab.setAlignment(Pos.CENTER);
        hbox.getChildren().add(lab);
        TextField moves = new TextField("f b r l f f r r f f f f f f f f");
        hbox.getChildren().add(moves);

        Button S = new Button("Start");
        S.setDefaultButton(true);
        hbox.getChildren().add(S);

        S.setOnAction(e -> {
            String s = moves.getText();

//            List<String> args = s;
            new OptionsParser();
            ArrayList<MoveDirection> directions = OptionsParser.parse(s);
            engine.setDirections(directions);
            engineThread = new Thread(engine);
            engineThread.start();
        });

        vbox.getChildren().add(hbox);
        vbox.getChildren().add(grid);
        Scene scene = new Scene(vbox, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        Thread t = new Thread(() -> {
            while (true) {
                if (map.update) {
                    refresh(grid);
                    //refhresh
                    map.update = false;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        });
//        String args = "f b r l f f r r f f f f f f f f";
//        ArrayList<MoveDirection> directions = OptionsParser.parse(args);
//        engine.setDirections(directions);
//        engineThread = new Thread(engine);
//        engineThread.start();
        refresh(grid);
        t.start();
    }
}