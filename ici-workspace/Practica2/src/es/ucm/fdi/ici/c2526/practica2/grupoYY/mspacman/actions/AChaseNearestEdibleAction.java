package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.awt.Color;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class AChaseNearestEdibleAction implements Action {

	@Override
	public MOVE execute(Game game) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		int minDistance = Integer.MAX_VALUE;
		GHOST closestGhost = null;

		for (GHOST ghost : GHOST.values()) {
			if (game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost),
						game.getGhostLastMoveMade(ghost));
				if (dist < minDistance) {
					minDistance = dist;
					closestGhost = ghost;
				}
			}
		}

		GameView.addLines(game, Color.RED, posPacman, game.getGhostCurrentNodeIndex(closestGhost));

		return game.getApproximateNextMoveTowardsTarget(posPacman, game.getGhostCurrentNodeIndex(closestGhost),
				game.getGhostLastMoveMade(closestGhost), DM.PATH);

	}

	@Override
	public String getActionId() {
		return "Chase Nearest Edible Ghost";
	}

}
