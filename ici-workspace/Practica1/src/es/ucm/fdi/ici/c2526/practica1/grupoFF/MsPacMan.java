package es.ucm.fdi.ici.c2526.practica1.grupoFF;

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

public class MsPacMan extends PacmanController{

	//private static final Color[] colours = {Color.RED, Color.PINK, Color.CYAN, Color.ORANGE};

    @Override
    public MOVE getMove(Game game, long timeDue) {
        int posPacman = game.getPacmanCurrentNodeIndex(); 
        MOVE lastMove = game.getPacmanLastMoveMade(); 

        int tooCloseDistance = 80;
        int dangerDistance = 15;
        int minPills = 10;
        int minDistance = Integer.MAX_VALUE;
        GHOST closestGhost = null;
        boolean ghostIsEdible = false;
        
    	Queue<Integer> q = new LinkedList<>();
    	Set<Integer> s = new HashSet<>();

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
        if (closestGhost != null && minDistance < tooCloseDistance && !ghostIsEdible) { 
        	posGhost = game.getGhostCurrentNodeIndex(closestGhost);
        	//GameView.addPoints(game,colours[closestGhost.ordinal()],game.getShortestPath(posGhost,posPacman));
        	GameView.addLines(game,Color.CYAN, posPacman, posGhost);
            return game.getApproximateNextMoveAwayFromTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
        }

        // Perseguir el fantasma si es comestible
        if (closestGhost != null && ghostIsEdible && game.getGhostEdibleTime(closestGhost) > 3) {
        	posGhost = game.getGhostCurrentNodeIndex(closestGhost);
        	//GameView.addPoints(game,colours[closestGhost.ordinal()],game.getShortestPath(posGhost,posPacman));
        	GameView.addLines(game,Color.CYAN, posPacman, posGhost);
            return game.getApproximateNextMoveTowardsTarget(posPacman, posGhost, lastMove, Constants.DM.PATH);
        }

        int[] pills = game.getActivePillsIndices();
        int[] powerPills = game.getActivePowerPillsIndices();

        // Contar fantasmas no comestibles fuera de la guarida
        int nonEdibleOut = 0;
        for (GHOST ghost : GHOST.values()) {
            if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
                nonEdibleOut++;
            }
        }
        
        boolean avoidPowerPillZone = nonEdibleOut < 3;
        int minPowerPillDist = 10;
        
        // Buscar pill normal más segura, evitando acercarse a power pills si es necesario
        int safestPill = -1;
        int numPills = game.getNumberOfActivePills();
        if(numPills < minPills) dangerDistance = 20;
        minDistance = Integer.MAX_VALUE;
        for (int pill : pills) {
            int[] path = game.getShortestPath(posPacman, pill, lastMove);
            boolean safe = true;
            // Si hay que evitar zonas de power pills, descartar caminos que pasen cerca de ellas
            if (avoidPowerPillZone) {
                for (int pp : powerPills) {
                    for (int node : path) {
                        int distPP = game.getShortestPathDistance(node, pp);
                        if (distPP != -1 && distPP < minPowerPillDist) {
                            safe = false;
                            break;
                        }
                    }
                    if (!safe) break;
                }
            }
            if (!safe) continue;
            // Comprobar fantasmas peligrosos cerca del camino
            for (GHOST ghost : GHOST.values()) {
                int ghostPos = game.getGhostCurrentNodeIndex(ghost);
                if (game.getGhostLairTime(ghost) <= 0 && !game.isGhostEdible(ghost)) {
                    for (int p : path) {
                        int dist = game.getShortestPathDistance(ghostPos, p);
                        if (dist != -1 && dist < dangerDistance) {
                            safe = false;
                            break;
                        }
                    }
                    if (!safe) break;
                }
            }
            if (safe) {
                int dist = path.length;
                if (dist < minDistance) {
                    minDistance = dist;
                    safestPill = pill;
                }
            }
        }

        // Buscar power pill solo si hay 3 o más fantasmas fuera
        int powerPillDistance = 30;
        int safestPowerPill = -1;
        minDistance = Integer.MAX_VALUE;
        if (!avoidPowerPillZone) {
            for (int pp : powerPills) {
                int[] path = game.getShortestPath(posPacman, pp, lastMove);
                boolean safe = true;
                for (GHOST ghost : GHOST.values()) {
                    int ghostPos = game.getGhostCurrentNodeIndex(ghost);
                    if (game.getGhostLairTime(ghost) <= 0 || !game.isGhostEdible(ghost)) {
                        for (int p : path) {
                            int dist = game.getShortestPathDistance(ghostPos, p);
                            if (dist != -1 && dist < dangerDistance) {
                                safe = false;
                                break;
                            }
                        }
                        if (!safe) break;
                    }
                }
                if (safe) {
                    int dist = path.length;
                    if (dist < minDistance && dist < powerPillDistance) {
                        minDistance = dist;
                        safestPowerPill = pp;
                    }
                }
            }
        }

        // Prioridad: power pill (si permitido), si no pill normal, si no neutral
        if (safestPowerPill != -1) {
            return game.getApproximateNextMoveTowardsTarget(posPacman, safestPowerPill, lastMove, Constants.DM.PATH);
        }
        if (safestPill != -1) {
            return game.getApproximateNextMoveTowardsTarget(posPacman, safestPill, lastMove, Constants.DM.PATH);
        }

        return MOVE.NEUTRAL;
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
