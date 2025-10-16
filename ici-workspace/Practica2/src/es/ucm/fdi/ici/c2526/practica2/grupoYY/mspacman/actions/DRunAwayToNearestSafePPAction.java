package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import pacman.game.Game;
import pacman.game.GameView;

import java.awt.Color;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class DRunAwayToNearestSafePPAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		GameView.addLines(game, Color.YELLOW, game.getPacmanCurrentNodeIndex(), p.getNearestSafePowerPill(game));

		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				p.getNearestSafePowerPill(game), game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public String getActionId() {

		return "RunAway To Nearest Safe PowerPill";
	}

}
