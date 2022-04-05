package dk.dtu.compute.se.pisd.roborally.view.components;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author s215718
 * */
public class ConveyerBeltComponent extends Pane {
    public ConveyerBeltComponent(double width, double height) {
        Rectangle background = new Rectangle(width, height, Paint.valueOf("blue"));
        Rectangle belt = new Rectangle(0,5, width, height-10);
        Arrow arrow = new Arrow(width/2, height/2);
        getChildren().addAll(background,belt,arrow);
    }
}
