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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.boardElements.Gear;
import dk.dtu.compute.se.pisd.roborally.view.components.Arrow;
import dk.dtu.compute.se.pisd.roborally.view.components.ConveyerBelt;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 50; // 60; // 75;
    final public static int SPACE_WIDTH = 50;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        renderWalls();
        if(space.getBoardElement() != null)
            renderBoardElement();

        if (subject == this.space) {
            updatePlayer();
        }

    }

    private void renderWalls(){
        if(space.getWalls() != null) {
            for (Heading heading : space.getWalls()) {
                Pane pane = new Pane();
                Rectangle rectangle =
                        new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
                rectangle.setFill(Color.TRANSPARENT);
                pane.getChildren().add(rectangle);
                double x1 = 0,y1=0,x2=0,y2=0;

                switch (heading){
                    case SOUTH -> {
                        x1=2;
                        y1=SPACE_HEIGHT-2;
                        x2=SPACE_WIDTH-2;
                        y2=SPACE_HEIGHT-2;
                    }
                    case WEST -> {
                        x1=2;
                        y1=2;
                        x2=2;
                        y2=SPACE_WIDTH-2;
                    }
                    case EAST -> {
                        x1=SPACE_WIDTH-2;
                        y1=2;
                        x2=SPACE_WIDTH-2;
                        y2=SPACE_HEIGHT-2;
                    }
                    case NORTH -> {
                        x1=2;
                        y1=2;
                        x2=SPACE_WIDTH-2;
                        y2=2;
                    }
                }

                Line line =
                        new Line(x1,y1,x2,y2);
                line.setStroke(Color.RED);
                line.setStrokeWidth(5);
                pane.getChildren().add(line);
                this.getChildren().add(pane);
            }
        }
    }

    private void renderBoardElement(){
        Pane pane = new Pane();
        Rectangle rectangle =
                new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);
        BoardElement element = space.getBoardElement();

        if(element.getClass() == Gear.class){
            Gear concreteElement = (Gear) element;
            Circle circle = new Circle(10);
            if(concreteElement.getTurnDirection().equals(TurnDirection.LEFT))
                circle.setFill(Color.DARKRED);
            else
                circle.setFill(Paint.valueOf("green"));
            circle.setCenterX(SPACE_WIDTH/2.0);
            circle.setCenterY(SPACE_HEIGHT/2.0);
            pane.getChildren().add(circle);
            this.getChildren().add(pane);
        }else if(element.getClass() == dk.dtu.compute.se.pisd.roborally.model.boardElements.ConveyerBelt.class){
            dk.dtu.compute.se.pisd.roborally.model.boardElements.ConveyerBelt concreteElement =
                    (dk.dtu.compute.se.pisd.roborally.model.boardElements.ConveyerBelt) element;
            ConveyerBelt belt = new ConveyerBelt(SPACE_WIDTH, SPACE_HEIGHT);
            switch (concreteElement.getDirection()){
                case SOUTH -> belt.setRotate(90);
                case WEST -> belt.setRotate(180);
                case NORTH -> setRotate(-90);
            }
            getChildren().add(belt);
        }
    }
}
