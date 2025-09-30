package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class Sue implements GhostState{

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {
	
	
		int nextJunction = nextJunction(game);

		GameView.addPoints(game, Color.ORANGE,game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), nextJunction));
		
		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), nextJunction,
					game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
		
	}
	
	private int nextJunction(Game game) {
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
	            if (move == MOVE.NEUTRAL) continue;

	           
	            if (current == start && move == lastMove.opposite()) {
	                continue;
	            }

	            int next = game.getNeighbour(current, move);
	            if (next != -1 && !visited.contains(next)) {
	                visited.add(next);
	                queue.add(next);
	            }
	        }
	    }

	  
	    return -1;
	}
}
