/*
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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * Player is responsible for holding information about the individual players
 * @author s211638
 * s2116438 has been responsible for energyCubes and checkpointsReached
 */
public class Player extends Subject {
    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;
    private PlayerState state;
    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;
    private List<CommandCard> discardPile;
    private List<CommandCard> programmingPile;
    private int checkpointsReached;
    private int energyCubes;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.checkpointsReached = 0;
        this.space = null;
        this.energyCubes = 5;
        this.state = PlayerState.NORMAL;
        this.discardPile = new ArrayList<>();
        this.programmingPile = new ArrayList<>(Arrays.asList(
                new CommandCard(Command.FORWARD),
                new CommandCard(Command.FORWARD),
                new CommandCard(Command.FORWARD),
                new CommandCard(Command.MOVE_TWO),
                new CommandCard(Command.MOVE_TWO),
                new CommandCard(Command.MOVE_TWO),
                new CommandCard(Command.FAST_FORWARD),
                new CommandCard(Command.FAST_FORWARD),
                new CommandCard(Command.LEFT),
                new CommandCard(Command.LEFT),
                new CommandCard(Command.RIGHT),
                new CommandCard(Command.RIGHT),
                new CommandCard(Command.OPTION_LEFT_RIGHT),
                new CommandCard(Command.OPTION_LEFT_RIGHT),
                new CommandCard(Command.AGAIN),
                new CommandCard(Command.AGAIN),
                new CommandCard(Command.U_TURN),
                new CommandCard(Command.U_TURN),
                new CommandCard(Command.BACK_UP),
                new CommandCard(Command.BACK_UP)
        ));

        shuffleProgrammingPile();

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public int getCheckpointsReached() {
        return checkpointsReached;
    }

    public void setCheckpointsReached(int checkpointsReached) {
        this.checkpointsReached = checkpointsReached;}

    public int getEnergyCubes() {
        return energyCubes;
    }

    public void setEnergyCubes(int energyCubes) {
        this.energyCubes = energyCubes;
    }

    public CommandCard drawCardFromProgrammingPile(){
        //empty the discard pile whenever the programmingPile is empty
        if(programmingPile.isEmpty()){
            programmingPile.addAll(discardPile);
            discardPile = new ArrayList<>();
        }
        shuffleProgrammingPile();
        CommandCard card = programmingPile.get(0);
        programmingPile.remove(0);
        return card;
    }

    private void shuffleProgrammingPile(){
        for(int i = 0; i< 1000; i++){
            int firstIndex = (int) (Math.random()*programmingPile.size());
            CommandCard card = programmingPile.get(firstIndex);
            programmingPile.remove(card);
            programmingPile.add((int) (Math.random()*programmingPile.size()), card);
        }
    }

    public void addToDiscardPile(CommandCard commandCard){
        discardPile.add(commandCard);
    }

    public void addToDiscardPile(List<CommandCard> commandCards){
        discardPile.addAll(commandCards);
    }
    /**
     * Empty all cards to discard pile remove damage cards used in the program
     * */
    public void addAllToDiscardPile() {
        for(CommandCardField field : program){
            if(field.getCard() != null)
                addToDiscardPile(field.getCard());
            field.setCard(null);
        }
        for(CommandCardField field : cards){
            if(field.getCard() != null)
                addToDiscardPile(field.getCard());
            field.setCard(null);
        }

    }

    public List<CommandCard> getProgrammingPile() {
        return programmingPile;
    }

    public List<CommandCard> getDiscardPile() {
        return discardPile;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }


}
