package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.TurnDirection;

public class Gear implements BoardElement{
    private TurnDirection turnDirection;

    public Gear(TurnDirection turnDirection){
        this.turnDirection = turnDirection;
    }
    @Override
    public void interact(Player player) {
        if(turnDirection.equals(TurnDirection.LEFT)) {
            player.setHeading(player.getHeading().prev());
        }else {
            player.setHeading(player.getHeading().next());
        }
    }

    public TurnDirection getTurnDirection() {
        return turnDirection;
    }
}
