package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * @author s215705
 * */
public class Checkpoint implements BoardElement{

    private int number;

    public Checkpoint(int number){
        this.number = number;
    }

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
