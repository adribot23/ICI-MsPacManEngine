package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class DRunAwayFromNearestGhost implements Action {

	@Override
	public MOVE execute(Game game) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		int minDistance = Integer.MAX_VALUE;
		GHOST closestGhost = null;

		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost),
						game.getPacmanLastMoveMade());
				if (dist < minDistance) {
					minDistance = dist;
					closestGhost = ghost;
				}
			}
		}

		return game.getApproximateNextMoveAwayFromTarget(posPacman, game.getGhostCurrentNodeIndex(closestGhost),
				game.getPacmanLastMoveMade(), DM.PATH);

	}

	@Override
	public String getActionId() {
		// TODO Auto-generated method stub
		return "Run Away From Nearest Ghost";
	}

}
