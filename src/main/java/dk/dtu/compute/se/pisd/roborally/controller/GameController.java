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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.RebootToken;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        if (space.getPlayer() == null) {
            Player currentPlayer = board.getCurrentPlayer();
            currentPlayer.setSpace(space);
            if (board.getPlayerNumber(currentPlayer) == board.getPlayersNumber() - 1) {
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                Player newPlayer = board.getPlayer(board.getPlayerNumber(currentPlayer) + 1);
                board.setCurrentPlayer(newPlayer);
            }
            board.setCounter(board.getCounter() + 1);
        }
    }

    // XXX: V2

    /**
     * Starts the programming phase of the robot and passes it to the Domain layer
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(player.drawCardFromProgrammingPile());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private boolean winnerFound(){
        if(board.getCheckpoints() != 0){
            for (Player player: board.getPlayers()) {
                if(player.getCheckpointsReached() == board.getCheckpoints()){
                    board.setWinner(player);
                    board.setPhase(Phase.GAME_FINISHED);
                    return true;
                }
            }
        }
        return false;
    }

    private void handleDamagedPlayer(Player player){
        Space rebootToken = board.getRebootToken();
        if(rebootToken.getBoardElement() != null && rebootToken.getBoardElement().getClass() == RebootToken.class){
            RebootToken token = (RebootToken) rebootToken.getBoardElement();
            if(rebootToken.getPlayer() != null){
                Player playerOnSpace = rebootToken.getPlayer();
                try {
                    moveToSpace(playerOnSpace, board.getNeighbour(playerOnSpace.getSpace(), token.getHeading()), token.getHeading());
                }catch (ImpossibleMoveException e){
                    e.printStackTrace();
                }
            }
            //moveToSpace cannot be used since the player should not move directly to a neighbouring space
            player.setSpace(rebootToken);
            int amountOfSpam = board.getAmountOfDamageCard(Command.SPAM);
            if(amountOfSpam >= 2) {
                List<CommandCard> cards = board.drawDamageCards(Command.SPAM, 2);
                player.addToDiscardPile(cards);
            } else {
                //TODO here the player should be able to choose which cards to draw instead
                player.addToDiscardPile(board.drawDamageCards(Command.SPAM, amountOfSpam));
            }
            player.addAllToDiscardPile();
            player.setState(PlayerState.NORMAL);
        }
    }

    // XXX: V2
    /**
     * @author s215705
     * @author s215722
     * Executes the active register for the current player. The function handles the possibility of a command being interactive
     * */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    if(card.command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }else if(card.command.equals(Command.AGAIN)){
                        CommandCard prevCard = currentPlayer.getProgramField(step-1).getCard();
                        if(prevCard.command.isInteractive()){
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        }
                    }else{
                        executeCommand(currentPlayer, card.command);
                    }
                    if(card.command == Command.SPAM || card.command == Command.TROJAN_HORSE || card.command == Command.WORM
                        || card.command == Command.VIRUS){
                        currentPlayer.addToDiscardPile(card);
                        currentPlayer.getProgramField(step).setCard(null);
                    }
                }


                if(winnerFound()) {
                    board.setPhase(Phase.GAME_FINISHED);
                    return;
                }
                  /* if the activation phase is active, the steps of the players are executed.
                    Once an interactive card is hit, we display the options, and then continue.

                */

                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    for(Player player : board.getPlayers()){
                        BoardElement element = player.getSpace().getBoardElement();
                        if(element != null)
                            element.interact(player);
                        if(player.getState() == PlayerState.DAMAGED){
                            handleDamagedPlayer(player);
                        }
                    }

                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        for(Player player : board.getPlayers()){
                            player.addAllToDiscardPile();
                        }
                        startProgrammingPhase();
                    }
                    /* we repeat for every player in order of turn, and once the activation phase for
                    every player has been executed, we return to the programming phase
                     */
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * @author s215722
     * Delegates the execution of a command to relevant functions
     * */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
    switch (command) {
        case FORWARD:
            this.moveForward(player);
            break;
        case RIGHT:
            this.turnRight(player);
            break;
        case LEFT:
            this.turnLeft(player);
            break;
        case FAST_FORWARD:
            this.fastForward(player);
            break;
        // XXX Assignment A3
        case MOVE_TWO:
            this.moveForward(player);
            this.moveForward(player);
            break;
        case U_TURN:
            this.turnLeft(player);
            this.turnLeft(player);
            break;
        case BACK_UP:
            this.moveBackward(player);
            break;
        case AGAIN:
            again(player, command);
            break;
        case SPAM:
            spam(player);
            break;
        default:
            // DO NOTHING (for now)
        }
    }
}

    /**
     * @author s215705
     * Moves a player to a space if it is legal otherwise ImpossibleMoveException is thrown
     * */
    private void moveToSpace(@NotNull Player player,
                             @NotNull Space space,
                             @NotNull Heading heading) throws ImpossibleMoveException{
        Player playerOnSpace = space.getPlayer();
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




    public void spam(@NotNull Player player){
        if(player != null){
            CommandCard card = player.getProgramField(0).getCard();
            executeCommand(player, card.command);
        }
    }

    /**
     * @author s215722
     * */
    // TODO Assignment V2
    public void moveForward(@NotNull Player player) {
        if (board != null && player != null && player.board == board) {
            Space currentSpace = player.getSpace();
            if (currentSpace != null) {
                Space newSpace = board.getNeighbour(currentSpace, player.getHeading());
                if (newSpace != null) {
                    try {
                        moveToSpace(player, newSpace, player.getHeading());
                    } catch (ImpossibleMoveException e){

                    }
                }
            }
        }
    }


    /**
     * @author s215722
     * */
    public void moveBackward(@NotNull Player player){
      this.turnLeft(player);
      this.turnLeft(player);
      moveForward(player);
      this.turnLeft(player);
      this.turnLeft(player);
    }
    /**
     * @author s215722
     * */
    public void again(@NotNull Player player, @NotNull Command command){
        for(int i = 1; i < 5; i++){
            if(player.getProgramField(i) != null && player.getProgramField(i).getCard().command == command){
                CommandCard prevCommandCard = player.getProgramField(i-1).getCard();
                if(prevCommandCard != null)
                    executeCommand(player, prevCommandCard.command);
                return;
            }
        }
    }

    /**
     * @author s215722
     * */
    public void fastForward(@NotNull Player player) {
        for (int i = 0; i < 3; i++)
            moveForward(player);
    }

    /**
     * @author s215722
     * */
    public void turnRight(@NotNull Player player) {
        if(board!=null && player!=null && player.board==board){
            Heading currentHeading = player.getHeading();
            if(currentHeading!=null){
                Heading newHeading= currentHeading.next();
                if(newHeading!=null){
                    player.setHeading(newHeading);}
            }
        }
    }

    /**
     * @author s215722
     * */
    public void turnLeft(@NotNull Player player) {
        if(board!=null && player!=null && player.board==board){
            Heading currentHeading = player.getHeading();
            if(currentHeading!=null){
                Heading newHeading= currentHeading.prev();
                if(newHeading!=null){
                    player.setHeading(newHeading);}
            }
        }
    }

    /**
     * @author s215722
     * */
    public void executeCommandOptionAndContinue(Command option){
        Player currentPlayer = board.getCurrentPlayer();
        int step = board.getStep();
        if(currentPlayer != null) {
            if (option != null)
                executeCommand(currentPlayer, option);
            board.setPhase(Phase.ACTIVATION);

            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                step++;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer(board.getPlayer(0));
                } else {
                    startProgrammingPhase();
                }
            }
        }
        if(!board.isStepMode())
            continuePrograms();
    }
    /* we set the activation phase for each player, in order, i.e we get and execute their
    respective commands. When the last player number
    is reached, we also assure that it is not the turn of the player #1. When the activation phase for all
    players is set, we activate the programming phase.
     */
    /**
     * @author s215718
     * Moves cards from the program to the cards line and vice versa, makes sure that an again-card cannot be put in
     * 1st place of the programming line
     * */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            // Make sure that again-cards cannot be put in the first programming field
            for(int i = 0; i < board.getPlayersNumber(); i++){
                if(sourceCard.command.equals(Command.AGAIN) && board.getPlayer(i).getProgramField(0) == target)
                    return false;
            }
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    public void fireLaser(Space from, Heading direction){
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
                        fireLaser(to, direction);
                }
            }
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
