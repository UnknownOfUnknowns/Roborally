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
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
class Repository implements IRepository {

	private static final String GAME_GAMEID = "gameID";

	private static final String GAME_NAME = "name";

	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";
	private static final String GAME_BOARD_NAME = "boardName";

	private static final String PLAYER_PLAYERID = "playerID";

	private static final String PLAYER_NAME = "name";

	private static final String PLAYER_COLOUR = "colour";

	private static final String PLAYER_GAMEID = "gameID";

	private static final String PLAYER_POSITION_X = "positionX";

	private static final String PLAYER_POSITION_Y = "positionY";

	private static final String PLAYER_HEADING = "heading";

	public static final String PLAYER_CHECKPOINTS = "checkpoints";

	public static final String COMMAND_COMMAND = "command";

	public static final String COMMAND_FIELD_TYPE = "fieldType";

	public static final String COMMAND_CARD_INDEX = "cardIndex";

	public static final String[] COMMAND_CARD_PILES =  new String[]{"HAND", "PROGRAMMING", "STACK", "DISCARD"};

	final public static int NO_HAND_CARDS = 8;

	final public static int NO_PROGRAMMING_CARDS = 5;

	private Connector connector;

	Repository(Connector connector){
		this.connector = connector;
	}

	@Override
	public boolean createGameInDB(Board game) {
		if (game.getGameId() == null) {
			Connection connection = connector.getConnection();
			try {
				connection.setAutoCommit(false);

				PreparedStatement ps = getInsertGameStatementRGK();
				// TODO: the name should eventually set by the user
				//       for the game and should be then used
				//       game.getName();
				ps.setString(1, "Date: " +  new Date()); // instead of name
				ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
				ps.setInt(3, game.getPhase().ordinal());
				ps.setInt(4, game.getStep());
				ps.setString(5, game.boardName);


				int affectedRows = ps.executeUpdate();
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (affectedRows == 1 && generatedKeys.next()) {
					game.setGameId(generatedKeys.getInt(1));
				}
				generatedKeys.close();

				// Enable foreign key constraint check again:
				// statement.execute("SET foreign_key_checks = 1");
				// statement.close();

				createPlayersInDB(game);
				/* TOODO this method needs to be implemented first
				createCardFieldsInDB(game);
				 */

				createCommandCardsInDB(game);

				// since current player is a foreign key, it can oly be
				// inserted after the players are created, since MySQL does
				// not have a per transaction validation, but validates on
				// a per row basis.
				ps = getSelectGameStatementU();
				ps.setInt(1, game.getGameId());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
					rs.updateRow();
				} else {
					connection.rollback();
				}
				rs.close();

				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Some DB error");

				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
		}
		return false;
	}

	@Override
	public boolean updateGameInDB(Board game) {
		assert game.getGameId() != null;

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateString(GAME_BOARD_NAME, game.boardName);
				rs.updateRow();
			} else {
				// TODO error handling
			}
			rs.close();

			updatePlayersInDB(game);
			/* TOODO this method needs to be implemented first
			updateCardFieldsInDB(game);
			*/
			updateCommandCardsInDB(game);
            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");

			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO error handling
				e1.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public Board loadGameFromDB(int id) {
		Board game = LoadBoard.loadBoard(null);;
		try {
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
				String boardName = rs.getString(GAME_BOARD_NAME);
				if(boardName != null) {
					game = LoadBoard.loadBoard(boardName);
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
				game.setStep(rs.getInt(GAME_STEP));
			} else {
				return new Board(8,8);
			}
			rs.close();

			game.setGameId(id);
			loadPlayersFromDB(game);
			loadCommandCardsFromDB(game);

			if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				return null;
			}


			return game;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}

