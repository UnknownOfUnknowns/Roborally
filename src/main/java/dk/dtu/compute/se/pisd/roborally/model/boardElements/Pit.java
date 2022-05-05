package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
/**
 * @author s215705
 * */
public class Pit implements BoardElement {

    @Override
    public void interact(Player player) {
        player.setState(PlayerState.DAMAGED);
    }
}
