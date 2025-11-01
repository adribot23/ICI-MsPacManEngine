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

	// Fantasmas asignados
	private int firstGhost, secondGhost;
	private int nearestGhostToPacman, nearestGhostToFirstJunction, nearestGhostToSecondJunction,
			nearestGhostToThirdJunction;

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
		this.nearestGhostToPacman = nearestGhostToPacman(game);
		this.nearestGhostToFirstJunction = nearestGhostToFirstJunction(game);
		this.nearestGhostToSecondJunction = nearestGhostToSecondJunction(game);
		this.nearestGhostToThirdJunction = nearestGhostToThirdJunction(game);

		// Cercanía a power pill
		this.pacmanNearToPowerPill = pacmanNearToPowerPill(game);
	}

	@Override
	public Collection<String> getFacts() {
		Vector<String> facts = new Vector<String>();

		// BLINKY
		facts.add(String.format(
				"(BLINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s))",
				this.BLINKYedible, this.BLINKYnearToNotEdibleGhost, this.BLINKYnearToEdibleGhost,
				this.BLINKYnearToPacman));

		// INKY
		facts.add(String.format(
				"(INKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s))",
				this.INKYedible, this.INKYnearToNotEdibleGhost, this.INKYnearToEdibleGhost, 
				this.INKYnearToPacman));

		// PINKY
		facts.add(String.format(
				"(PINKY (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s))",
				this.PINKYedible, this.PINKYnearToNotEdibleGhost, this.PINKYnearToEdibleGhost, 
				this.PINKYnearToPacman));

		// SUE
		facts.add(String.format(
				"(SUE (edible %s) (nearToNotEdibleGhost %s) (nearToEdibleGhost %s) (nearToPacman %s))",
				this.SUEedible, this.SUEnearToNotEdibleGhost, this.SUEnearToEdibleGhost, 
				this.SUEnearToPacman));

		// MSPACMAN
		facts.add(String.format("(MSPACMAN (nearToPowerPill %s))", this.pacmanNearToPowerPill));

		// GAME
		facts.add(String.format("(GAME (onlyOnePowerPillLeft %s) (lastPills %s) (firstGhost %d) (secondGhost %d) (nearestGhostToPacman %d) (nearestGhostToFirstJunction %d) (nearestGhostToSecondJunction %d) (nearestGhostToThirdJunction %d))",
				this.onlyOnePowerPIllLeft, this.lastPills, this.firstGhost, this.secondGhost,
				this.nearestGhostToPacman, this.nearestGhostToFirstJunction, this.nearestGhostToSecondJunction, 
				this.nearestGhostToThirdJunction));

		return facts;
	}

	private int nearestGhostToPacman(Game game) {
		int pacmanNode = game.getPacmanCurrentNodeIndex();
		GHOST[] ghosts = GHOST.values();
		int nearestGhost = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int i = 0; i < ghosts.length; i++) {
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i])) continue;

			int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
			int distance = game.getShortestPathDistance(ghostNode, pacmanNode, game.getGhostLastMoveMade(ghosts[i]));

			if (distance != -1 && distance < minDistance) {
				minDistance = distance;
				nearestGhost = i;
			}
		}

		return nearestGhost;
	}

	private int nearestGhostToFirstJunction(Game game) {
		int assignedToPacman = nearestGhostToPacman(game);
		int[] junctions = nextPacmanJunctions(game);
		if (junctions[0] == -1)
			return -1;

		GHOST[] ghosts = GHOST.values();
		int nearestGhost = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int i = 0; i < ghosts.length; i++) {
			// Evitamos el fantasma ya asignado a pacman
			if (i == assignedToPacman) continue;
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i])) continue;

			int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
			int distance = game.getShortestPathDistance(ghostNode, junctions[0], game.getGhostLastMoveMade(ghosts[i]));

			if (distance != -1 && distance < minDistance) {
				minDistance = distance;
				nearestGhost = i;
			}
		}

		return nearestGhost;
	}

	private int nearestGhostToSecondJunction(Game game) {
		int assignedToPacman = nearestGhostToPacman(game);
		int assignedToFirstJunction = nearestGhostToFirstJunction(game);
		int[] junctions = nextPacmanJunctions(game);
		if (junctions[1] == -1)
			return -1;

		GHOST[] ghosts = GHOST.values();
		int nearestGhost = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int i = 0; i < ghosts.length; i++) {
			// Evitamos fantasmas ya asignados
			if (i == assignedToPacman || i == assignedToFirstJunction) continue; 
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i])) continue;

			int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
			int distance = game.getShortestPathDistance(ghostNode, junctions[1], game.getGhostLastMoveMade(ghosts[i]));

			if (distance != -1 && distance < minDistance) {
				minDistance = distance;
				nearestGhost = i;
			}
		}

		return nearestGhost;
	}

	private int nearestGhostToThirdJunction(Game game) {
		int assignedToPacman = nearestGhostToPacman(game);
		int assignedToFirstJunction = nearestGhostToFirstJunction(game);
		int assignedToSecondJunction = nearestGhostToSecondJunction(game);
		int[] junctions = nextPacmanJunctions(game);
		if (junctions[2] == -1)
			return -1;

		GHOST[] ghosts = GHOST.values();
		int nearestGhost = -1;
		int minDistance = Integer.MAX_VALUE;

		for (int i = 0; i < ghosts.length; i++) {
			// Evitamos todos los fantasmas ya asignados
			if (i == assignedToPacman || i == assignedToFirstJunction || i == assignedToSecondJunction) continue;
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i])) continue;

			int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
			int distance = game.getShortestPathDistance(ghostNode, junctions[2], game.getGhostLastMoveMade(ghosts[i]));

			if (distance != -1 && distance < minDistance) {
				minDistance = distance;
				nearestGhost = i;
			}
		}

		return nearestGhost;
	}

	private int[] ghostsAssigned(Game game) {
		int[] assigned = new int[2];
		assigned[0] = -1;
		assigned[1] = -1;

		// Meter aqui condicion para reiniciarlo, si pacman muere o el numero de pp
		// activas es != 1
		if (!this.onlyOnePowerPIllLeft || game.wasPacManEaten())
			return assigned;

		int[] powerPills = game.getActivePowerPillsIndices();
		int lastPowerPill = powerPills[0];

		GHOST[] ghosts = GHOST.values();
		int[] distances = new int[ghosts.length];
		for (int i = 0; i < ghosts.length; i++) {
			if (game.getGhostLairTime(ghosts[i]) > 0 || game.isGhostEdible(ghosts[i]))
				distances[i] = Integer.MAX_VALUE;
			else {
				int ghostNode = game.getGhostCurrentNodeIndex(ghosts[i]);
				distances[i] = game.getShortestPathDistance(ghostNode, lastPowerPill,
						game.getGhostLastMoveMade(ghosts[i]));
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
			} else if (distances[i] < secondMinDist) {
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
				if (nextNode == -1) break;
				prev = current;
				current = nextNode;
				localVisited.add(current);
			}

			if (current != -1 && game.isJunction(current) && current != startJunction) {
				result[count++] = current;
				if (count == 3) break;
			}
		}

		return result;
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
}
