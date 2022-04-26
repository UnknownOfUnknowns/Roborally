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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * This package is responsible for coordination of loading, saving and new games.
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            ArrayList<String> boards = new ArrayList<>();
            boards.add("defaultboard");
            boards.add("emptyboard");
            boards.add("greatBoard");
            ChoiceDialog<String> boardDialog = new ChoiceDialog<>(boards.get(0), boards);
            boardDialog.setTitle("Player number");
            boardDialog.setHeaderText("Select number of players");
            Optional<String> boardResult = boardDialog.showAndWait();

            Board board;
            board = boardResult.map(LoadBoard::loadBoard).orElseGet(() -> LoadBoard.loadBoard("defaultboard"));
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            // XXX: V2
            board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();
            board.attach(this);
            roboRally.createBoardView(gameController);
            RepositoryAccess.getRepository().createGameInDB(board);
        }
    }

    public void saveGame() {
        if(gameController != null){
            RepositoryAccess.getRepository().updateGameInDB(gameController.board);
        }
    }

    public void loadGame() {

        List<GameInDB> savedGames = RepositoryAccess.getRepository().getGames();
        if(savedGames != null && !savedGames.isEmpty()) {
            List<String> gameNames = new ArrayList<>();
            savedGames.forEach(e -> {gameNames.add(e.toString());});
            ChoiceDialog<String> dialog = new ChoiceDialog<>(gameNames.get(0), gameNames);
            dialog.setTitle("Player number");
            dialog.setHeaderText("Select number of players");
            Optional<String> result = dialog.showAndWait();

            savedGames.forEach(gameInDB -> {
                if(result.isPresent() && gameInDB.toString().equals(result.get())){
                    Board board = RepositoryAccess.getRepository().loadGameFromDB(gameInDB.id);
                    gameController = new GameController(board);
                    board.attach(this);
                    roboRally.createBoardView(gameController);
                }
            });
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            try {
                saveGame();
            }catch (Exception e){
                //this is made such that the game can be stopped even though the connection to the database fails
            }
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }
    /**
     * @author s215705
     * When the game is finnished this functions prompts the message to the player
     * */
    @Override
    public void update(Subject subject) {
        //The game is automatically saved whenever a move is made
        saveGame();
        if(gameController.board.getPhase().equals(Phase.GAME_FINISHED)){
            ButtonType okButton = new ButtonType("Finish", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> winnerFoundDialog = new Dialog<>();
            Player winner = gameController.board.getWinner();
            winnerFoundDialog.getDialogPane().getButtonTypes().add(okButton);
            winnerFoundDialog.getDialogPane().lookupButton(okButton).setDisable(false);
            winnerFoundDialog.setContentText(winner.getName() + " has won this game");
            winnerFoundDialog.showAndWait();
            stopGame();
        }
    }

}
