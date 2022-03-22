/**
        *  This file is part of the initial project provided for the
        *  course "Project in Software Development (02362)" held at
        *  DTU Compute at the Technical University of Denmark.
        *
        *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
         *  This software is free software; you can redistribute it and/or modify
         *  it under the terms of the GNU General Public License as published by
         *  the Free Software Foundation; version 2 of the License.
         *
         *  This project is distributed in the hope that it will be useful,
         *  but WITHOUT ANY WARRANTY; without even the implied warranty of
         *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
         *  GNU General Public License for more details.
         *
         *  You should have received a copy of the GNU General Public License
         *  along with this project; if not, write to the Free Software
         *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
         *
         */
package dk.dtu.compute.se.pisd.roborally.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author s215722
 * s215722 has added the additional commands
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Fwd"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("Fast Fwd"),
    // XXX Assignment A3
    MOVE_TWO("Fwd 2"),
    U_TURN("U-turn"),
    BACK_UP("Back 1"),
    AGAIN("Again"),

    // XXX Assignment V3
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT);

    /* this option is created for interactive card.
    Two options will be displayed and the player can choose between moving left or right*/

    final public String displayName;

    // XXX Assignment V3
    // Command(String displayName) {
    //     this.displayName = displayName;
    // }
    //
    // replaced by the code below:

    final private List<Command> options;

    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {
        return !options.isEmpty();
    }
/* checks whether the card is interactive (decides if above coded option is to be displayed or not */

    public List<Command> getOptions() {
        return options;
    }
/*options coded above are returned*/
}
