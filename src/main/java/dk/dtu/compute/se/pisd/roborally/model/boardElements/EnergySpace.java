package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Player;

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
        if(player.board.getStep() == 4){
            player.setEnergyCubes(player.getEnergyCubes() + 1);
        }
    }
}
