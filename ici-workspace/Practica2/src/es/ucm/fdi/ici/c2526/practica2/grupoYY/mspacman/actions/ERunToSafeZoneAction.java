package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions;

import java.awt.Color;

import es.ucm.fdi.ici.Action;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class ERunToSafeZoneAction implements Action {

	@Override
	public MOVE execute(Game game) {
		PacmanMethods p = new PacmanMethods();
		int node = p.findSafeZone(game);
		if (node != -1) {
			GameView.addLines(game, Color.CYAN, game.getPacmanCurrentNodeIndex(), node);
			return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), node,
					game.getPacmanLastMoveMade(), DM.PATH);
		}
		return MOVE.NEUTRAL;

	}

	@Override
	public String getActionId() {
		return "Run To Safe Zone Action";
	}
}
