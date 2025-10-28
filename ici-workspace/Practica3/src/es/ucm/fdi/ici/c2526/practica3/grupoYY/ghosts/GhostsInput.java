package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import es.ucm.fdi.ici.rules.RulesInput;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsInput extends RulesInput {

	// Estados básicos
	private boolean BLINKYedible, INKYedible, PINKYedible, SUEedible;
	private boolean BLINKYnearToNotEdibleGhost, INKYnearToNotEdibleGhost, PINKYnearToNotEdibleGhost,
			SUEnearToNotEdibleGhost;
	private boolean BLINKYnearToPacman, INKYnearToPacman, PINKYnearToPacman, SUEnearToPacman;
	private boolean BLINKYnearToEdibleGhost, INKYnearToEdibleGhost, PINKYnearToEdibleGhost, SUEnearToEdibleGhost;
	private boolean BLINKYnearToLastPowerPill, INKYnearToLastPowerPill, PINKYnearToLastPowerPill,
			SUEnearToLastPowerPill;
	private boolean pacmanNearToPowerPill, onlyOnePowerPIllLeft;

	// DISTANCIAS
	private int BLINKYdistToPacman, INKYdistToPacman, PINKYdistToPacman, SUEdistToPacman;
	private int BLINKYdistToJunction, INKYdistToJunction, PINKYdistToJunction, SUEdistToJunction;
	private int BLINKYdistToPill, INKYdistToPill, PINKYdistToPill, SUEdistToPill;

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

		// Cercanía a PowerPill
		this.BLINKYnearToLastPowerPill = nearToLastPowerPill(game, GHOST.BLINKY);
		this.INKYnearToLastPowerPill = nearToLastPowerPill(game, GHOST.INKY);
		this.PINKYnearToLastPowerPill = nearToLastPowerPill(game, GHOST.PINKY);
		this.SUEnearToLastPowerPill = nearToLastPowerPill(game, GHOST.SUE);

		// Cercanía a otros fantasmas comestibles
		this.BLINKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.BLINKY);
		this.INKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.INKY);
		this.PINKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.PINKY);
		this.SUEnearToEdibleGhost = nearToEdibleGhost(game, GHOST.SUE);

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

		this.BLINKYdistToPill = distanceToPacmanPill(game, GHOST.BLINKY);
		this.INKYdistToPill = distanceToPacmanPill(game, GHOST.INKY);
		this.PINKYdistToPill = distanceToPacmanPill(game, GHOST.PINKY);
		this.SUEdistToPill = distanceToPacmanPill(game, GHOST.SUE);
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();

		// BLINKY
		facts.add(String.format(
				"(BLINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (nearToLastPowerPill %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.BLINKYedible, this.BLINKYnearToNotEdibleGhost, this.BLINKYnearToEdibleGhost,
				this.BLINKYnearToPacman, this.BLINKYnearToLastPowerPill, this.BLINKYdistToPacman,
				this.BLINKYdistToJunction, this.BLINKYdistToPill));

		// INKY
		facts.add(String.format(
				"(INKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (nearToLastPowerPill %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.INKYedible, this.INKYnearToNotEdibleGhost, this.INKYnearToEdibleGhost, this.INKYnearToPacman,
				this.INKYnearToLastPowerPill, this.INKYdistToPacman, this.INKYdistToJunction, this.INKYdistToPill));

		// PINKY
		facts.add(String.format(
				"(PINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (nearToLastPowerPill %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.PINKYedible, this.PINKYnearToNotEdibleGhost, this.PINKYnearToEdibleGhost, this.PINKYnearToPacman,
				this.PINKYnearToLastPowerPill, this.PINKYdistToPacman, this.PINKYdistToJunction, this.PINKYdistToPill));

		// SUE
		facts.add(String.format(
				"(SUE (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (nearToLastPowerPill %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.SUEedible, this.SUEnearToNotEdibleGhost, this.SUEnearToEdibleGhost, this.SUEnearToPacman,
				this.SUEnearToLastPowerPill, this.SUEdistToPacman, this.SUEdistToJunction, this.SUEdistToPill));

		// MSPACMAN
		facts.add(String.format("(MSPACMAN (nearToPowerPill %s))", this.pacmanNearToPowerPill));

		// GAME
		facts.add(String.format("(GAME (onlyOnePowerPillLeft %s))", this.onlyOnePowerPIllLeft));

		return facts;
	}

	private boolean nearToLastPowerPill(Game game, GHOST ghost) {
		int posGhost = game.getGhostCurrentNodeIndex(ghost);
		for (int ppill : game.getActivePowerPillsIndices()) {
			int dist = game.getShortestPathDistance(posGhost, ppill, game.getGhostLastMoveMade(ghost));
			if (dist < GhostConstants.GHOST_NEAR_PP_DISTANCE) {
				return true;
			}
		}
		return false;
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
				dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(ghost));
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
			if (dist < GhostConstants.DANGER_PACMAN_PP_DISTANCE) {
				return true;
			}
		}
		return false;
	}

	private boolean nearToEdibleGhost(Game game, GHOST ghost) {
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);
		for (GHOST g : GHOST.values()) {
			if (g != ghost && game.isGhostEdible(g)) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				int dist = game.getShortestPathDistance(posCurrGhost, posGhost);
				if (dist < GhostConstants.GHOST_NEAR_EACH_OTHER) {
					return true;
				}
			}
		}
		return false;
	}

	private int distanceToPacman(Game game, GHOST ghost) {
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int pacmanNode = game.getPacmanCurrentNodeIndex();

		if (ghostNode == -1 || pacmanNode == -1)
			return Integer.MAX_VALUE;

		int dist = game.getShortestPathDistance(ghostNode, pacmanNode, game.getGhostLastMoveMade(ghost));

		int nearGhostPenalty = 0;
		
		int[] path = game.getShortestPath(ghostNode, pacmanNode, game.getGhostLastMoveMade(ghost));
		nearGhostPenalty = ghostPenaltyInPath(game, ghost, path);

		return dist + nearGhostPenalty;

	}

	private int distanceToPacmanJunction(Game game, GHOST ghost) {
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int pacmanJunction = nextPacmanJunction(game);
		if (pacmanJunction == -1)
			return Integer.MAX_VALUE;

		int nearGhostPenalty = 0;
		int[] path = game.getShortestPath(ghostNode, pacmanJunction, game.getGhostLastMoveMade(ghost));
		nearGhostPenalty = ghostPenaltyInPath(game, ghost, path);

		int dist = game.getShortestPathDistance(ghostNode, pacmanJunction, game.getGhostLastMoveMade(ghost));
		return dist + nearGhostPenalty;
	}

	private int distanceToPacmanPill(Game game, GHOST ghost) {
		int pacManNode = game.getPacmanCurrentNodeIndex();
		int ghostNode = game.getGhostCurrentNodeIndex(ghost);
		int minDist = Integer.MAX_VALUE;
		int pill = -1;
		for (int pp : game.getActivePillsIndices()) {
			int d = game.getShortestPathDistance(pacManNode, pp, game.getPacmanLastMoveMade());
			if (d < minDist) {
				minDist = d;
				pill = pp;
			}
		}
		if(pill == -1)
			return Integer.MAX_VALUE;
		int[] path = game.getShortestPath(ghostNode, pill, game.getGhostLastMoveMade(ghost));
		int nearGhostPenalty = ghostPenaltyInPath(game, ghost, path);

		int dist = game.getShortestPathDistance(ghostNode, pill, game.getGhostLastMoveMade(ghost));
		return dist + nearGhostPenalty;
	}

	private int ghostPenaltyInPath(Game game, GHOST ghost, int[] path) {
		int penalty = 0;
		for (int node : path) {
			for (GHOST g : GHOST.values()) {
				if (g == ghost)
					continue;
				int otherNode = game.getGhostCurrentNodeIndex(g);
				if (otherNode == -1 || game.getGhostLairTime(g) > 0 || game.isGhostEdible(g))
					continue;
				if (otherNode == node)
					penalty += GhostConstants.GHOST_IN_THE_PATH;
			}
		}
		return penalty;
	}
}
