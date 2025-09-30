package es.ucm.fdi.ici.c2526.practica1.grupoF;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.awt.Color;

import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;

public class Pinky implements GhostState {

	private static final int STEPS_AHEAD = 10;

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {
		int predictedPos = posPacman;
		MOVE pacmanMove = game.getPacmanLastMoveMade();
		for (int i = 0; i < STEPS_AHEAD; i++) {
			int next = game.getNeighbour(predictedPos, pacmanMove);
			if (next == -1)
				break;
			predictedPos = next;
		}

		GameView.addPoints(game, Color.PINK, game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), predictedPos));
		return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), predictedPos,
				game.getGhostLastMoveMade(ghost), Constants.DM.PATH);
	}
}
