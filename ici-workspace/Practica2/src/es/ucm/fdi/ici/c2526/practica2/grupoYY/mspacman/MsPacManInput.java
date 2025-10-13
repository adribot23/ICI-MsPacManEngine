package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.PacmanConfig;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class MsPacManInput extends Input {

	private boolean avoidPowerPills;
	private boolean powerPillEaten;
	private boolean edibleGhostInGame;
	private boolean nearNotEdibleGhost;
	private int nearestSafePill;
	private int nearestSafePP;
	
	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		powerPillEaten = game.wasPowerPillEaten();
		avoidPowerPills = avoidPowerPillZone();
		edibleGhostInGame=  edibleGhostInGame();
		nearNotEdibleGhost= nearNotEdibleGhost();
		
		nearestSafePill = getNearestSafePill();
		nearestSafePP = getNearestSafePowerPill();

	}

	public boolean avoidPowerPills() {
		return avoidPowerPills;
	}

	public boolean wasPowerPillEaten() {
		return powerPillEaten;
	}

	public boolean isEdibleGhostInGame() {
		return edibleGhostInGame;
	}
	
	public boolean isNearNotEdibleGhost() {
		return nearNotEdibleGhost;
	}
	public int getNearestSafePill() {
		return nearestSafePill;
	}

	public int getNearestSafePowerPill() {
		return nearestSafePP;
	}

	private boolean edibleGhostInGame() {
		
		for(GHOST g:GHOST.values()) 
			if(game.isGhostEdible(g)) return true;
		
		return false;
		
	}
	
	private int nearestSafePill() {
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

	private int nearestSafePowerPill() {
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

	private int nearestPill() {
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

	
	private boolean nearNotEdibleGhost() {
		int posPacman = game.getPacmanCurrentNodeIndex();
		
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost),
						game.getGhostLastMoveMade(ghost));
				if (dist <PacmanConfig.DANGER_DISTANCE) {
					return true;
				}
			}
		}
		return false;

	}
}
