package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

public class MsPacMan extends PacmanController {
	Queue<Integer> q = new LinkedList<>();
	Set<Integer> s = new HashSet<>();
	private static final int DANGER_DISTANCE = 20;
	private static final int TOO_CLOSE_DISTANCE = 50;
	private static final int MIN_POWER_PILL_DISTANCE = 10;

	/*
	 * Pacman busca al fantasma mas cercano, si esta muy cerca y no es comestible
	 * huye de el, en cambio si es combestible y le quedan mas de tres segundos en
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

		int posGhost = -1;

		// Huir del fantasma si esta muy cerca y no es comestible
		if (closestGhost != null && minDistance < TOO_CLOSE_DISTANCE && !ghostIsEdible) {
			posGhost = game.getGhostCurrentNodeIndex(closestGhost);
			// GameView.addPoints(game, Color.YELLOW, game.getShortestPath(posGhost,
			// posPacman));
			GameView.addLines(game, Color.YELLOW, posPacman, posGhost);
			return game.getApproximateNextMoveAwayFromTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
		}

		// Perseguir el fantasma si es comestible
		if (closestGhost != null && ghostIsEdible && game.getGhostEdibleTime(closestGhost) > 3) {
			posGhost = game.getGhostCurrentNodeIndex(closestGhost);
			GameView.addPoints(game, Color.YELLOW, game.getShortestPath(posGhost, posPacman));
			// GameView.addLines(game, colours[closestGhost.ordinal()], posPacman,
			// posGhost);
			return game.getApproximateNextMoveTowardsTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
		}

		// Contar fantasmas no comestibles fuera de la guarida
		int nonEdibleOut = 0;
		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				nonEdibleOut++;
			}
		}
		boolean avoidPowerPillZone = nonEdibleOut < 3;

		// Buscar power pill solo si hay 3 o mas fantasmas fuera
		int safestPowerPill = -1;
		safestPowerPill = getNearestSafePowerPill(game, posPacman, DANGER_DISTANCE, avoidPowerPillZone, lastMove);

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

	int getNearestPill(Game game) {
		q.clear();
		s.clear();

		int current, index;

		q.add(game.getPacmanCurrentNodeIndex());
		s.add(game.getPacmanCurrentNodeIndex());

		while (!q.isEmpty()) {

			current = q.poll();

			if (game.isPillStillAvailable(current) || game.isPowerPillStillAvailable(current))
				return current;

			for (MOVE m : MOVE.values()) {
				index = game.getNeighbour(current, m);
				if (index != -1 && !s.contains(index)) {
					q.add(index);
					s.add(index);
				}
			}
		}

		return -1;

	}
}
