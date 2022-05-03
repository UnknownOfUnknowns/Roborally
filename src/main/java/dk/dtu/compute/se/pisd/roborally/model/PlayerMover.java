package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;
/**
 * @author s215705
 * This function handles the movement of a player from one space to another.
 * The movement is quite complicated procedure, therefore it is kept in this interface which can then be used
 * where it fits.
 * */
public interface PlayerMover {
    /**
     * @author s215705
     * Moves a player to a space if it is legal otherwise ImpossibleMoveException is thrown
     * */
    default void moveToSpace(@NotNull Player player,
                                    @NotNull Space space,
                                    @NotNull Heading heading) throws ImpossibleMoveException{
        Player playerOnSpace = space.getPlayer();
        Board board = space.board;
        if(playerOnSpace != null){
            Space nextSpace = board.getNeighbour(space, heading);
            if(nextSpace != null){
                moveToSpace(playerOnSpace, nextSpace, heading);
            }else{
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        if(space.getWalls() != null) {
            for (Heading head : space.getWalls()) {
                if (board.getNeighbour(space, head).equals(player.getSpace()))
                    throw new ImpossibleMoveException(player, space, heading);
            }
        }
        if(player.getSpace().getWalls() != null) {
            for (Heading head : player.getSpace().getWalls()) {
                if (board.getNeighbour(player.getSpace(), head).equals(space))
                    throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }

}
