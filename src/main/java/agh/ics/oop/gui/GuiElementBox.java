package agh.ics.oop.gui;

import agh.ics.oop.Animal;
import agh.ics.oop.IMapElement;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GuiElementBox extends Application {
    Image image;
    VBox vbox = new VBox();
    public GuiElementBox(IMapElement element){
        image = new Image(element.getImage());
        ImageView view = new ImageView();
        view.setImage(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        Label lab1 = new Label("Trawa");
        if(element instanceof Animal){
            lab1 = new Label(((Animal) element).getPosition().toString());
        }
        ColorAdjust colorAdjust = new ColorAdjust();
        view.setEffect(colorAdjust);
        vbox.getChildren().add(view);
        vbox.getChildren().add(lab1);
//        vbox.setMaxWidth(20);
//        vbox.setMaxHeight(20);
        vbox.setAlignment(Pos.CENTER);
    }
    @Override
    public void start(Stage newStage){

    }
}