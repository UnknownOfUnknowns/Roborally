package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
/**
 * @author s211638
 * */
public class EnergySpace implements BoardElement{

    private int energyCubes;

    public EnergySpace() {
        this.energyCubes = 1;
    }

    @Override
    public void interact(Player player) {
        if (energyCubes > 0) {
            player.setEnergyCubes(player.getEnergyCubes() + 1);
            energyCubes--;
        }
        //Add an extra energy cube to the players energy level in case it is the fifth register
        if(player.board.getStep() == 4){
            player.setEnergyCubes(player.getEnergyCubes() + 1);
        }
    }

    public int getEnergyCubes() {
        return energyCubes;
    }
}
