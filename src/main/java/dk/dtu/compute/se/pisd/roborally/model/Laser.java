package dk.dtu.compute.se.pisd.roborally.model;

import java.util.List;

public class Laser {

    public void fire(Space from, Heading direction){
        if(from != null) {
            if (from.getPlayer() != null) {
                Player player = from.getPlayer();
                player.setState(PlayerState.DAMAGED);
            } else {
                Space to = from.board.getNeighbour(from, direction);

                if (to != null) {
                    List<Heading> walls = to.getWalls();
                    if (walls != null) {
                        if(walls.contains(direction.opposite()))
                         return;
                    } else if(from.getWalls().contains(direction))
                        return;
                    else
                        fire(to, direction);
                }
            }
        }
    }
}