	@Override
	public List<GameInDB> getGames() {
		// TODO when there many games in the DB, fetching all available games
		//      from the DB is a bit extreme; eventually there should a
		//      methods that can filter the returned games in order to
		//      reduce the number of the returned games.
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO proper error handling
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * @author s211638
	 * */
	private void createPlayersInDB(Board game) throws SQLException {
		// TODO code should be more defensive
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameId());
			rs.updateInt(PLAYER_PLAYERID, i);
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKPOINTS, player.getCheckpointsReached());
			rs.insertRow();
		}

		rs.close();
	}
	/**
	 * @author s211638
	 * */
	private int getPileSize(String pileName, Player player){
		switch (pileName){
			case "HAND" -> {return NO_HAND_CARDS;}
			case "PROGRAMMING" -> {return NO_PROGRAMMING_CARDS;}
			case "STACK" -> {return player.getProgrammingPile().size();}
			case "DISCARD" -> {return player.getDiscardPile().size();}
			default -> {return 0;}
		}
	}

	/**
	 * @author s211638
	 * */
	private CommandCard getCommandCard(String pileName, Player player, int index){
		try {
			switch (pileName) {
				case "HAND" -> {
					return player.getCardField(index).getCard();
				}
				case "PROGRAMMING" -> {
					return player.getProgramField(index).getCard();
				}
				case "STACK" -> {
					return player.getProgrammingPile().get(index);
				}
				case "DISCARD" -> {
					return player.getDiscardPile().get(index);
				}
				default -> {
					throw new IllegalArgumentException();
				}
			}
		} catch (NullPointerException e){
			return null;
		}
	}

	/**
	 * @author s215722
	 * @author s215705
	 * */
	private void createCommandCardsInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectCommandCardsStatementOrderedByIndex();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			for(String pile : COMMAND_CARD_PILES){
				for(int j = 0; j < getPileSize(pile, player); j++){
					rs.moveToInsertRow();
					rs.updateInt(PLAYER_GAMEID, game.getGameId());
					rs.updateInt(PLAYER_PLAYERID, i);
					String commandName = "null";
					CommandCard card = getCommandCard(pile, player, j);
					if(card != null){
						commandName = card.command.displayName;
					}
					rs.updateString(COMMAND_COMMAND, commandName);
					rs.updateString(COMMAND_FIELD_TYPE, pile);
					rs.updateInt(COMMAND_CARD_INDEX, j);
					rs.insertRow();
				}
			}

		}
		rs.close();
	}

	/**
	 * @author s215722
	 * @author s215705
	 * */
	private void loadPlayersFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				// TODO this should be more defensive
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name);
				game.addPlayer(player);

				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getSpace(x,y));
				int heading = rs.getInt(PLAYER_HEADING);
				player.setHeading(Heading.values()[heading]);

				player.setCheckpointsReached(rs.getInt(PLAYER_CHECKPOINTS));

			} else {
				// TODO error handling
				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}
		rs.close();
	}

	/**
	 * @author s211638
	 * Sets the commandCards into the appropriate pile. The programming and discard pile do not make use of the index
	 * since the size of these is zero from the beginning. The order is still maintained in these if the functions is called
	 * in the order the cards are in. It should be noted that the order of the mentioned piles is irrelevant since the programming
	 * pile is shuffled and not visible for the user, while the discard is shuffled into the programming pile.
	 * */
	private void setCommandCard(String pileName, Player player, int index, Command c) {
		CommandCard card = new CommandCard(c);
		switch (pileName) {
			case "HAND" -> {
				player.getCardField(index).setCard(card);
			}
			case "PROGRAMMING" -> {
				player.getProgramField(index).setCard(card);
			}
			case "STACK" -> {
				player.getProgrammingPile().add(card);
			}
			case "DISCARD" -> {
				player.getDiscardPile().add(card);
			}
			default -> {
				throw new IllegalArgumentException();
			}
		}
	}
	/**
	 * @author s215722
	 * @author s215705
	 * Updates the commandCards by first deleting them. This is easier since the discard pile and the drawing pile is
	 * of variable length
	 * */
	private void loadCommandCardsFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectCommandCardsStatementOrderedByIndex();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();

		while(rs.next()){
			int playerID = rs.getInt(PLAYER_PLAYERID);
			String commandName = rs.getString(COMMAND_COMMAND);
			String fieldType = rs.getString(COMMAND_FIELD_TYPE);
			int cardIndex = rs.getInt(COMMAND_CARD_INDEX);

			Player player = game.getPlayer(playerID);
			Command command = null;
			Command[] commands = Command.values();
			for(Command com : commands){
				if(com.displayName.equals(commandName)){
					command = com;
					break;
				}
			}

			if(command == null)
				continue;

			setCommandCard(fieldType, player, cardIndex, command);

		}
		rs.close();
	}

	/**
	 * @author s211638
	 * */
	private void updatePlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			// TODO should be more defensive
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKPOINTS, player.getCheckpointsReached());
			// TODO error handling
			// TODO take care of case when number of players changes, etc
			rs.updateRow();
		}
		rs.close();

		// TODO error handling/consistency check: check whether all players were updated
	}
	/**
	 * @author s211638
	 * */
	private void updateCommandCardsInDB(Board game) throws SQLException {
		//Since some command card piles are of dynamic length we just delete the piles in DB,
		//and creates them again, this is easier than removing some cards from the database and updating
		//others
		PreparedStatement ps = deleteCommandCardsStatement();
		ps.setInt(1, game.getGameId());

		ps.execute();
		createCommandCardsInDB(game);
	}

	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(name, currentPlayer, phase, step, boardName) VALUES (?, ?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE gameID = ?";

	private PreparedStatement select_game_stmt = null;

	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
					    ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}

	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}

	private static final String SQL_SELECT_COMMAND_CARDS = "SELECT * FROM CommandCard WHERE gameID = ? ORDER BY cardIndex";

	private PreparedStatement select_command_cards_statement = null;
	/**
	 * @author s215705
	 * This method returns a statement that can be executed to find the commandcards of a game
	 * @return PreparedStatement
	 * */
	private PreparedStatement getSelectCommandCardsStatementOrderedByIndex(){
		if(select_command_cards_statement == null){
			Connection connection = connector.getConnection();
			try {
				select_command_cards_statement = connection.prepareStatement(
						SQL_SELECT_COMMAND_CARDS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				//TODO add error handling
				e.printStackTrace();
			}
		}
		return select_command_cards_statement;
	}


	public static final String SQL_DELETE_COMMAND_CARDS = "DELETE FROM CommandCard WHERE gameID = ?";
	private PreparedStatement delete_command_cards_statement = null;

	private PreparedStatement deleteCommandCardsStatement(){
		if(delete_command_cards_statement == null){
			Connection connection = connector.getConnection();
			try {
				delete_command_cards_statement = connection.prepareStatement(
						SQL_DELETE_COMMAND_CARDS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				//TODO add error handling
				e.printStackTrace();
			}
		}
		return delete_command_cards_statement;
	}

	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

	private PreparedStatement select_players_asc_stmt = null;

	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}

	private static final String SQL_SELECT_GAMES =
			"SELECT gameID, name FROM Game";

	private PreparedStatement select_games_stmt = null;

	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}



}
