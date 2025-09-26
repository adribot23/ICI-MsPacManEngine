package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

public class Ghosts extends GhostController {

	private static final int POWER_PILL_DISTANCE = 10;
    GHOST[] ghosts = GHOST.values();
	private Random rnd = new Random();
	
	// 1º Cuando estan comestibles huyen a sus esquinas mas cercanas excluyendo la que esta el pacman (scatter)
	// 2º Cuando no estan comestibles hacer nextJunction(Game game) de 2 fanstasmas y los otros 2 normal (pacmanOrJunction)

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> ghostMove = new EnumMap<GHOST, MOVE>(GHOST.class);
		int posPacman = game.getPacmanCurrentNodeIndex();
		int[] ghostToTarget = scatter(game);
		
        for (int i = 0; i < 4; i++) {
            GHOST ghostType = ghosts[i];
            if (game.doesGhostRequireAction(ghostType)) {
                // COMESTIBLE
                if(game.isGhostEdible(ghostType)) {
                    // Si le quedan menos de 5 seg en estado comestible, empieza a perseguir al pacman
                    if(game.getGhostEdibleTime(ghostType) < 5) {
                        MOVE move = game.getApproximateNextMoveTowardsTarget(
                                game.getGhostCurrentNodeIndex(ghostType),
                                posPacman,
                                game.getGhostLastMoveMade(ghostType),
                                Constants.DM.PATH);
                        ghostMove.put(ghostType, move);
                    // Si le quedan mas de 5 seg en estado comestible, se comprueba si tiene esquina asignada
                    } else {
                        // Si tiene esquina asignada, va hacia ella
                        if (ghostToTarget[i] != -1) {
                        	GameView.addLines(game,Color.CYAN, game.getGhostCurrentNodeIndex(ghostType), ghostToTarget[i]);
                            MOVE move = game.getApproximateNextMoveTowardsTarget(
                                    game.getGhostCurrentNodeIndex(ghostType),
                                    ghostToTarget[i],
                                    game.getGhostLastMoveMade(ghostType),
                                    Constants.DM.PATH);
                            ghostMove.put(ghostType, move);
                        } else {
                            // Si no tiene esquina asignada, huye del pacman
                            MOVE move = game.getApproximateNextMoveAwayFromTarget(
                                    game.getGhostCurrentNodeIndex(ghostType),
                                    posPacman,
                                    game.getGhostLastMoveMade(ghostType),
                                    Constants.DM.PATH);
                            ghostMove.put(ghostType, move);
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
                    // Si el fantasma no es comestible y el pacman esta cerca de una pp, huye del pacman
                    if (pacmanNearPP) {
                        MOVE move = game.getApproximateNextMoveAwayFromTarget(
                                game.getGhostCurrentNodeIndex(ghostType),
                                posPacman,
                                game.getGhostLastMoveMade(ghostType),
                                Constants.DM.PATH);
                        ghostMove.put(ghostType, move);
                    // Si el fantasma no es comestible y el pacman no esta cerca de pp, se persigue al pacman normak
                    } else {
                        if (rnd.nextDouble() < 0.9) {
                            MOVE move = pacmanOrJunction(game, ghostType);
                            ghostMove.put(ghostType, move);
                        } else {
                            MOVE[] moves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghostType), game.getGhostLastMoveMade(ghostType));
                            if (moves.length > 0) {
                                ghostMove.put(ghostType, moves[rnd.nextInt(moves.length)]);
                            } else {
                                ghostMove.put(ghostType, MOVE.NEUTRAL);
                            }
                        }
                    }
                }
            }
        }
		return ghostMove;
	}

	int nextJunction(Game game) {
		int node = game.getPacmanCurrentNodeIndex();
	
		while(node != -1 && !game.isJunction(node)) {
			node = game.getNeighbour(node, game.getPacmanLastMoveMade());
		
		}
		return node;
	}
	
	// Funcion para saber que fantasmas van a por el pacman y cuales a por el siguiente cruce
	MOVE pacmanOrJunction(Game game, GHOST ghost) {
    	// MIRAR SI RENTA USAR UNA FORMA DISTINTA DE IR PARA CADA FANTASMA
		Constants.DM[] routes = {Constants.DM.PATH, Constants.DM.MANHATTAN, Constants.DM.EUCLID, Constants.DM.PATH};
        int ghostId = ghost.ordinal();
        Constants.DM chaseMethod = routes[ghostId % routes.length];
		
		int posPacman = game.getPacmanCurrentNodeIndex();
		int nextJunction = nextJunction(game);
		
		if(nextJunction != -1) {
			int junctionPath = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghost), nextJunction);
			int pacmanPath = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghost), posPacman);
			int nearestPos = (junctionPath <= pacmanPath) ? nextJunction : posPacman;
			
			GameView.addPoints(game,Color.YELLOW,game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), nextJunction));
			return game.getApproximateNextMoveTowardsTarget(
	                game.getGhostCurrentNodeIndex(ghost),
	                nearestPos,
	                game.getGhostLastMoveMade(ghost),
	                chaseMethod);
		} else {
			GameView.addPoints(game,Color.RED,game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), posPacman));
			return game.getApproximateNextMoveTowardsTarget(
	                game.getGhostCurrentNodeIndex(ghost),
	                posPacman,
	                game.getGhostLastMoveMade(ghost),
	                chaseMethod);
		}
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
            }
            else if (dist < minDist) {
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
        for (int i = 0; i < 4; i++) ghostToTarget[i] = -1;

        // Contar cuantos fantasmas comestibles hay
        int edibleCount = 0;
        for (int i = 0; i < 4; i++) {
            if (game.isGhostEdible(ghosts[i])) edibleCount++;
        }

        // Solo si hay al menos dos fantasmas comestibles, aplica la estrategia de esquinas
        if (edibleCount > 1) {
            // Guardamos las distancias de cada fantasma a la pp mas lejana
            int[] distToFarthestPP = new int[4];
            for (int i = 0; i < 4; i++) {
                distToFarthestPP[i] = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), farthestPP);
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