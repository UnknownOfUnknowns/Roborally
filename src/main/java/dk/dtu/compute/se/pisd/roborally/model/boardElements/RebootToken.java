package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
/**
 * This must be a BoardElement since it is placed on the board, this way other classes know how it should be used,
 * see space view. It knows its heading in case more robots are put on the space.
 * */
public class RebootToken implements BoardElement{
    Heading heading;

    public RebootToken(Heading heading){
        this.heading = heading;
    }

    public Heading getHeading() {
        return heading;
    }

    @Override
    public void interact(Player player) {
        //Do not do anything here the necessary actions will be performed in the GameController
    }
}