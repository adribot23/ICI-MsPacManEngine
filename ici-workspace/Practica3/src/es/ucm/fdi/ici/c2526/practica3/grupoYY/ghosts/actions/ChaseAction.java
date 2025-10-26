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
	// CIRCLE_POWERPILL --> Dar vueltas alrededor de la última power pill
	enum STRATEGY {
		PACMAN, JUNCTION, PILL, GHOST, EDIBLE, CIRCLE_POWERPILL
	};

	STRATEGY chaseStrategy;
	private boolean isClockwise = true; // Por defecto empieza en sentido horario
	private static GHOST firstCirclingGhost = null; // Para identificar el primer fantasma que circula
	private int lastPowerPillIndex = -1; // Para detectar cuando pasamos por la power pill

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
		int dist = 0, minDist = Integer.MAX_VALUE, posNearNotEdible = 0;
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);
		for (GHOST g : GHOST.values()) {
			if (g != ghost && !game.isGhostEdible(g)) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				if (posGhost != -1 &&  posCurrGhost!= -1) {
					dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(g));
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

	private MOVE circleAroundLastPowerPill(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();

		// Obtener la última power pill activa
		int targetPill = powerPills[powerPills.length - 1];
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);

		// Si es el primer fantasma que va a circular alrededor de la power pill
		if (firstCirclingGhost == null) {
			firstCirclingGhost = ghost;
		}
		// Si es el segundo fantasma y va en el mismo sentido que el primero, cambiar
		// dirección
		else if (firstCirclingGhost != ghost && game.getGhostLastMoveMade(firstCirclingGhost) == lastMove) {
			isClockwise = !isClockwise;
		}

		// Detectar si acabamos de pasar por la power pill
		if (ghostPos == targetPill) {
			lastPowerPillIndex = targetPill;
			// El siguiente movimiento determinará el sentido
			return lastMove;
		}

		// Si ya pasamos por la power pill, ajustar el sentido según el movimiento
		if (lastPowerPillIndex == targetPill) {
			MOVE[] possibleMoves = game.getPossibleMoves(ghostPos, lastMove);
			// Preferir giro a la derecha para horario, izquierda para antihorario
			for (MOVE move : possibleMoves) {
				if (isClockwise && isRightTurn(lastMove, move) || !isClockwise && isLeftTurn(lastMove, move)) {
					return move;
				}
			}
			// Si no podemos girar en la dirección deseada, tomar cualquier movimiento
			// válido
			return possibleMoves.length > 0 ? possibleMoves[0] : MOVE.NEUTRAL;
		}

		// Si aún no hemos llegado a la power pill, ir hacia ella
		return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
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