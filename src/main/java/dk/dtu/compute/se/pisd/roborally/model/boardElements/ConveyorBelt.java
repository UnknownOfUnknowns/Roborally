package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;

/**
 * @author s215705
 * */
public class ConveyorBelt implements BoardElement, PlayerMover {
    Heading heading;

    public ConveyorBelt(Heading heading){
        this.heading = heading;
    }

    @Override
    public void interact(Player player) {
        Space newSpace = player.board.getNeighbour(player.getSpace(), heading);
        if(newSpace.getPlayer() == null){
            try {
                moveToSpace(player, newSpace, heading);
            } catch (ImpossibleMoveException e) {
                //If the move cannot be completed just dont do anything
            }
        }
    }

    public Heading getDirection() {
        return heading;
    }

}
