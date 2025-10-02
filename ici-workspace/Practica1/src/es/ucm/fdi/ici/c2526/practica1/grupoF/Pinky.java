package es.ucm.fdi.ici.c2526.practica1.grupoF;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

public class Pinky implements GhostState {

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {

		int nearestPill = getNearestPill(game);
		
		if(nearestPill!=-1) {
		GameView.addPoints(game, Color.PINK, game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), nearestPill));
		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), nearestPill,
				game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
		}
		return MOVE.NEUTRAL;

	}

	private int getNearestPill(Game game) {
		Queue<Integer> q = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();

		int pacmanPos = game.getPacmanCurrentNodeIndex();
		q.add(pacmanPos);
		visited.add(pacmanPos);

		while (!q.isEmpty()) {
			int current = q.poll();

			if (isPillNode(game, current)) {
				return current;
			}

			for (MOVE m : MOVE.values()) {
				int next = game.getNeighbour(current, m);
				if (next != -1 && !visited.contains(next)) {
					q.add(next);
					visited.add(next);
				}
			}
		}

		return -1;
	}

	// Comprueba si un nodo corresponde a una pill o power pill disponible
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
