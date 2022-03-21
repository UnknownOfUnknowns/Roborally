package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public interface BoardElement {
    /**
     * @author s215705
     * This method is called whenever a player lands on a space with a boardelement
     * */
    void interact(Player player);
}
