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
	private boolean pacmanNearToPowerPill, onlyOnePowerPIllLeft, lastPills;

	// DISTANCIAS
	private int BLINKYdistToPacman, INKYdistToPacman, PINKYdistToPacman, SUEdistToPacman;
	private int BLINKYdistToJunction, INKYdistToJunction, PINKYdistToJunction, SUEdistToJunction;
	private int BLINKYdistToPill, INKYdistToPill, PINKYdistToPill, SUEdistToPill;
	
	// Fantasmas asignados 
	private int firstGhost, secondGhost;

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

		// Numero de pills / power pills activas
		this.onlyOnePowerPIllLeft = game.getNumberOfActivePowerPills() == 1;
		this.lastPills = game.getNumberOfActivePills() < GhostConstants.LAST_PILLS;

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

		// Cercanía a otros fantasmas comestibles
		this.BLINKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.BLINKY);
		this.INKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.INKY);
		this.PINKYnearToEdibleGhost = nearToEdibleGhost(game, GHOST.PINKY);
		this.SUEnearToEdibleGhost = nearToEdibleGhost(game, GHOST.SUE);

		// Fantasmas asignados
		this.firstGhost = ghostsAssigned(game)[0];
		this.secondGhost = ghostsAssigned(game)[1];
		
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
				"(BLINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.BLINKYedible, this.BLINKYnearToNotEdibleGhost, this.BLINKYnearToEdibleGhost,
				this.BLINKYnearToPacman, this.BLINKYdistToPacman, this.BLINKYdistToJunction, this.BLINKYdistToPill));

		// INKY
		facts.add(String.format(
				"(INKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.INKYedible, this.INKYnearToNotEdibleGhost, this.INKYnearToEdibleGhost, this.INKYnearToPacman,
				this.INKYdistToPacman, this.INKYdistToJunction, this.INKYdistToPill));

		// PINKY
		facts.add(String.format(
				"(PINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.PINKYedible, this.PINKYnearToNotEdibleGhost, this.PINKYnearToEdibleGhost, this.PINKYnearToPacman,
				this.PINKYdistToPacman, this.PINKYdistToJunction, this.PINKYdistToPill));

		// SUE
		facts.add(String.format(
				"(SUE (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s) (distToPacman %d) (distToPacmanJunction %d) (distToPacmanPill %d))",
				this.SUEedible, this.SUEnearToNotEdibleGhost, this.SUEnearToEdibleGhost, this.SUEnearToPacman,
				this.SUEdistToPacman, this.SUEdistToJunction, this.SUEdistToPill));

		// MSPACMAN
		facts.add(String.format("(MSPACMAN (nearToPowerPill %s))", this.pacmanNearToPowerPill));

		// GAME
		facts.add(String.format("(GAME (onlyOnePowerPillLeft %s) (lastPills %s) (firstGhost %d) (secondGhost %d))", 
				this.onlyOnePowerPIllLeft, this.lastPills, this.firstGhost, this.secondGhost));

		return facts;
	}
	
	private int[] ghostsAssigned(Game game) {
		int[] assigned = new int[2];
		assigned[0] = -1;
		assigned[1] = -1;
		
		// Meter aqui condicion para reiniciarlo, si pacman muere o el numero de pp activas es != 1
		if(!this.onlyOnePowerPIllLeft || game.wasPacManEaten()) return assigned;
		
		int[] powerPills = game.getActivePowerPillsIndices();
		int lastPowerPill = powerPills[0];
		
		GHOST[] ghosts = GHOST.values();
		int[] distances = new int[ghosts.length];
		for (int i = 0; i < ghosts.length; i++) {
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i]))
				distances[i] = Integer.MAX_VALUE;
			else {
				int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
				distances[i] = game.getShortestPathDistance(ghostNode, lastPowerPill, game.getGhostLastMoveMade(ghosts[i]));
				if (distances[i] == -1)
					distances[i] = Integer.MAX_VALUE;
			}
		}
		
		int firstClosest = -1;
		int secondClosest = -1;
		int minDist = Integer.MAX_VALUE;
		int secondMinDist = Integer.MAX_VALUE;
		
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] < minDist) {
				secondClosest = firstClosest;
				secondMinDist = minDist;
				firstClosest = i;
				minDist = distances[i];
			}
			else if (distances[i] < secondMinDist) {
				secondClosest = i;
				secondMinDist = distances[i];
			}
		}
		
		assigned[0] = firstClosest;
		assigned[1] = secondClosest;
		
		return assigned;
	}

	private int[] nextPacmanJunctions(Game game) {
		int pacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		Set<Integer> visited = new HashSet<>();
		Queue<Integer> queue = new LinkedList<>();

		queue.add(pacman);
		visited.add(pacman);

		Integer startJunction = null;
		while (!queue.isEmpty()) {
			int current = queue.poll();
			if (current != pacman && game.isJunction(current)) {
				startJunction = current;
				break;
			}

			for (MOVE move : MOVE.values()) {
				if (move != MOVE.NEUTRAL && !(current == pacman && move == lastMove.opposite())) {
					int next = game.getNeighbour(current, move);
					if (next != -1 && !visited.contains(next)) {
						visited.add(next);
						queue.add(next);
					}
				}
			}
		}

		if (startJunction == null) {
			return new int[] { -1, -1, -1 };
		}

		int[] result = new int[] { -1, -1, -1 };
		int count = 0;

		for (MOVE m : game.getPossibleMoves(startJunction)) {
			if (m == MOVE.NEUTRAL) continue;

			int prev = startJunction;
			int current = game.getNeighbour(startJunction, m);
			if (current == -1) continue;

			Set<Integer> localVisited = new HashSet<>();
			localVisited.add(prev);
			localVisited.add(current);

			while (current != -1 && !game.isJunction(current)) {
				int nextNode = -1;
				for (MOVE nm : game.getPossibleMoves(current)) {
					if (nm == MOVE.NEUTRAL) continue;
					int cand = game.getNeighbour(current, nm);
					if (cand == -1) continue;
					if (cand != prev && !localVisited.contains(cand)) {
						nextNode = cand;
						break;
					}
				}
				if (nextNode == -1)
					break;
				prev = current;
				current = nextNode;
				localVisited.add(current);
			}

			if (current != -1 && game.isJunction(current) && current != startJunction) {
				result[count++] = current;
				if (count == 3)
					break;
			}
		}

		return result;
	}

	private int nearestJunction(Game game, GHOST ghost) {
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);
		int[] junctions = nextPacmanJunctions(game);

		int bestNode = -1;
		int minDist = Integer.MAX_VALUE;
		for (int j : junctions) {
			if (j < 0) continue;
			int distToJunction = game.getShortestPathDistance(ghostPos, j, lastMove);
			if (distToJunction >= 0 && distToJunction < minDist) {
				minDist = distToJunction;
				bestNode = j;
			}
		}
		return bestNode;
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
		int pacmanJunction = nearestJunction(game, ghost);
		if (pacmanJunction == -1)
			return Integer.MAX_VALUE;

		int nearGhostPenalty = 0;
		int[] path = game.getShortestPath(ghostNode, pacmanJunction, game.getGhostLastMoveMade(ghost));
		nearGhostPenalty = ghostPenaltyInPath(game, ghost, path);

		int[] pacmanPath = game.getShortestPath(game.getPacmanCurrentNodeIndex(), pacmanJunction,
				game.getPacmanLastMoveMade());

		for (int i : pacmanPath) {
			if (i == ghostNode)
				return Integer.MAX_VALUE;
		}

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
		if (pill == -1)
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
