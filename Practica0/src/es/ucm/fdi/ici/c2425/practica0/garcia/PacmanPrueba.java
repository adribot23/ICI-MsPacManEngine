package es.ucm.fdi.ici.c2425.practica0.garcia;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

import pacman.game.Constants.MOVE;

public class PacmanPrueba extends PacmanController {
	@Override
		
	public MOVE getMove(Game game, long timeDue) {
	    Queue<Integer> q = new LinkedList<>();
	    Set<Integer> visited = new HashSet<>();
	    
	    int pacmanPos = game.getPacmanCurrentNodeIndex();
	    q.add(pacmanPos);
	    visited.add(pacmanPos);

	    while (!q.isEmpty()) {
	        int current = q.poll();

	        // ¿Es una pill o power pill y está disponible?
	        if (isPillNode(game, current)) {
	            GameView.addLines(game, Color.YELLOW, pacmanPos, current);

	            return game.getApproximateNextMoveTowardsTarget(
	                pacmanPos, current, game.getPacmanLastMoveMade(), Constants.DM.PATH);
	        }

	        // Expando vecinos (sin MOVE.NEUTRAL)
	        for (MOVE m : MOVE.values()) {
	            if (m == MOVE.NEUTRAL) continue;
	            int next = game.getNeighbour(current, m);
	            if (next != -1 && !visited.contains(next)) {
	                q.add(next);
	                visited.add(next);
	            }
	        }
	    }

	    return MOVE.NEUTRAL;
	}

	/** Comprueba si un nodo corresponde a una pill o power pill disponible */
	private boolean isPillNode(Game game, int node) {
	    for (int p : game.getActivePillsIndices()) {
	        if (p == node && game.isPillStillAvailable(game.getPillIndex(p))) {
	            return true;
	        }
	    }
	    for (int pp : game.getActivePowerPillsIndices()) {
	        if (pp == node && game.isPowerPillStillAvailable(game.getPowerPillIndex(pp))) {
	            return true;
	        }
	    }
	    return false;
	}
}
