package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;

public class Pit implements BoardElement {

    @Override
    public void interact(Player player) {
        player.setState(PlayerState.DAMAGED);
    }
}
