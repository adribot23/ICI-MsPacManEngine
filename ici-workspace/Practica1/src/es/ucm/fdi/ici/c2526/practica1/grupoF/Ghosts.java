package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;
import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class Ghosts extends GhostController {
	EnumMap<GHOST, MOVE> ghostMove = new EnumMap<GHOST, MOVE>(GHOST.class);
	private static final int POWER_PILL_DISTANCE = 20;
	GHOST[] ghosts = GHOST.values();

	EnumMap<GHOST, GhostState> strategies = new EnumMap<>(GHOST.class);

	public Ghosts() {
		strategies.put(GHOST.BLINKY, new Blinky());
		strategies.put(GHOST.PINKY, new Pinky());
		strategies.put(GHOST.INKY, new Inky());
		strategies.put(GHOST.SUE, new Sue());
	}
	/*
	 * 1º Cuando son comestibles, si les queda menos de 5 segundos para que vuelvan
	 * a la normalidad van hacia el pacman, en caso contrario huyen a sus esquinas
	 * mas cercanas excluyendo en la que esta el pacman (scatter), si no tienen
	 * asignada una esquina huyen directamente del pacman.
	 * 
	 * 2º Cuando no son cometibles se comprueba si pacman esta cerca de una
	 * powerPill, en caso afirmativo los fantasmas huyen del pacman, en caso
	 * contrario los fantasmas eligen entre ir al siguiente cruce al que va a llegar
	 * Pac-Man siguiendo su dirección actual (nextJunction) o ir directamente hacia
	 * el pacman
	 */

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		int[] ghostToTarget = scatter(game);

		GhostState g;
		for (int i = 0; i < 4; i++) {
			GHOST ghostType = ghosts[i];
			if (game.doesGhostRequireAction(ghostType)) {
				// COMESTIBLE

				MOVE move;
				if (game.isGhostEdible(ghostType)) {
					// Si le quedan menos de 5 seg en estado comestible, empieza a perseguir al
					// pacman
					if (game.getGhostEdibleTime(ghostType) < 5) {
						move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType),
								posPacman, game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);

						// Si le quedan mas de 5 seg en estado comestible, se comprueba si tiene esquina
						// asignada
					} else {
						// Si tiene esquina asignada, va hacia ella
						if (ghostToTarget[i] != -1) {
							GameView.addLines(game, Color.WHITE, game.getGhostCurrentNodeIndex(ghostType),
									ghostToTarget[i]);
							move = game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType),
									ghostToTarget[i], game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);

						} else {
							// Si no tiene esquina asignada, huye del pacman
							move = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghostType),
									posPacman, game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);
						}
					}
				} else {
					// NO COMESTIBLE
					int[] powerPills = game.getActivePowerPillsIndices();
					boolean pacmanNearPP = false;
					for (int pp : powerPills) {
						int dist = game.getShortestPathDistance(posPacman, pp);
						// Comprueba si el pacman esta a punto de comerse una pp
						if (dist != -1 && dist < POWER_PILL_DISTANCE) {
							pacmanNearPP = true;
							break;
						}
					}

					// Si el fantasma no es comestible y el pacman esta cerca de una pp, huye del
					// pacman
					if (pacmanNearPP)
						move = game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghostType),
								posPacman, game.getGhostLastMoveMade(ghostType), Constants.DM.PATH);

					g = strategies.get(ghostType);
					move = g.action(game, ghostType, posPacman);
				}

				ghostMove.put(ghostType, move);
			}
		}
		return ghostMove;
	}

	// Funcion para dispersar a los fantasmas en modo comestible
	int[] scatter(Game game) {
		int posPacMan = game.getPacmanCurrentNodeIndex();
		int[] allPowerPills = game.getPowerPillIndices();

		// Calcular la power pill mas lejana a Pac-Man
		int farthestPP = -1;
		int nearestPacmanPP = -1;
		int maxDist = -1;
		int minDist = Integer.MAX_VALUE;
		for (int pp : allPowerPills) {
			int dist = game.getShortestPathDistance(posPacMan, pp);
			if (dist > maxDist) {
				maxDist = dist;
				farthestPP = pp;
			} else if (dist < minDist) {
				minDist = dist;
				nearestPacmanPP = pp;
			}

		}
		// Obtener las otras dos power pills
		int[] otherPP = new int[2];
		int cont = 0;
		for (int pp : allPowerPills) {
			if (pp != farthestPP && pp != nearestPacmanPP && cont < 2) {
				otherPP[cont++] = pp;
			}
		}

		// Asignar fantasmas a esquinas segn cercania
		boolean[] assigned = new boolean[4];
		int[] ghostToTarget = new int[4]; // Indice de power pill objetivo para cada fantasma (-1 si no asignado)
		for (int i = 0; i < 4; i++)
			ghostToTarget[i] = -1;

		// Contar cuantos fantasmas comestibles hay
		int edibleCount = 0;
		for (int i = 0; i < 4; i++) {
			if (game.isGhostEdible(ghosts[i]))
				edibleCount++;
		}

		// Solo si hay al menos dos fantasmas comestibles, aplica la estrategia de
		// esquinas
		if (edibleCount > 1) {
			// Guardamos las distancias de cada fantasma a la pp mas lejana
			int[] distToFarthestPP = new int[4];
			for (int i = 0; i < 4; i++) {
				distToFarthestPP[i] = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]),
						farthestPP);
			}
			// Buscamos los 2 que mas cerca esten de ella
			for (int n = 0; n < 2; n++) {
				int ghostId = -1;
				minDist = Integer.MAX_VALUE;
				for (int i = 0; i < 4; i++) {
					if (!assigned[i] && game.isGhostEdible(ghosts[i]) && distToFarthestPP[i] < minDist) {
						minDist = distToFarthestPP[i];
						ghostId = i;
					}
				}
				// Se asignan los 2 fantasmas que mas cerca estan de la pp mas lejana
				if (ghostId != -1) {
					assigned[ghostId] = true;
					ghostToTarget[ghostId] = farthestPP;
				}
			}
			// Buscamos los 2 fantasmas que esten mas cerca de las esquinas que faltan
			for (int j = 0; j < 2; j++) {
				int ghostId = -1;
				minDist = Integer.MAX_VALUE;
				for (int i = 0; i < 4; i++) {
					if (!assigned[i] && game.isGhostEdible(ghosts[i])) {
						int dist = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), otherPP[j]);
						if (dist < minDist) {
							minDist = dist;
							ghostId = i;
						}
					}
				}
				if (ghostId != -1) {
					assigned[ghostId] = true;
					ghostToTarget[ghostId] = otherPP[j];
				}
			}
		}
		return ghostToTarget;
	}

}