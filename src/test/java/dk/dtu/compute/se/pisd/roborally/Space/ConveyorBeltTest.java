package dk.dtu.compute.se.pisd.roborally.Space;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.ConveyorBelt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * @author s215705
 * */
public class ConveyorBeltTest {
    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
        board.getSpace(0,1).setBoardElement(new ConveyorBelt(Heading.SOUTH));
    }

    @Test
    void useConveyerBelt(){
        Board board = gameController.board;
        board.setPhase(Phase.ACTIVATION);
        Player current = board.getCurrentPlayer();
        current.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        gameController.executePrograms();
        Assertions.assertEquals(current.getSpace(), board.getSpace(0,2), "The player should be on the space following the ConveyerBelt");
    }


}
