/* Name: ComputerPlayer
 * Author: Devon McGrath
 * Description: This class represents a computer player which can update the
 * game state without user interaction.
 */

package model;


/**
 * The {@code ComputerPlayer} class represents a computer player and updates
 * the board based on a model.
 */
public class AlphaBetaPlayer extends MinMaxPlayer {

	protected int alpha;
	protected int beta;
	
	public AlphaBetaPlayer(boolean joueur) {
		super(joueur);
		alpha = 0;
		beta = 0;
	}

	@Override
	protected int setup_Value(Game tempGame) { // == alphaBetaSearch
		return maxValue(tempGame);
	}
	
	@Override
	protected int affect_v_Value(String minmax, int v, Game tempGame) {
		if (minmax == "max") {
			int vTemp = Math.max(v, minValue(tempGame));
			if (vTemp >= beta) return vTemp;
			else alpha = Math.max(alpha, v);
			return vTemp;
		}
		else {
			int vTemp = Math.min(v, maxValue(tempGame));
			if (vTemp <= alpha) return vTemp;
			else beta = Math.min(beta, v);
			return vTemp;
		}
	}
}
