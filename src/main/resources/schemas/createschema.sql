/* Need to switch off FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;

CREATE TABLE IF NOT EXISTS Game (
  gameID int NOT NULL UNIQUE AUTO_INCREMENT,

  name varchar(255),

  phase tinyint,
  step tinyint,
  currentPlayer tinyint NULL,
  boardName varchar(30),

  PRIMARY KEY (gameID),
  FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
  On delete cascade
);;

CREATE TABLE IF NOT EXISTS Player (
  gameID int NOT NULL,
  playerID tinyint NOT NULL,

  name varchar(255),
  colour varchar(31),

  positionX int,
  positionY int,
  heading tinyint,

  PRIMARY KEY (gameID, playerID),
  FOREIGN KEY (gameID) REFERENCES Game(gameID)
  ON DELETE CASCADE
);;

CREATE TABLE IF NOT EXISTS CommandCard(
    gameID INT NOT NULL,
    playerID tinyint NOT NULL,
    command varchar(255),
    fieldType varchar(15),
    cardIndex tinyint,
    FOREIGN KEY (gameID) REFERENCES Game(gameID),
    FOREIGN KEY (gameID, playerID) REFERENCES Player(gameID, playerID),
    PRIMARY KEY (gameID, playerID, command, fieldType, cardIndex)
);;

SET FOREIGN_KEY_CHECKS = 1;;
