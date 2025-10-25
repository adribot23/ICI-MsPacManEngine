package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChasePacmanNextJunction implements RulesAction{

	GHOST ghost;
	
	public ChasePacmanNextJunction(GHOST ghost) {
		this.ghost=ghost;
	}
	
	
	@Override
	public MOVE execute(Game game) {
		 if (game.doesGhostRequireAction(ghost))        //if it requires an action
	        {
			 
	                return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
	                		nextPacmanJunction(game), game.getGhostLastMoveMade(ghost), DM.PATH);
	        }
	        return MOVE.NEUTRAL;
	}

	@Override
	public void parseFact(Fact actionFact) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getActionId() {
		// TODO Auto-generated method stub
		return ghost + "chasesJunction";
	}
	
	private int nextPacmanJunction(Game game) {
		int start = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		Set<Integer> visited = new HashSet<>();
		Queue<Integer> queue = new LinkedList<>();

		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty()) {
			int current = queue.poll();

			if (current != start && game.isJunction(current)) {
				return current;
			}

			for (MOVE move : MOVE.values()) {

				if (move != MOVE.NEUTRAL && !(current == start && move == lastMove.opposite())) {

					int next = game.getNeighbour(current, move);
					if (next != -1 && !visited.contains(next)) {
						visited.add(next);
						queue.add(next);
					}
				}
			}
		}

		return -1;
	}
}
