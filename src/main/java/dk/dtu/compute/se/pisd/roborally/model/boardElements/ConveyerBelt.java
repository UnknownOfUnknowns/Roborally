package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
/**
 * @author s215705
 * */
public class ConveyerBelt implements BoardElement{
    Space space;
    Heading direction;

    public ConveyerBelt(Space space, Heading heading){
        this.space = space;
        this.direction = heading;
    }

    @Override
    public void interact(Player player) {
        Space newSpace = space.board.getNeighbour(space, direction);
        if(newSpace.getPlayer() == null){
            player.setSpace(newSpace);
            if(newSpace.getBoardElement() != null && newSpace.getBoardElement().getClass() != ConveyerBelt.class){
                newSpace.getBoardElement().interact(player);
            }
        }
    }

    public Heading getDirection() {
        return direction;
    }
}
