package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ARunAwayFromNearestPowerPill implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();

		return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), p.getNearPowerPill(game),
				game.getPacmanLastMoveMade(), DM.PATH);
	}

	@Override
	public String getActionId() {
		return "RunAway From Nearest PowerPill";
	}

}
