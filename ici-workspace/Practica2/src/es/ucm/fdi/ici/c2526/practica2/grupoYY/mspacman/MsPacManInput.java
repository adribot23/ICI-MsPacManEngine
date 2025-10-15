package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.PacmanMethods;
import gate.util.Pair;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class MsPacManInput extends Input {

	private boolean avoidPowerPills;
	private boolean powerPillEaten;
	private boolean edibleGhostInGame;
	private boolean nearNotEdibleGhost;
	private boolean onlyOneFarEdibleGhost;
	private boolean ghostEaten;
	private boolean nearToEdibleGhost;
	private boolean ghostOutside;
	private int twoOrMoreGhostsCloseEachOther;
	private int nearestSafePill;
	private int nearestSafePP;
	private int safeZone;

	private PacmanMethods m;

	public MsPacManInput(Game game) {

		super(game);

	}

	@Override
	public void parseInput() {
		PacmanMethods m= new PacmanMethods();
		powerPillEaten = game.wasPowerPillEaten();
		ghostOutside = ghostOutside();
		avoidPowerPills = avoidPowerPillZone();
		edibleGhostInGame = edibleGhostInGame();
		nearNotEdibleGhost = nearNotEdibleGhost();
		onlyOneFarEdibleGhost = onlyOneFarEdibleGhost();
		twoOrMoreGhostsCloseEachOther = twoOrMoreGhostsCloseEachOther();
		ghostEaten = ghostEaten();
		nearToEdibleGhost = nearToEdibleGhost();
		safeZone = m.findSafeZone(game);
		nearestSafePill = findNearestSafePill();
		nearestSafePP = findNearestSafePowerPill();
		

	}

	public int getTwoOrMoreGhostsCloseEachOther() {

		return twoOrMoreGhostsCloseEachOther;
	}

	public boolean getAvoidPowerPills() {
		return avoidPowerPills;
	}

	public boolean getWasPowerPillEaten() {
		return powerPillEaten;
	}

	public boolean getIsEdibleGhostInGame() {
		return edibleGhostInGame;
	}

	public int getNearestSafePill() {
		return nearestSafePill;
	}

	public int getNearestSafePowerPill() {
		return nearestSafePP;
	}

	public int getSafeZone() {
		return safeZone;
	}

	public boolean getGhostEaten() {
		return ghostEaten;
	}
	
	public boolean getGhostOutside() {
		return ghostOutside;
	}

	public boolean nearToEdibleGhost() {
		int dist = 0, minDist = Integer.MAX_VALUE;
		nearToEdibleGhost = false;
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g));
				if (minDist > dist)
					minDist = dist;
			}
		}
		if (minDist < PacmanConfig.GHOST_IS_NEAR)
			nearToEdibleGhost = true;

		return nearToEdibleGhost;
	}

	public boolean ghostEaten() {
		ghostEaten = false;
		for (GHOST g : GHOST.values()) {
			if (game.wasGhostEaten(g)) {
				ghostEaten = true;
				return ghostEaten;
			}
		}
		return ghostEaten;
	}

	public boolean onlyOneFarEdibleGhost() {
		int dist = 0, cont = 0;
		onlyOneFarEdibleGhost = false;
		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				cont++;
				if (cont == 1)
					dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(g));
				else if (cont > 1)
					break;
			}
		}
		if (cont == 1 && dist >= PacmanConfig.GHOST_IS_FAR)
			onlyOneFarEdibleGhost = true;

		return onlyOneFarEdibleGhost;
	}

	public boolean isNearNotEdibleGhost() {
		int dist = 0, minDist = Integer.MAX_VALUE;
		nearNotEdibleGhost = false;
		for (GHOST g : GHOST.values()) {
			if (!game.isGhostEdible(g) && game.getGhostLairTime(g) == 0) {
				dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g));
				if (minDist > dist)
					minDist = dist;
			}
		}
		if (minDist < PacmanConfig.GHOST_IS_NEAR)
			nearNotEdibleGhost = true;

		return nearNotEdibleGhost;
	}

	private boolean edibleGhostInGame() {

		for (GHOST g : GHOST.values())
			if (game.isGhostEdible(g))
				return true;

		return false;
	}

	private boolean nearNotEdibleGhost() {
		int posPacman = game.getPacmanCurrentNodeIndex();

		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost),
						game.getGhostLastMoveMade(ghost));
				if (dist < PacmanConfig.DANGER_DISTANCE) {
					return true;
				}
			}
		}
		return false;

	}

	private int findNearestSafePill() {
		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices();
		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		int safestPill = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int pill : pills) {
			int[] path = game.getShortestPath(posPacman, pill, lastMove);
			boolean safe = true;

			if (isPathSafeFromGhosts(path)) {
				if (avoidPowerPillZone())
					safe = isPathSafeFromPowerPills(path, powerPills);

				if (safe && path.length < minDistance) {
					safestPill = pill;
					minDistance = path.length;
				}
			}
		}
		return safestPill;
	}

	private int findNearestSafePowerPill() {
		if (avoidPowerPillZone())
			return -1;

		int[] powerPills = game.getActivePowerPillsIndices();
		int safestPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;
		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		for (int pp : powerPills) {
			int[] path = game.getShortestPath(posPacman, pp, lastMove);
			if (isPathSafeFromGhosts(path)) {
				if (path.length < minDistance) {
					minDistance = path.length;
					safestPowerPill = pp;
				}
			}
		}

		return safestPowerPill;
	}

	private int findNearestPill() {
		Queue<Integer> q = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();

		int pacmanPos = game.getPacmanCurrentNodeIndex();
		q.add(pacmanPos);
		visited.add(pacmanPos);

		while (!q.isEmpty()) {
			int current = q.poll();

			if (isPillNode(current)) {
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

	private boolean isPillNode(int node) {
		for (int p : game.getActivePillsIndices())
			if (p == node)
				return true;
		for (int pp : game.getActivePowerPillsIndices())
			if (pp == node)
				return true;
		return false;
	}

	private boolean isPathSafeFromGhosts(int[] path) {
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

	private boolean isPathSafeFromPowerPills(int[] path, int[] powerPills) {
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

	private boolean avoidPowerPillZone() {
		int nonEdibleOut = 0;
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				nonEdibleOut++;
			}
		}
		return nonEdibleOut < 3;
	}

	private boolean ghostOutside() {

		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostLairTime(ghost) <= 0)
				return true;
		}
		return false;
	}

	private int twoOrMoreGhostsCloseEachOther() {
		int closestGhost = -1;
		int minDist = Integer.MAX_VALUE;

		for (GHOST g : GHOST.values()) {
			boolean inGroup = false;

			int nodeG = game.getGhostCurrentNodeIndex(g);
			if (game.getGhostLairTime(g) > 0 || nodeG == -1)
				continue; // Fantasma en lair o sin nodo válido

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
