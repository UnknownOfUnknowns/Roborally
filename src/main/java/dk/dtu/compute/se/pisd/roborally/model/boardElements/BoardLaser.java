package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class BoardLaser implements BoardElement {

    private Heading shotDirection;
    private int laserCount;

    public BoardLaser(Heading shotDirection, int laserCount){
        this.shotDirection = shotDirection;
        this.laserCount = laserCount;
    }

    @Override
    public void interact(Player player) {
        player.setState(PlayerState.DAMAGED);
    }

    public Heading getShotDirection() {
        return shotDirection;
    }

    public int getLaserCount() {
        return laserCount;
    }
}



