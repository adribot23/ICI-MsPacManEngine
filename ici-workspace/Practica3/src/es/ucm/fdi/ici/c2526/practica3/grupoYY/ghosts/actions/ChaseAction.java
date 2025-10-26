package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class ChaseAction implements RulesAction {

	GHOST ghost;

	// PACMAN --> Perseguir Pacman normal
	// JUNCTION --> Perseguir siguiente cruce del Pacman
	// PILL --> Perseguir Pill mas cercana a Pacman
	// EDIBLE --> Perseguir fantasma comestible mas cercano a Pacman
	enum STRATEGY {
		PACMAN, JUNCTION, PILL, EDIBLE
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
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
				case JUNCTION:
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nextPacmanJunction(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case PILL:
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestPillToPacman(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case EDIBLE:
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestGhostToPacman(game), game.getGhostLastMoveMade(ghost), DM.PATH);
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

	private int nearestGhostToPacman(Game game) {
		int dist = 0, minDist = Integer.MAX_VALUE, posNearEdible = 0;
		int posPacman = game.getPacmanCurrentNodeIndex();
		for (GHOST g : GHOST.values()) {
			if (g != ghost && game.isGhostEdible(g)) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				dist = game.getShortestPathDistance(posGhost, posPacman, game.getGhostLastMoveMade(g));
				if (dist < minDist) {
					minDist = dist;
					posNearEdible = posGhost;
				}
			}
		}
		return posNearEdible;
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
/*
	private MOVE getCircularMove(Game game, boolean clockwise) {
		int[] powerPills = game.getActivePowerPillsIndices();

		// Get the last power pill (highest index)
		int targetPill = powerPills[powerPills.length - 1];
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);

		// Get all possible moves except going backwards
		MOVE[] possibleMoves = game.getPossibleMoves(ghostPos, lastMove);

		if (possibleMoves.length == 0) {
			return MOVE.NEUTRAL;
		}

		// Calculate the best move that maintains circular motion
		double bestAngle = Double.MAX_VALUE;
		MOVE bestMove = possibleMoves[0];

		// Current position relative to power pill
		double currentX = game.getNodeXCood(ghostPos) - game.getNodeXCood(targetPill);
		double currentY = game.getNodeYCood(ghostPos) - game.getNodeYCood(targetPill);
		double currentAngle = Math.atan2(currentY, currentX);

		for (MOVE move : possibleMoves) {
			int nextPos = game.getNeighbour(ghostPos, move);
			if (nextPos == -1)
				continue;

			// Next position relative to power pill
			double nextX = game.getNodeXCood(nextPos) - game.getNodeXCood(targetPill);
			double nextY = game.getNodeYCood(nextPos) - game.getNodeYCood(targetPill);
			double nextAngle = Math.atan2(nextY, nextX);

			// Calculate angle difference
			double angleDiff = nextAngle - currentAngle;
			if (angleDiff > Math.PI)
				angleDiff -= 2 * Math.PI;
			if (angleDiff < -Math.PI)
				angleDiff += 2 * Math.PI;

			// For clockwise movement we want positive angle differences
			// For counter-clockwise movement we want negative angle differences
			double score = clockwise ? -angleDiff : angleDiff;

			if (score < bestAngle) {
				bestAngle = score;
				bestMove = move;
			}
		}

		return bestMove;
	}
	*/
}
