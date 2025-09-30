package es.ucm.fdi.ici.c2526.practica1.grupoF;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public interface GhostState {
	MOVE action(Game game, GHOST ghost, int posPacman);
}
