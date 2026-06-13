package es.ucm.fdi.ici.c2425.practica0.garcia;

import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class MsPacManRandom extends PacmanController {

	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		return allMoves[rnd.nextInt(allMoves.length)];
	}

	public String getName() {
		return "MsPacManRandom";
	}

}
