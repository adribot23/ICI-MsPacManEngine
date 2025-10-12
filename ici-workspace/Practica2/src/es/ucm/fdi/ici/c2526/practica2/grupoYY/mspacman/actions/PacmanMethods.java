package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PacmanMethods {

	
	public  int getNearestSafePill(Game game) {
		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices();
		int posPacman= game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		int safestPill = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int pill : pills) {
			int[] path = game.getShortestPath(posPacman, pill, lastMove);
			boolean safe = true;

			if (isPathSafeFromGhosts(game, path)) {
				if (avoidPowerPillZone(game))
					safe = isPathSafeFromPowerPills(game, path, powerPills);

				if (safe && path.length < minDistance) {
					safestPill = pill;
					minDistance = path.length;
				}
			}
		}
		return safestPill;
	}


	public int getNearestSafePowerPill(Game game) {
		if (avoidPowerPillZone(game))
			return -1;

		int[] powerPills = game.getActivePowerPillsIndices();
		int safestPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;
		int posPacman= game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		
		for (int pp : powerPills) {
			int[] path = game.getShortestPath(posPacman, pp, lastMove);
			if (isPathSafeFromGhosts(game, path)) {
				if (path.length < minDistance) {
					minDistance = path.length;
					safestPowerPill = pp;
				}
			}
		}

		return safestPowerPill;
	}

	
	public int getNearestPill(Game game) {
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

	private boolean isPillNode(Game game, int node) {
		for (int p : game.getActivePillsIndices())
			if (p == node)
				return true;
		for (int pp : game.getActivePowerPillsIndices())
			if (pp == node)
				return true;
		return false;
	}

	private boolean isPathSafeFromGhosts(Game game, int[] path) {
		for (GHOST ghost : GHOST.values()) {
			int ghostPos = game.getGhostCurrentNodeIndex(ghost);
			if (game.getGhostLairTime(ghost) <= 0 && !game.isGhostEdible(ghost)) {
				for (int node : path) {
					int dist = game.getShortestPathDistance(ghostPos, node);
					if (dist != -1 && dist < PacmanConfig.DANGER_DISTANCE) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean isPathSafeFromPowerPills(Game game, int[] path, int[] powerPills) {
		for (int pp : powerPills) {
			for (int node : path) {
				int distPP = game.getShortestPathDistance(node, pp);
				if (distPP != -1 && distPP < PacmanConfig.MIN_POWER_PILL_DISTANCE) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean avoidPowerPillZone(Game game) {
		int nonEdibleOut = 0;
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				nonEdibleOut++;
			}
		}
		return nonEdibleOut < 3;
	}
}
