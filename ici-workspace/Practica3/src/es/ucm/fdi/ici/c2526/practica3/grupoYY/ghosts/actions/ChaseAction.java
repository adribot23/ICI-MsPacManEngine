package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import java.awt.Color;
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
import pacman.game.GameView;

public class ChaseAction implements RulesAction {

	GHOST ghost;

	// PACMAN --> Perseguir Pacman normal
	// JUNCTION --> Perseguir siguiente cruce del Pacman
	// PILL --> Perseguir Pill mas cercana a Pacman
	// EDIBLE --> Perseguir fantasma comestible mas cercano a Pacman
	// CIRCLE_POWERPILL --> Dar vueltas alrededor de la última power pill
	enum STRATEGY {
		PACMAN, JUNCTION, PILL, GHOST, EDIBLE, POWERPILL, CIRCLE_POWERPILL
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
				case GHOST:
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestNotEdibleGhost(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case EDIBLE:
					return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							nearestGhostToPacman(game), game.getGhostLastMoveMade(ghost), DM.PATH);
				case POWERPILL:
					return chaseLastPowerPill(game);
				case CIRCLE_POWERPILL:
					return circleAroundLastPowerPill(game);
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
			return -1; // fantasma actual en la guarida

		for (GHOST g : GHOST.values()) {
			if (g != ghost && !game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0) { // ignorar fantasmas en guarida
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
	
	private static int[] avoidPath;
	private static GHOST firstGhost = null;
	private boolean isClockwise = true;

	private MOVE chaseLastPowerPill(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();

		int targetPill = powerPills[powerPills.length - 1];
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);
		
		// Si es el primer fantasma, guardamos el camino mas cercano a la PP para evitarlo
		if (firstGhost == null) {
			firstGhost = ghost;
			avoidPath = game.getShortestPath(ghostPos, targetPill, lastMove);
			GameView.addPoints(game, Color.CYAN, avoidPath);
			return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
		}
		
		// Si es el primer fantasma, vamos hacia la PP
		if (firstGhost == ghost) {
			GameView.addLines(game, Color.RED, ghostPos, targetPill);
			return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
		}
		
		// Si no es el primer fantasma, buscamos un camino alternativo hacia la PP sin pasar por el avoidPath
		if (firstGhost != ghost && firstGhost != null) {
			// No esta bien puesto, solo esta asi para comprobar cuando entra
			GameView.addLines(game, Color.RED, ghostPos, targetPill);
			return game.getApproximateNextMoveTowardsTarget(ghostPos, game.getGhostInitialNodeIndex(), lastMove, DM.PATH);
		}

		return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
	}
	/*
	private int[] findAlternativePath(Game game, int start, int target, int[] avoidPath) {
		// BFS shortest path from start to target, avoiding nodes in avoidPath
		LinkedList<Integer> empty = new LinkedList<>();
		if (start == target) {
			empty.add(start);
			return empty;
		}

		Queue<Integer> q = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();
		java.util.Map<Integer, Integer> parent = new java.util.HashMap<>();

		q.add(start);
		visited.add(start);

		while (!q.isEmpty()) {
			int curr = q.poll();
			for (MOVE move : MOVE.values()) {
				if (move == MOVE.NEUTRAL)
					continue;
				int next = game.getNeighbour(curr, move);
				if (next == -1)
					continue;
				if (visited.contains(next))
					continue;
				// avoid nodes in avoidPath, but allow target even if it's in avoidPath
				if (avoidPath != null && avoidPath.contains(next) && next != target)
					continue;
				visited.add(next);
				parent.put(next, curr);
				if (next == target) {
					// reconstruct path
					LinkedList<Integer> path = new LinkedList<>();
					int node = target;
					while (node != start) {
						path.addFirst(node);
						node = parent.get(node);
						if (node == 0 && !visited.contains(start))
							break; // safety
					}
					path.addFirst(start);
					return path;
				}
				q.add(next);
			}
		}

		return new LinkedList<>();
	}
	*/

	private MOVE circleAroundLastPowerPill(Game game) {
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);

		// Asumimos que ya hemos pasado por la power pill
		MOVE[] possibleMoves = game.getPossibleMoves(ghostPos, lastMove);
		// Mantener el sentido actual (isClockwise)
		for (MOVE move : possibleMoves) {
			if (isClockwise && isRightTurn(lastMove, move) || !isClockwise && isLeftTurn(lastMove, move)) {
				return move;
			}
		}
		// Si no podemos girar, seguimos recto si es posible
		for (MOVE move : possibleMoves) {
			if (move == lastMove) {
				return move;
			}
		}
		return MOVE.NEUTRAL;
	}

	// Determina si el nuevo movimiento es un giro a la derecha respecto al último
	// movimiento
	private boolean isRightTurn(MOVE lastMove, MOVE newMove) {
		switch (lastMove) {
			case UP:
				return newMove == MOVE.RIGHT;
			case RIGHT:
				return newMove == MOVE.DOWN;
			case DOWN:
				return newMove == MOVE.LEFT;
			case LEFT:
				return newMove == MOVE.UP;
			default:
				return false;
		}
	}

	// Determina si el nuevo movimiento es un giro a la izquierda respecto al último
	// movimiento
	private boolean isLeftTurn(MOVE lastMove, MOVE newMove) {
		switch (lastMove) {
			case UP:
				return newMove == MOVE.LEFT;
			case LEFT:
				return newMove == MOVE.DOWN;
			case DOWN:
				return newMove == MOVE.RIGHT;
			case RIGHT:
				return newMove == MOVE.UP;
			default:
				return false;
		}
	}
}