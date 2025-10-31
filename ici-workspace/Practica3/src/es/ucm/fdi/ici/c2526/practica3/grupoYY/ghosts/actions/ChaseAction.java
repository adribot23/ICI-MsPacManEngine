package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.GhostConstants;
import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class ChaseAction implements RulesAction {

	GHOST ghost;

	// PACMAN --> Perseguir Pacman normal
	// JUNCTION --> Perseguir siguiente cruce del Pacman
	// PILL --> Perseguir Pill mas cercana a Pacman
	// GHOST --> Huir hacia fantasma no comestible mas cercano
	// POWERPILL --> Perseguir ultima power pill por dos caminos distintos

	enum STRATEGY {
		PACMAN, JUNCTION, PILL, GHOST, POWERPILL
	};

	STRATEGY chaseStrategy;

	public ChaseAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public void parseFact(Fact actionFact) {
		try {
			Value value = actionFact.getSlotValue("chasestrategy");
			if (value == null)
				return;
			String strategyValue = value.stringValue(null);
			chaseStrategy = STRATEGY.valueOf(strategyValue);
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MOVE execute(Game game) {
		if (game.doesGhostRequireAction(ghost)) // if it requires an action
		{
			switch (chaseStrategy) {
				case PACMAN:
					GameView.addPoints(game, Color.RED,
							game.getShortestPath(game.getGhostCurrentNodeIndex(ghost),
									game.getPacmanCurrentNodeIndex()));
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
				case JUNCTION:
					GameView.addPoints(game, Color.BLUE,
							game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), nearestJunction(game)));
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestJunction(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case PILL:
					GameView.addPoints(game, Color.YELLOW,
							game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), nearestPillToPacman(game)));
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestPillToPacman(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case GHOST:
					GameView.addLines(game, Color.ORANGE, game.getGhostCurrentNodeIndex(ghost),
							nearestNotEdibleGhost(game));
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestNotEdibleGhost(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case POWERPILL:
					return chaseLastPowerPill(game);
				default:
					throw new IllegalArgumentException("Unexpected value: " + chaseStrategy.toString());
			}
		}
		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return ghost + "chases";
	}

	private int nearestNotEdibleGhost(Game game) {
		int minDist = Integer.MAX_VALUE;
		int posNearNotEdible = -1;

		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);
		if (posCurrGhost == -1)
			return -1;

		for (GHOST g : GHOST.values()) {
			if (g != ghost && !game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				if (posGhost != -1) {
					int dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(g));
					if (dist < minDist) {
						minDist = dist;
						posNearNotEdible = posGhost;
					}
				}
			}
		}

		return posNearNotEdible;
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
					// prefer going forward (not back to prev)
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

	private int nearestJunction(Game game) {
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

	private int nearestPillToPacman(Game game) {
		Queue<Integer> q = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();

		int pacmanPos = game.getPacmanCurrentNodeIndex();
		q.add(pacmanPos);
		visited.add(pacmanPos);

		while (!q.isEmpty()) {
			int current = q.poll();

			if (isPillNode(game, current)) {
				return current;
			}

			for (MOVE m : MOVE.values()) {
				int next = game.getNeighbour(current, m);
				if (next != -1 && !visited.contains(next)) {
					q.add(next);
					visited.add(next);
				}
			}
		}
		return -1;
	}

	private boolean isPillNode(Game game, int node) {
		for (int p : game.getActivePillsIndices())
			if (p == node)
				return true;
		for (int pp : game.getActivePowerPillsIndices())
			if (pp == node)
				return true;
		return false;
	}

    
	// Dynamic assignment of two ghosts to chase the last power pill
	private static GHOST[] assignedChasers = new GHOST[] { null, null };
	private static int assignedPill = -1; // node index of the pill assigned
	private static int[] avoidPath = null;

	private MOVE chaseLastPowerPill(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();

		int targetPill = powerPills[powerPills.length - 1];
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);
		// Reset assignment if the assigned pill is no longer active
		boolean assignedPillStillActive = false;
		if (assignedPill != -1) {
			for (int pp : powerPills) {
				if (pp == assignedPill) {
					assignedPillStillActive = true;
					break;
				}
			}
		}
		if (!assignedPillStillActive) {
			assignedChasers[0] = null;
			assignedChasers[1] = null;
			assignedPill = -1;
			avoidPath = null;
		}

		// If assignment not set for this pill, compute two closest ghosts and lock assignment
		if (assignedPill != targetPill) {
			// compute distances from each alive ghost to targetPill
			java.util.List<java.util.AbstractMap.SimpleEntry<GHOST, Integer>> dists =
					new java.util.ArrayList<>();
			for (GHOST g : GHOST.values()) {
				int gpos = game.getGhostCurrentNodeIndex(g);
				if (gpos == -1 || game.getGhostLairTime(g) > 0)
					continue;
				int d = game.getShortestPathDistance(gpos, targetPill, game.getGhostLastMoveMade(g));
				dists.add(new java.util.AbstractMap.SimpleEntry<>(g, d));
			}
			// sort by distance
			dists.sort((a, b) -> Integer.compare(a.getValue(), b.getValue()));

			assignedChasers[0] = dists.size() > 0 ? dists.get(0).getKey() : null;
			assignedChasers[1] = dists.size() > 1 ? dists.get(1).getKey() : null;
			assignedPill = targetPill;
			// prepare avoidPath as shortest path from first assigned chaser if available
			if (assignedChasers[0] != null) {
				int firstPos = game.getGhostCurrentNodeIndex(assignedChasers[0]);
				avoidPath = game.getShortestPath(firstPos, targetPill, game.getGhostLastMoveMade(assignedChasers[0]));
				GameView.addPoints(game, Color.RED, avoidPath);
			}
		}

		// If this ghost is not assigned to chase the power pill, perform fallback behavior
		// instead of staying neutral. By default we fall back to chasing Pacman.
		if (assignedChasers[0] != ghost && assignedChasers[1] != ghost) {
			// Fallback: chase Pacman (can be changed to SCATTER or other behavior later)
			return game.getApproximateNextMoveTowardsTarget(ghostPos, game.getPacmanCurrentNodeIndex(), lastMove, DM.PATH);
		}

		// If this is the primary chaser
		if (assignedChasers[0] == ghost) {
			// go straight to the pill
			GameView.addPoints(game, Color.YELLOW, game.getShortestPath(ghostPos, targetPill, lastMove));
			return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
		}

		// Secondary chaser: try to find an alternative path that avoids the primary's path
		int[] alternativePath = findAlternativePath(game, ghostPos, targetPill, avoidPath == null ? new int[0] : avoidPath);
		GameView.addPoints(game, Color.WHITE, alternativePath);

		int nextNode = alternativePath.length > 0 ? alternativePath[0] : -1;
		for (int i = 0; i < alternativePath.length - 1; i++) {
			if (alternativePath[i] == ghostPos) {
				nextNode = alternativePath[i + 1];
				break;
			}
		}
		if (nextNode == -1)
			return MOVE.NEUTRAL;
		return game.getNextMoveTowardsTarget(ghostPos, nextNode, lastMove, DM.PATH);
	}

	private int[] findAlternativePath(Game game, int start, int target, int[] avoidPath) {
		// Convertir avoidPath en Set para bfs
		Set<Integer> avoidSet = new HashSet<>();
		for (int node : avoidPath) {
			avoidSet.add(node);
		}

		Queue<int[]> queue = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();
		int[] parent = new int[game.getNumberOfNodes()];

		queue.add(new int[] { start, 0, -1 });
		visited.add(start);

		// BFS con contador de intersecciones
		while (!queue.isEmpty()) {
			int[] current = queue.poll();
			int node = current[0];
			int intersections = current[1];
			parent[node] = current[2];

			if (node == target) {
				// Reconstruir el camino
				LinkedList<Integer> path = new LinkedList<>();
				int curr = node;
				while (curr != -1) {
					path.addFirst(curr);
					curr = parent[curr];
				}
				int[] resultPath = path.stream().mapToInt(Integer::intValue).toArray();
				GameView.addPoints(game, Color.WHITE, resultPath);
				return resultPath;
			}

			// Expandir vecinos
			for (MOVE move : game.getPossibleMoves(node)) {
				int next = game.getNeighbour(node, move);
				if (next != -1 && !visited.contains(next)) {
					int newIntersections = intersections;
					if (avoidSet.contains(next)) {
						newIntersections++;
						if (newIntersections > GhostConstants.MAX_INTERSECTIONS)
							continue;
					}
					visited.add(next);
					queue.add(new int[] { next, newIntersections, node });
				}
			}
		}

		// Si no se encuentra camino alternativo, usar el camino directo
		return game.getShortestPath(start, target, game.getGhostLastMoveMade(ghost));
	}
}