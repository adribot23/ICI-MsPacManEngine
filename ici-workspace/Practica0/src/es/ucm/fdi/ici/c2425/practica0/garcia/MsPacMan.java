package es.ucm.fdi.ici.c2425.practica0.garcia;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public final class MsPacMan extends PacmanController {

	
	int minDistance,distance,limit=20;
	GHOST nearGhost;
	@Override
	public MOVE getMove(Game game, long timeDue) {

		
		 nearGhost = getNearestChasingGhost(game);
		 
		 if(nearGhost!=null) {
			 return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);
		 }
		 
		 
		 nearGhost =  getNearestEdibleGhost( game);
		 
		 if(nearGhost!=null) {
			 return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);
		 }
		 
		 return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					getNearestPill(game), game.getPacmanLastMoveMade(), Constants.DM.PATH);
		
		 
		
	}

	
	private GHOST getNearestChasingGhost(Game game) {
		minDistance = Integer.MAX_VALUE;
		nearGhost = null;
		for (GHOST ghostType : GHOST.values()) {
			if (game.getGhostLairTime(ghostType) == 0 && !game.isGhostEdible(ghostType)) {
				distance = game.getShortestPathDistance( game.getPacmanCurrentNodeIndex() ,game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanLastMoveMade());
				if (minDistance > distance && distance<limit) {
					minDistance = distance;
					nearGhost = ghostType;
				}
			}
		}
		return nearGhost;
		
	}

	private GHOST getNearestEdibleGhost(Game game) {
		minDistance = Integer.MAX_VALUE;
		nearGhost = null;
		for (GHOST ghostType : GHOST.values()) {
			if (game.isGhostEdible(ghostType)) {
				distance = game.getShortestPathDistance( game.getPacmanCurrentNodeIndex() ,game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanLastMoveMade());
				if (minDistance > distance && distance<limit) {
					minDistance = distance;
					nearGhost = ghostType;
				}
			}
		}
		return nearGhost;
		
	}
	
	
	int getNearestPill(Game game) {
		minDistance = Integer.MAX_VALUE;
		int[] powerPills = game.getActivePowerPillsIndices();
		int[] pills = game.getActivePillsIndices();
		int pillIndex = 0;
		for (int i : powerPills) {
			 distance= game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), i,
					game.getPacmanLastMoveMade());
			 
				if (minDistance > distance) {
					minDistance = distance;
					pillIndex = i;
					}
			
		}
		
		for (int i : pills) {
			 distance= game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), i,
					game.getPacmanLastMoveMade());
			 
				if (minDistance > distance) {
					minDistance = distance;
					pillIndex = i;
					}
			
		}
		
		return	pillIndex;
	}

	public String getName() {
		return "MsPacMan";
	}
}
