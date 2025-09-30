package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController {
	Queue<Integer> q = new LinkedList<>();
	Set<Integer> s = new HashSet<>();
	private static final int DANGER_DISTANCE = 30;
	private static final int TOO_CLOSE_DISTANCE = 50;
	private static final int MIN_POWER_PILL_DISTANCE = 20;

	/*
	 * Pacman busca al fantasma mas cercano, si esta muy cerca y no es comestible
	 * huye de el por el camino que tenga la pill mas cercana y que no contenga
	 * fantasmas , en cambio si es combestible y le quedan mas de tres segundos en
	 * ese modo va a por el. Si no se da ninguno de estos casos el pacman va a la
	 * powerpill mas cercana solo si hay mas de dos fantasmas afuera y si no hay
	 * fantasmas en el path hacia la powerpill. En caso negativo pacman va hacia la
	 * pill mas cercana que no tenga fantasmas cerca ni powerpills si hay menos de
	 * tres fantasmas fuera. Como ultmimo caso pacman va a la pill mas cercana sin
	 * restricciones
	 */


	@Override
	public MOVE getMove(Game game, long timeDue) {

		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();
		GHOST closestGhost = null;
		boolean ghostIsEdible = false;
		int minDistance = Integer.MAX_VALUE;

		for (GHOST ghost : GHOST.values()) {
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			if (game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, ghostNode, lastMove);
				if (dist < minDistance) {
					minDistance = dist;
					closestGhost = ghost;
					ghostIsEdible = game.isGhostEdible(ghost);
				}
			}
		}

		
		int nonEdibleOut = 0;
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				nonEdibleOut++;
			}
		}
		boolean avoidPowerPillZone = nonEdibleOut < 3;
		int posGhost = -1;

		// Huir del fantasma si esta muy cerca y no es comestible
		if (closestGhost != null && minDistance < TOO_CLOSE_DISTANCE && !ghostIsEdible) {
			posGhost = game.getGhostCurrentNodeIndex(closestGhost);
			// return getEscapeMove(game, posPacman, posGhost, lastMove);
			
			/*
			int pill = getNearestPill(game);
			
			boolean safe = true;
			for (int node : game.getShortestPath(posPacman, pill, lastMove)) {
				for (GHOST g : GHOST.values()) {
					if (game.getGhostCurrentNodeIndex(g) == node) {
						safe = false;
						break;
					}
				}
				if (!safe)
					break;
			}
			*/
			 int pill = getNearestSafePill(game,posPacman, MIN_POWER_PILL_DISTANCE ,DANGER_DISTANCE,avoidPowerPillZone,lastMove);
			if (pill !=-1) {
				GameView.addLines(game, Color.YELLOW, posPacman, pill);
				game.getApproximateNextMoveTowardsTarget(posPacman, pill, lastMove, Constants.DM.PATH);
			} else {
				GameView.addLines(game, Color.RED, posPacman, posGhost);
				return game.getApproximateNextMoveAwayFromTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
			}
		}

		// Perseguir el fantasma si es comestible
		if (closestGhost != null && ghostIsEdible && game.getGhostEdibleTime(closestGhost) > 50) {
			posGhost = game.getGhostCurrentNodeIndex(closestGhost);
			GameView.addLines(game, Color.BLUE, posPacman, posGhost);
			return game.getApproximateNextMoveTowardsTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
		}

	

		// Buscar power pill solo si hay 3 o mas fantasmas fuera
		int safestPowerPill = getNearestSafePowerPill(game, posPacman, DANGER_DISTANCE, avoidPowerPillZone, lastMove);

		// Prioridad: power pill (si permitido), si no pill normal, si no neutral
		if (safestPowerPill != -1) {
			GameView.addLines(game, Color.YELLOW, posPacman, safestPowerPill);
			return game.getApproximateNextMoveTowardsTarget(posPacman, safestPowerPill, lastMove, Constants.DM.PATH);
		}

		// Buscar pill normal mas segura, evitando acercarse a power pills si es
		// necesario

		int safestPill = -1;
		safestPill = getNearestSafePill(game, posPacman, MIN_POWER_PILL_DISTANCE, DANGER_DISTANCE, avoidPowerPillZone,
				lastMove);

		if (safestPill != -1) {
			GameView.addLines(game, Color.YELLOW, posPacman, safestPill);
			return game.getApproximateNextMoveTowardsTarget(posPacman, safestPill, lastMove, Constants.DM.PATH);
		}

		// return game.getApproximateNextMoveAwayFromTarget(posPacman, posGhost,
		// lastMove, Constants.DM.PATH);
		return game.getApproximateNextMoveTowardsTarget(posPacman, getNearestPill(game), lastMove, Constants.DM.PATH);
	}

	private int getNearestSafePill(Game game, int posPacman, int minPowerPillDist, int dangerDistance,
			boolean avoidPowerPillZone, MOVE lastMove) {

		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices();

		int safestPill = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int pill : pills) {
			int[] path = game.getShortestPath(posPacman, pill, lastMove);
			boolean safe = true;

			// Si hay que evitar zonas de power pills, descartar caminos que pasen cerca de
			// ellas

			if (isPathSafeFromGhosts(game, path, dangerDistance)) {
				if (avoidPowerPillZone)
					safe = isPathSafeFromPowerPills(game, path, powerPills, minPowerPillDist);

				if (safe && path.length < minDistance) {
					safestPill = pill;
					minDistance = path.length;
				}
			}
		}
		return safestPill;
	}


	private int getNearestSafePowerPill(Game game, int posPacman, int dangerDistance, boolean avoidPowerPillZone,
			MOVE lastMove) {

		if (avoidPowerPillZone)
			return -1; // No se buscan power pills si hay que evitarlas

		int[] powerPills = game.getActivePowerPillsIndices();
		int safestPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int pp : powerPills) {
			int[] path = game.getShortestPath(posPacman, pp, lastMove);

			if (isPathSafeFromGhosts(game, path, dangerDistance)) {
				if (path.length < minDistance) {
					minDistance = path.length;
					safestPowerPill = pp;
				}
			}
		}

		return safestPowerPill;
	}

	private boolean isPathSafeFromPowerPills(Game game, int[] path, int[] powerPills, int minDist) {
		for (int pp : powerPills) {
			for (int node : path) {
				int distPP = game.getShortestPathDistance(node, pp);
				if (distPP != -1 && distPP < minDist) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isPathSafeFromGhosts(Game game, int[] path, int dangerDistance) {
		for (GHOST ghost : GHOST.values()) {
			int ghostPos = game.getGhostCurrentNodeIndex(ghost);
			if (game.getGhostLairTime(ghost) <= 0 && !game.isGhostEdible(ghost)) {
				for (int node : path) {
					int dist = game.getShortestPathDistance(ghostPos, node);
					if (dist != -1 && dist < dangerDistance) {
						return false;
					}
				}
			}
		}
		return true;
	}
	/*
	 * private int[] getInactivePowerPills(Game game) { int[] activePowerPills =
	 * game.getActivePowerPillsIndices(); int[] allPowerPills =
	 * game.getPowerPillIndices();
	 * 
	 * int[] aux = new int[allPowerPills.length]; int count = 0;
	 * 
	 * for (int p : allPowerPills) { boolean found = false; for (int a :
	 * activePowerPills) { if (a == p) { found = true; break; } } if (!found) {
	 * aux[count] = p; count++; } }
	 * 
	 * int[] result = new int[count]; for (int i = 0; i < count; i++) result[i] =
	 * aux[i]; return result; }
	 */

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

			// Expando vecinos (sin MOVE.NEUTRAL)
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
