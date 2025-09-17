package es.ucm.fdi.ici.c2425.practica0.garcia;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public final class MsPacManRunAway extends PacmanController {
	GHOST nearGhost;
	Double minDistance=Double.MAX_VALUE, distance=Double.MAX_VALUE;

	@Override
	public MOVE getMove(Game game, long timeDue) {

		for (GHOST ghostType : GHOST.values()) {
			if (game.doesGhostRequireAction(ghostType))
			distance = game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostType),
					game.getPacmanLastMoveMade(), Constants.DM.PATH);

			if (minDistance > distance) {
				minDistance = distance;
				nearGhost = ghostType;
			}
		}

		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);

	}

	public String getName() {
		return " MsPacManRunAway";
	}
}
