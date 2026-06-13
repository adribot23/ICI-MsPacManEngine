package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.awt.Color;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class AChaseSeveralGhostRouteAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		int node = p.twoOrMoreGhostsCloseEachOther(game);
		GameView.addLines(game, Color.RED, game.getPacmanCurrentNodeIndex(), node);
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), node,
				game.getPacmanLastMoveMade(), DM.PATH);

	}

	@Override
	public String getActionId() {
		return "Chase Several Ghost Route Action";
	}
}
