package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	// Estados básicos
	private boolean BLINKYedible, INKYedible, PINKYedible, SUEedible;
	private boolean BLINKYnearToNotEdibleGhost, INKYnearToNotEdibleGhost, PINKYnearToNotEdibleGhost,
			SUEnearToNotEdibleGhost;
	private boolean BLINKYnearToPacman, INKYnearToPacman, PINKYnearToPacman, SUEnearToPacman;
	private boolean pacmanNearToPowerPill, onlyOnePowerPIllLeft;

	// DISTANCIAS pre-calculadas
	private int BLINKYdistToPacman, INKYdistToPacman, PINKYdistToPacman, SUEdistToPacman;
	private int BLINKYdistToJunction, INKYdistToJunction, PINKYdistToJunction, SUEdistToJunction;
	private int BLINKYdistToPowerPill, INKYdistToPowerPill, PINKYdistToPowerPill, SUEdistToPowerPill;

	public GhostsInput(Game game) {
		super(game);
	}

	@Override
	public void parseInput() {
		// Comestibilidad
		this.BLINKYedible = game.isGhostEdible(GHOST.BLINKY);
		this.INKYedible = game.isGhostEdible(GHOST.INKY);
		this.PINKYedible = game.isGhostEdible(GHOST.PINKY);
		this.SUEedible = game.isGhostEdible(GHOST.SUE);
		this.onlyOnePowerPIllLeft = game.getNumberOfActivePowerPills() == 1;

		// Cercanía a otros fantasmas no comestibles
		this.BLINKYnearToNotEdibleGhost = nearestNotEdibleGhost(game, GHOST.BLINKY) != -1;
		this.INKYnearToNotEdibleGhost = nearestNotEdibleGhost(game, GHOST.INKY) != -1;
		this.PINKYnearToNotEdibleGhost = nearestNotEdibleGhost(game, GHOST.PINKY) != -1;
		this.SUEnearToNotEdibleGhost = nearestNotEdibleGhost(game, GHOST.SUE) != -1;

		// Cercanía a Pacman
		this.BLINKYnearToPacman = nearToPacman(game, GHOST.BLINKY);
		this.INKYnearToPacman = nearToPacman(game, GHOST.INKY);
		this.PINKYnearToPacman = nearToPacman(game, GHOST.PINKY);
		this.SUEnearToPacman = nearToPacman(game, GHOST.SUE);

		// Cercanía a power pill
		this.pacmanNearToPowerPill = pacmanNearToPowerPill(game);

		// DISTANCIAS pre-calculadas
		this.BLINKYdistToPacman = distanceToPacman(game, GHOST.BLINKY);
		this.INKYdistToPacman = distanceToPacman(game, GHOST.INKY);
		this.PINKYdistToPacman = distanceToPacman(game, GHOST.PINKY);
		this.SUEdistToPacman = distanceToPacman(game, GHOST.SUE);

		this.BLINKYdistToJunction = distanceToPacmanJunction(game, GHOST.BLINKY);
		this.INKYdistToJunction = distanceToPacmanJunction(game, GHOST.INKY);
		this.PINKYdistToJunction = distanceToPacmanJunction(game, GHOST.PINKY);
		this.SUEdistToJunction = distanceToPacmanJunction(game, GHOST.SUE);

		this.BLINKYdistToPowerPill = distanceToPacmanPowerPill(game, GHOST.BLINKY);
		this.INKYdistToPowerPill = distanceToPacmanPowerPill(game, GHOST.INKY);
		this.PINKYdistToPowerPill = distanceToPacmanPowerPill(game, GHOST.PINKY);
		this.SUEdistToPowerPill = distanceToPacmanPowerPill(game, GHOST.SUE);
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();

		// BLINKY
		facts.add(String.format(
				"(BLINKY (edible %s) (nearToNotEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPowerPill %d))",
				this.BLINKYedible, this.BLINKYnearToNotEdibleGhost, this.BLINKYnearToPacman, this.BLINKYdistToPacman,
				this.BLINKYdistToJunction, this.BLINKYdistToPowerPill));

		// INKY
		facts.add(String.format(
				"(INKY (edible %s) (nearToNotEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPowerPill %d))",
				this.INKYedible, this.INKYnearToNotEdibleGhost, this.INKYnearToPacman, this.INKYdistToPacman,
				this.INKYdistToJunction, this.INKYdistToPowerPill));

		// PINKY
		facts.add(String.format(
				"(PINKY (edible %s) (nearToNotEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPowerPill %d))",
				this.PINKYedible, this.PINKYnearToNotEdibleGhost, this.PINKYnearToPacman, this.PINKYdistToPacman,
				this.PINKYdistToJunction, this.PINKYdistToPowerPill));

		// SUE
		facts.add(String.format(
				"(SUE (edible %s) (nearToNotEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPowerPill %d))",
				this.SUEedible, this.SUEnearToNotEdibleGhost, this.SUEnearToPacman, this.SUEdistToPacman,
				this.SUEdistToJunction, this.SUEdistToPowerPill));

		// MSPACMAN
		facts.add(String.format("(MSPACMAN (nearToPowerPill %s))", this.pacmanNearToPowerPill));

		// GAME
		facts.add(String.format("(GAME (onlyOnePowerPillLeft %s))", this.onlyOnePowerPIllLeft));

		return facts;
	}

	private int nextPacmanJunction(Game game) {
		int start = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		Set<Integer> visited = new HashSet<>();
		Queue<Integer> queue = new LinkedList<>();

		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty()) {
			int current = queue.poll();

			if (current != start && game.isJunction(current)) {
				return current;
			}

			for (MOVE move : MOVE.values()) {

				if (move != MOVE.NEUTRAL && !(current == start && move == lastMove.opposite())) {

					int next = game.getNeighbour(current, move);
					if (next != -1 && !visited.contains(next)) {
						visited.add(next);
						queue.add(next);
					}
				}
			}
		}

		return -1;
	}

	private int nearestNotEdibleGhost(Game game, GHOST ghost) {
		int dist = 0, minDist = Integer.MAX_VALUE, posNearNotEdible = -1;
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);

		for (GHOST g : GHOST.values()) {
			if (g != ghost && !game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(g));
				if (dist < minDist && dist < GhostConstants.NOT_EDIBLE_GHOST_DISTANCE) {
					minDist = dist;
					posNearNotEdible = posGhost;
				}
			}
		}
		return posNearNotEdible;

	}

	private boolean nearToPacman(Game game, GHOST ghost) {
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);
		int posPacman = game.getPacmanCurrentNodeIndex();
		int dist = game.getShortestPathDistance(posCurrGhost, posPacman, game.getGhostLastMoveMade(ghost));
		return dist < GhostConstants.DANGER_DISTANCE;
	}

	private boolean pacmanNearToPowerPill(Game game) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		for (int ppill : game.getActivePowerPillsIndices()) {
			int dist = game.getShortestPathDistance(posPacman, ppill, game.getPacmanLastMoveMade());
			if (dist < GhostConstants.DANGER_PILL_DISTANCE) {
				return true;
			}
		}
		return false;
	}

	private int distanceToPacman(Game game, GHOST ghost) {
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		return game.getShortestPathDistance(ghostNode, pacmanNode);
	}

	private int distanceToPacmanJunction(Game game, GHOST ghost) {
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int pacmanJunction = nextPacmanJunction(game);
		if (pacmanJunction == -1)
			return Integer.MAX_VALUE;
		return game.getShortestPathDistance(ghostNode, pacmanJunction);
	}

	private int distanceToPacmanPowerPill(Game game, GHOST ghost) {
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int minDist = Integer.MAX_VALUE;
		for (int pp : game.getActivePowerPillsIndices()) {
			int d = game.getShortestPathDistance(ghostNode, pp);
			if (d < minDist)
				minDist = d;
		}
		return minDist;
	}

}
