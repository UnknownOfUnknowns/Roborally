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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.ConveyorBelt;

import javax.sound.midi.Soundbank;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";
    /**
     * @author s215718
     * The board is first loaded into BoardTemplate afterwards spaces are set up with their elements and walls
     * */
    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(
                BoardElement.class, new Adapter<BoardElement>());

        Gson gson = builder.create();
        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(
                BOARDSFOLDER + "/" + boardname+".json");

        if(inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            JsonReader reader = gson.newJsonReader(streamReader);
            BoardTemplate boardTemplate = gson.fromJson(reader, BoardTemplate.class);
            try {
                reader.close();
            } catch (IOException e) {

            }
            if(boardTemplate != null) {
                Board board = new Board(boardTemplate.width, boardTemplate.height, boardname);
                if (boardTemplate.spaces != null) {
                    for (SpaceTemplate spaceTemplate : boardTemplate.spaces) {
                        Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
                        space.setWalls(spaceTemplate.walls);
                        BoardElement element = spaceTemplate.actions.get(0);
                        space.setBoardElement(element);
                        //Find the number of checkpoints in the game
                        if(element instanceof Checkpoint){
                            Checkpoint checkpoint = (Checkpoint) element;
                            if(checkpoint.getNumber() > board.getCheckpoints()){
                                board.setCheckpoints(checkpoint.getNumber());
                            }
                        }
                    }
                }
                return board;
            }
        }
        return new Board(8,8);

    }
}
