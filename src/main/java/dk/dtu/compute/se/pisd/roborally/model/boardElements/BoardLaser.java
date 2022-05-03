package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BoardLaser implements BoardElement {

    private Heading shotDirection;
    private int laserCount;

    public BoardLaser(Heading shotDirection, int laserCount){
        this.shotDirection = shotDirection;
        this.laserCount = laserCount;
    }

    @Override
    public void interact(Player player) {
        //Special boardElement don't do anything
    }

    public Heading getShotDirection() {
        return shotDirection;
    }

    public int getLaserCount() {
        return laserCount;
    }
}



