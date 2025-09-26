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

	private static final Color[] colours = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};
	private static final int POWER_PILL_DISTANCE = 10;
	private Random rnd = new Random();

	
	// 1∫ Cuando estan comestibles huyen a sus esquinas mas cercanas excluyendo la que esta el pacman
	// 2∫ Cuando no estan comestibles hacer  nextJunction(Game game) de 2 fanstasmas y los otros 2 normal 

	
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		EnumMap<GHOST, MOVE> ghostMove = new EnumMap<GHOST, MOVE>(GHOST.class);
		int posPacMan = game.getPacmanCurrentNodeIndex();
		
		int[] allPowerPills = game.getPowerPillIndices();

        // 1. Calcular la power pill m√°s lejana a Pac-Man
        int farthestPP = -1;
        int maxDist = -1;
        for (int pp : allPowerPills) {
            int dist = game.getShortestPathDistance(posPacMan, pp);
            if (dist > maxDist) {
                maxDist = dist;
                farthestPP = pp;
              
            }
        }
        // 2. Obtener las otras dos power pills
        int[] otherPPs = new int[2];
        int idx = 0;
        for (int pp : allPowerPills) {
            if (pp != farthestPP && idx < 2) {
                otherPPs[idx++] = pp;
            }
        }

        // 3. Asignar fantasmas a esquinas segn cercan≠a
        GHOST[] ghosts = GHOST.values();
        boolean[] assigned = new boolean[4];
        int[] ghostToTarget = new int[4]; // ndice de power pill objetivo para cada fantasma (-1 si no asignado)
        for (int i = 0; i < 4; i++) ghostToTarget[i] = -1;

        // Contar cu°ntos fantasmas comestibles hay
        int edibleCount = 0;
        for (int i = 0; i < 4; i++) {
            if (game.isGhostEdible(ghosts[i])) edibleCount++;
        }

        // Solo si hay al menos dos fantasmas comestibles, aplica la estrategia de esquinas
        if (edibleCount >= 2) {
            // Asignar los dos fantasmas comestibles m°s cercanos a la esquina m√°s lejana
            int[] distToFarthestPP = new int[4];
            for (int i = 0; i < 4; i++) {
                distToFarthestPP[i] = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), farthestPP);
            }
            for (int n = 0; n < 2; n++) {
                int minIdx = -1, minDist = Integer.MAX_VALUE;
                for (int i = 0; i < 4; i++) {
                    if (!assigned[i] && game.isGhostEdible(ghosts[i]) && distToFarthestPP[i] < minDist) {
                        minDist = distToFarthestPP[i];
                        minIdx = i;
                    }
                }
                if (minIdx != -1) {
                    assigned[minIdx] = true;
                    ghostToTarget[minIdx] = farthestPP;
                }
            }
            // Asignar los otros fantasmas comestibles a las otras dos esquinas
            for (int j = 0; j < 2; j++) {
                int minIdx = -1, minDist = Integer.MAX_VALUE;
                for (int i = 0; i < 4; i++) {
                    if (!assigned[i] && game.isGhostEdible(ghosts[i])) {
                        int dist = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]), otherPPs[j]);
                        if (dist < minDist) {
                            minDist = dist;
                            minIdx = i;
                        }
                    }
                }
                if (minIdx != -1) {
                    assigned[minIdx] = true;
                    ghostToTarget[minIdx] = otherPPs[j];
                }
            }
        }
		
        for (int i = 0; i < 4; i++) {
            GHOST ghostType = ghosts[i];
            if (game.doesGhostRequireAction(ghostType)) {
                // Si el fantasma es comestible
                if(game.isGhostEdible(ghostType)) {
                    // Si le quedan menos de 5 seg en estado comestible, empieza a perseguir al pacman
                    if(game.getGhostEdibleTime(ghostType) < 5) {
                        MOVE move = game.getApproximateNextMoveTowardsTarget(
                                game.getGhostCurrentNodeIndex(ghostType),
                                posPacMan,
                                game.getGhostLastMoveMade(ghostType),
                                Constants.DM.PATH);
                        ghostMove.put(ghostType, move);
                    // Si le quedan mas de 5 seg en estado comestible, se comprueba si tiene esquina asignada
                    }else {
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
                                    posPacMan,
                                    game.getGhostLastMoveMade(ghostType),
                                    Constants.DM.PATH);
                            ghostMove.put(ghostType, move);
                        }
                    }
                } else {
                    // Si el fantasma no es comestible
                    int[] powerPills = game.getActivePowerPillsIndices();
                    boolean pacmanNearPP = false;
                    for (int pp : powerPills) {
                        int dist = game.getShortestPathDistance(posPacMan, pp);
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
                                posPacMan,
                                game.getGhostLastMoveMade(ghostType),
                                Constants.DM.PATH);
                        ghostMove.put(ghostType, move);
                    // Si el fantasma no es comestible y el pacman no esta cerca de pp, se persigue al pacman normak
                    } else {
                    	// Se usa una forma diferente para cada fantasma para perseguir al pacman
                        Constants.DM[] dms = {Constants.DM.PATH, Constants.DM.MANHATTAN, Constants.DM.EUCLID, Constants.DM.PATH};
                        int ghostIdx = ghostType.ordinal(); // Asigna un valor distinto a cada fantasma
                        Constants.DM chaseMethod = dms[ghostIdx % dms.length];

                        if (rnd.nextDouble() < 0.9) {
                        	GameView.addPoints(game,colours[ghostIdx],game.getShortestPath(game.getGhostCurrentNodeIndex(ghostType), posPacMan));
                            MOVE move = game.getApproximateNextMoveTowardsTarget(
                                    game.getGhostCurrentNodeIndex(ghostType),
                                    posPacMan,
                                    game.getGhostLastMoveMade(ghostType),
                                    chaseMethod);
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
	
		while(!game.isJunction(node)) {
			node = game.getNeighbour(node , game.getPacmanLastMoveMade());
		
		}
		return node;
	}
	

}