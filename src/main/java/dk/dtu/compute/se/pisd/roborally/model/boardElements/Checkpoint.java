package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * @author s215705
 * */
public class Checkpoint implements BoardElement{

    private int number;

    public Checkpoint(int number){
        this.number = number;
    }
    /**
     * Adds one to the players checkpointcounter if he has landed in the right order
     * */
    @Override
    public void interact(Player player) {
        int checkpointsReached = player.getCheckpointsReached();
        if(checkpointsReached == number -1){
            player.setCheckpointsReached(checkpointsReached + 1);
        }
    }

    public int getNumber() {
        return number;
    }
}
