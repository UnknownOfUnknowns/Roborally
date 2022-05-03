package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
/**
 * @author s215705
 * This interface is common for all board elements and specifies the methods they have in common
 * */
public interface BoardElement {
    /**
     * @author s215705
     * This method is called whenever a player lands on a space with a boardelement
     * */
    void interact(Player player);
}
