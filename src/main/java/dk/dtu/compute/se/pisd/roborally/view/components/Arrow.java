package dk.dtu.compute.se.pisd.roborally.view.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * @author s215718
 * Draws the arrow for conveyerbelts
 * */
public class Arrow extends Polygon {
    public Arrow(double x, double y){
        super(x+6.0, y+0.0, x-10.0 , y-10.0, x+10.0, y+0.0, x-10.0, y+10.0);
        setFill(Color.LIGHTSTEELBLUE);
    }
}
