package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public class ERunToNearestSafePPAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				p.getNearestSafePowerPill(game), game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public String getActionId() {

		return "Run To Nearest Safe PowerPill";
	}

}