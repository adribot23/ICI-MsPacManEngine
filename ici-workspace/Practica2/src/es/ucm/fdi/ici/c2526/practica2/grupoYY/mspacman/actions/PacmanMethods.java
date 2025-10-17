package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.PacmanConfig;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class PacmanMethods {

	public int getNearestSafePill(Game game) {
		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices();
		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		int safestPill = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int pill : pills) {
			int[] path = game.getShortestPath(posPacman, pill, lastMove);
			int[] pah2 = game.
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

		int[] powerPills = game.getActivePowerPillsIndices();
		int safestPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;
		int posPacman = game.getPacmanCurrentNodeIndex();
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
					if (dist != -1 && dist < PacmanConfig.DANGER_PILL_DISTANCE) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean isPathSafeFromGhostsForSaveZone(Game game, int[] path) {
		for (GHOST ghost : GHOST.values()) {
			int ghostPos = game.getGhostCurrentNodeIndex(ghost);
			if (game.getGhostLairTime(ghost) <= 0 && !game.isGhostEdible(ghost)) {
				for (int node : path) {
					int dist = game.getShortestPathDistance(ghostPos, node);
					if (dist != -1 && dist < PacmanConfig.SAFE_ZONE_DANGER_DISTANCE) {
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

	public boolean avoidPowerPillZone(Game game) {
		int nonEdibleOut = 0;
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				nonEdibleOut++;
			}
		}
		return nonEdibleOut < 3;
	}
	
	public int findSafeZone(Game game) {
		int[] esquinas = game.getPowerPillIndices();

		int safeZone = -1;
		int masLejano = -1;
		for (int esquina : esquinas) {
			int[] path = game.getShortestPath(game.getPacmanCurrentNodeIndex(), esquina, game.getPacmanLastMoveMade());
			if (isPathSafeFromGhostsForSaveZone(game, path) && path.length > masLejano) {
				safeZone = esquina;
				masLejano = path.length;

			}
		}

		return safeZone;
	}

	public int twoOrMoreGhostsCloseEachOther(Game game) {
		int closestGhost = -1;
		int minDist = Integer.MAX_VALUE;

		for (GHOST g : GHOST.values()) {
			boolean inGroup = false;

			int nodeG = game.getGhostCurrentNodeIndex(g);
			if (game.getGhostLairTime(g) > 0 || nodeG == -1)
				continue;

			for (GHOST h : GHOST.values()) {
				if (!g.equals(h) && game.getGhostLairTime(h) == 0) {
					int nodeH = game.getGhostCurrentNodeIndex(h);
					if (nodeH == -1)
						continue;

					int dis = game.getShortestPathDistance(nodeG, nodeH, game.getGhostLastMoveMade(g));
					if (dis < PacmanConfig.GHOST_NEAR_EACH_OTHER) {
						inGroup = true;
						break;
					}
				}
			}

			if (inGroup) {
				int pacmanDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), nodeG,
						game.getPacmanLastMoveMade());

				if (pacmanDist < minDist) {
					minDist = pacmanDist;
					closestGhost = nodeG;
				}
			}
		}

		return closestGhost;
	}

}
