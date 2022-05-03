package dk.dtu.compute.se.pisd.roborally.model;
/**
 * @author s215718
 * This is thrown if for some reason the player cannot be moved to the specified space
 * */
public class ImpossibleMoveException extends Exception{
    private Player player;
    private Space space;
    private Heading heading;
    public ImpossibleMoveException(Player player,
                                   Space space,
                                   Heading heading) {
        super("Move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}
