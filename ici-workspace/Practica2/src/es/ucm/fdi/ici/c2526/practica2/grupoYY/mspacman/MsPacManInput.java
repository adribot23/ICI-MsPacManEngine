package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.PacmanMethods;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class MsPacManInput extends Input {

	private boolean avoidPowerPills;
	private boolean powerPillEaten;
	private boolean edibleGhostInGame;
	private boolean nearToNotEdibleGhost;
	private boolean onlyOneFarEdibleGhost;
	private boolean ghostEaten;
	private boolean nearToEdibleGhost;
	private boolean ghostOutside;
	private int twoOrMoreGhostsCloseEachOther;
	private int nearestSafePill;
	private int nearestSafePP;
	private int nearPP;

	public MsPacManInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		PacmanMethods m = new PacmanMethods();

		twoOrMoreGhostsCloseEachOther = m.twoOrMoreGhostsCloseEachOther(game);
		nearestSafePP = m.getNearestSafePowerPill(game);
		nearestSafePill = m.getNearestSafePill(game);
		avoidPowerPills = m.avoidPowerPillZone(game);
		nearPP = m.getNearPowerPill(game);

		powerPillEaten = game.wasPowerPillEaten();
		onlyOneFarEdibleGhost = onlyOneFarEdibleGhost();
		edibleGhostInGame = edibleGhostInGame();
		nearToNotEdibleGhost = nearToNotEdibleGhost();
		nearToEdibleGhost = nearToEdibleGhost();
		ghostOutside = ghostOutside();
		ghostEaten = ghostEaten();

	}

	public int getTwoOrMoreGhostsCloseEachOther() {

		return twoOrMoreGhostsCloseEachOther;
	}

	public int getNearestSafePowerPill() {
		return nearestSafePP;
	}

	public int getNearestSafePill() {
		return nearestSafePill;
	}

	public boolean getAvoidPowerPills() {
		return avoidPowerPills;
	}

	public int getNearPP() {
		return nearPP;
	}

	public boolean getWasPowerPillEaten() {
		return powerPillEaten;
	}

	public boolean getOnlyOneFarEdibleGhost() {
		return onlyOneFarEdibleGhost;
	}

	public boolean getIsEdibleGhostInGame() {
		return edibleGhostInGame;
	}

	public boolean getNearToNotEdibleGhost() {
		return nearToNotEdibleGhost;
	}

	public boolean getNearToEdibleGhost() {
		return nearToEdibleGhost;
	}

	public boolean getGhostEaten() {
		return ghostEaten;
	}

	public boolean getGhostOutside() {
		return ghostOutside;
	}

	private boolean nearToEdibleGhost() {
		int dist = 0;

		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(g));
				if (dist < PacmanConfig.EDIBLE_GHOST_DISTANCE)
					return true;
			}
		}

		return false;
	}

	private boolean onlyOneFarEdibleGhost() {
		int dist = 0, cont = 0;

		for (GHOST g : GHOST.values()) {
			if (game.isGhostEdible(g)) {
				cont++;
				if (cont == 1)
					dist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(g));

				else if (cont > 1)
					break;
			}
		}
		if (cont == 1 && dist >= PacmanConfig.GHOST_IS_FAR)
			return true;

		return false;
	}

	private boolean nearToNotEdibleGhost() {
		int posPacman = game.getPacmanCurrentNodeIndex();

		for (GHOST ghost : GHOST.values()) {
			if (!game.isGhostEdible(ghost) && game.getGhostLairTime(ghost) <= 0) {
				int dist = game.getShortestPathDistance(posPacman, game.getGhostCurrentNodeIndex(ghost));
				if (dist < PacmanConfig.DANGER_DISTANCE) {
					return true;
				}
			}
		}
		return false;

	}

	private boolean edibleGhostInGame() {

		for (GHOST g : GHOST.values())
			if (game.isGhostEdible(g))
				return true;

		return false;
	}

	private boolean ghostOutside() {

		for (GHOST ghost : GHOST.values()) {
			if (game.getGhostLairTime(ghost) <= 0)
				return true;
		}
		return false;
	}

	private boolean ghostEaten() {

		for (GHOST g : GHOST.values()) {
			if (game.wasGhostEaten(g)) {
				return true;
			}
		}
		return false;
	}

}
