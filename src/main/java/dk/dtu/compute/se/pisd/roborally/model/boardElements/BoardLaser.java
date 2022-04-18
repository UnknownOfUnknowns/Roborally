package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BoardLaser extends Laser implements BoardElement {

    private Heading shotDirection;
    private int laserCount;

    public BoardLaser(Heading shotDirection, int laserCount){
        this.shotDirection = shotDirection;
        this.laserCount = laserCount;
    }


    public void fire(Space from) {
        super.fire(from, shotDirection);
    }

    @Override
    public void interact(Player player) {

    }

    public Heading getShotDirection() {
        return shotDirection;
    }

    public int getLaserCount() {
        return laserCount;
    }
}



