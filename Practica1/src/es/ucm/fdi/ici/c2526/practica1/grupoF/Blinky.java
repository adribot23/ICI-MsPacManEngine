package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.awt.Color;

import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class Blinky implements GhostState {

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {
		GameView.addPoints(game, Color.RED, game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), posPacman));
		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), posPacman,
				game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
	}

}
