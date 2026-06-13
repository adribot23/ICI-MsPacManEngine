package es.ucm.fdi.ici.c2425.practica0.garcia;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class GhostsAggresive extends GhostController {

	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		moves.clear();
		for (GHOST ghostType : GHOST.values()) {
			if (game.doesGhostRequireAction(ghostType))
				moves.put(ghostType, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghostType),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghostType), Constants.DM.PATH));

		}
		return moves;

	}

	public String getName() {
		return "GhostsAggresive";
	}

}
