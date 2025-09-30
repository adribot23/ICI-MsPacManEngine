package es.ucm.fdi.ici.c2526.practica1.grupoF;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Blinky implements GhostState{

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {
		return game.getApproximateNextMoveTowardsTarget(posPacman, posPacman, game.getPacmanLastMoveMade(), Constants.DM.PATH);
	}

}
