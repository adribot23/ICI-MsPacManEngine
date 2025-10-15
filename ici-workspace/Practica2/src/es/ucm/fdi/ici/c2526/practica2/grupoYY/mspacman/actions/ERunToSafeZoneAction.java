package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ERunToSafeZoneAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), p.findSafeZone(game),
				game.getPacmanLastMoveMade(), DM.PATH);

	}

	@Override
	public String getActionId() {
		return "Run To Safe Zone Action";
	}
}
