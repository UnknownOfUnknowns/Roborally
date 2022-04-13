package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
/**
 * @author s215705
 * */
public class ConveyorBelt implements BoardElement{
    Heading heading;

    public ConveyorBelt(Heading heading){
        this.heading = heading;
    }

    @Override
    public void interact(Player player) {
        Space newSpace = player.board.getNeighbour(player.getSpace(), heading);
        if(newSpace.getPlayer() == null){
            player.setSpace(newSpace);
            if(newSpace.getBoardElement() != null && newSpace.getBoardElement().getClass() != ConveyorBelt.class){
                newSpace.getBoardElement().interact(player);
            }
        }
    }

    public Heading getDirection() {
        return heading;
    }

}
