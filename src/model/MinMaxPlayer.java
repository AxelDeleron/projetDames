/* Name: ComputerPlayer
 * Author: Devon McGrath
 * Description: This class represents a computer player which can update the
 * game state without user interaction.
 */

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logic.MoveGenerator;

/**
 * The {@code ComputerPlayer} class represents a computer player and updates
 * the board based on a model.
 */
public class MinMaxPlayer extends ComputerPlayer {

	protected boolean player;
	
	protected StateSet transpositionTableMax, transpositionTableMin;
	
	protected int counter;
	
	public MinMaxPlayer(boolean joueur) {
		this.player = joueur;
		this.transpositionTableMax = new StateSet();
		this.transpositionTableMin = new StateSet();
		this.counter = 0;
	}
	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void updateGame(Game game) {
		
		// Nothing to do
		if (game == null || game.isGameOver()) {
			return;
		}
		
		System.out.println("Update");

		//Getting the next move done by the MinMaxPlayer
		Move m = minimaxDecision(game);
		
		//Applying the move chosen by the MinMaxPlayer
		game.move(m.getStartIndex(), m.getEndIndex());
		
		System.out.println("Chosen move");
		System.out.println(m);
		
		//Creating a new transposition table at each new turn
		System.out.println(transpositionTableMax.size());
		System.out.println(transpositionTableMin.size());
		this.transpositionTableMax = new StateSet();
		this.transpositionTableMin = new StateSet();
			
		//TODO
	}
	
	public int maxValue(Game game) {
		//We don't go too deep to prevent from stackoverflow
		counter++;
		System.out.println(counter);
		if (counter > 1000) {return game.goodHeuristic(player);}
		
		int v = -1000;
		
		if(game.isGameOver()) {return game.goodHeuristic(player);}
		
		//For each move possible we check what the other player can do recursively
		for(Move m : getMoves(game))
		{
			Game tempGame = game.copy();
			tempGame.move(m.getStartIndex(), m.getEndIndex());
			
			//We check if we already encountered this state
			if(transpositionTableMax.getValue(tempGame) != null){return v;}
			
			//We check if the same player can play multiple times because of skips
			if(!tempGame.isP2Turn())
			{
				v = Math.min(v, maxValue(tempGame)); //If skips are available
			}
			else 
			{
				v = Math.max(v, minValue(tempGame)); //If skips aren't available
			}
			
		}
		transpositionTableMax.add(game, v);
		return v;
	}
	
	public int minValue(Game game) {
		counter++;
		System.out.println(counter);
		if (counter > 1000) {return game.goodHeuristic(player);}
		int v = 1000;
		if(game.isGameOver()) {return game.goodHeuristic(player);}
		for(Move m : getMoves(game))
		{
			Game tempGame = game.copy();
			tempGame.move(m.getStartIndex(), m.getEndIndex());
			
			//We check if we already encountered this state
			if(transpositionTableMin.getValue(tempGame) != null){return v;}
			
			if(tempGame.isP2Turn())
			{
				v = Math.max(v, minValue(tempGame));
			}
			else
			{
				v = Math.min(v, maxValue(tempGame));
			}
			
		}
		transpositionTableMin.add(game, v);
		return v;
	}
	
	public Move minimaxDecision(Game game) {
		//We initiate the first value to which we'll compare the rest
		Game tempGame = game.copy();
		Move m = getMoves(game).get(0);
		tempGame.move(m.getStartIndex(), m.getEndIndex());
		int v = minValue(tempGame);
		
		//We check each values from the possible moves and choose the best one for the MIN player
		for(Move mTemp :getMoves(game))
		{
			counter = 0;
			tempGame = game.copy();
			tempGame.move(mTemp.getStartIndex(), mTemp.getEndIndex());
			int vTemp = minValue(tempGame);
			if(vTemp > v)
			{
				m = mTemp;
				v = vTemp;
			}
		}
		return m;
	}
	
	/**
	 * Gets all the available moves and skips for the current player.
	 * 
	 * @param game	the current game state.
	 * @return a list of valid moves that the player can make.
	 */
	protected List<Move> getMoves(Game game) {
		
		// The next move needs to be a skip
		if (game.getSkipIndex() >= 0) {
			
			List<Move> moves = new ArrayList<>();
			List<Point> skips = MoveGenerator.getSkips(game.getBoard(),
					game.getSkipIndex());
			for (Point end : skips) {
				Game copy = game.copy();
				int startIndex = game.getSkipIndex(), endIndex = Board.toIndex(end);
				copy.move(startIndex,endIndex);
				moves.add(new Move(startIndex, endIndex, copy.goodHeuristic(!copy.isP2Turn())));
			}
			Collections.sort(moves);
			return moves;
		}
		
		// Get the checkers
		List<Point> checkers = new ArrayList<>();
		Board b = game.getBoard();
		if (game.isP2Turn()) {
			checkers.addAll(b.find(Board.BLACK_CHECKER));
			checkers.addAll(b.find(Board.BLACK_KING));
		} else {
			checkers.addAll(b.find(Board.WHITE_CHECKER));
			checkers.addAll(b.find(Board.WHITE_KING));
		}
		
		// Determine if there are any skips
		List<Move> moves = new ArrayList<>();
		for (Point checker : checkers) {
			int index = Board.toIndex(checker);
			List<Point> skips = MoveGenerator.getSkips(b, index);
			for (Point end : skips) {
				Game copy = game.copy();
				int endIndex = Board.toIndex(end);
				copy.move(index,endIndex);
				Move m = new Move(index, endIndex, copy.goodHeuristic(!copy.isP2Turn()));
				moves.add(m);
			}
		}
		
		// If there are no skips, add the regular moves
		if (moves.isEmpty()) {
			for (Point checker : checkers) {
				int index = Board.toIndex(checker);
				List<Point> movesEnds = MoveGenerator.getMoves(b, index);
				for (Point end : movesEnds) {
					Game copy = game.copy();
					int endIndex = Board.toIndex(end);
					copy.move(index,endIndex);
					moves.add(new Move(index, endIndex, copy.goodHeuristic(!copy.isP2Turn())));
				}
			}
		}
		Collections.sort(moves);
		return moves;
	}
}
