package es.ucm.fdi.ici.c2425.practica0.garcia;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public final class MsPacManRunAway extends PacmanController {
	GHOST nearGhost = null;

	int distance, minDistance;

	@Override
	public MOVE getMove(Game game, long timeDue) {
		minDistance = Integer.MAX_VALUE;

		for (GHOST ghostType : GHOST.values()) {

			if (game.getGhostLairTime(ghostType) == 0) {
				
				distance = game.getShortestPathDistance( game.getPacmanCurrentNodeIndex() ,game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanLastMoveMade());

				if (minDistance > distance) {
					minDistance = distance;
					nearGhost = ghostType;
				}
			}
		}

		if (nearGhost != null)
			return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);

		return MOVE.NEUTRAL;

	}

	public String getName() {
		return " MsPacManRunAway";
	}
}
