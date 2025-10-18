package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.awt.Color;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class DRunAwayToNearestSafePillAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		int node = p.getNearestSafePill(game);
		GameView.addLines(game, Color.YELLOW, game.getPacmanCurrentNodeIndex(), node);
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), p.getNearestSafePill(game),
				game.getPacmanLastMoveMade(), DM.PATH);

	}

	@Override
	public String getActionId() {
		return "RunAway To Nearest Safe Pill";
	}

}
