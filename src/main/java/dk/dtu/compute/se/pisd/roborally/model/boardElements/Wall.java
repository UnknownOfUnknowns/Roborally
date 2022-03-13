package dk.dtu.compute.se.pisd.roborally.model.boardElements;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Wall {
    List<Heading> faces;
    Space space;
    public Wall(Space space, Heading... headings) {
        this.space = space;
        faces = Arrays.asList(headings);
    }
}
