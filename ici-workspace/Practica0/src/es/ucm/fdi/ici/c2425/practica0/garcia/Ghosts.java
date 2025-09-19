package es.ucm.fdi.ici.c2425.practica0.garcia;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public final class Ghosts extends GhostController {
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();
	private int pacmanIndex, ghostIndex;
	private MOVE ghostMove;
	private int pillLimit = 50;

	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {

		moves.clear();

		pacmanIndex = game.getPacmanCurrentNodeIndex();

		for (GHOST ghostType : GHOST.values()) {

			if (game.doesGhostRequireAction(ghostType)) {

				ghostIndex = game.getGhostCurrentNodeIndex(ghostType);
				ghostMove = game.getGhostLastMoveMade(ghostType);

				if (game.isGhostEdible(ghostType) || pacmanCloseToPowerPill(game)) {

					moves.put(ghostType, game.getApproximateNextMoveAwayFromTarget(ghostIndex, pacmanIndex, ghostMove,
							Constants.DM.PATH));
				} else {
					if (rnd.nextFloat() < 0.9)
						moves.put(ghostType, game.getApproximateNextMoveTowardsTarget(ghostIndex, pacmanIndex,
								ghostMove, Constants.DM.PATH));
					else

						moves.put(ghostType, allMoves[rnd.nextInt(allMoves.length)]);
				}

			}
		}
		return moves;
	}

	boolean pacmanCloseToPowerPill(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();
		for (int i : powerPills) {
			if (pillLimit > game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), i,
					game.getPacmanLastMoveMade()))
				return true;
		}
		return false;
	}

	public String getName() {
		return "Ghosts";
	}
}
