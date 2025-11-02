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
		PACMAN, FIRSTJUNCTION, SECONDJUNCTION, THIRDJUNCTION, NEARESTTARGET, GHOST, POWERPILL, SEMIEDIBLE
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
			int[] junctions = nextPacmanJunctions(game);
			switch (chaseStrategy) {
			case PACMAN:
				GameView.addPoints(game, Color.RED,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex()));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
			case FIRSTJUNCTION:
				GameView.addPoints(game, Color.BLUE,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), junctions[0]));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), junctions[0],
						game.getGhostLastMoveMade(ghost), DM.PATH);
			case SECONDJUNCTION:
				GameView.addPoints(game, Color.GREEN,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), junctions[1]));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), junctions[1],
						game.getGhostLastMoveMade(ghost), DM.PATH);
			case THIRDJUNCTION:
				GameView.addPoints(game, Color.MAGENTA,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), junctions[2]));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), junctions[2],
						game.getGhostLastMoveMade(ghost), DM.PATH);
			case NEARESTTARGET:
				int target = nearestTarget(game, junctions);
				GameView.addPoints(game, Color.GRAY,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), target));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), target,
						game.getGhostLastMoveMade(ghost), DM.PATH);
			case GHOST:
				GameView.addLines(game, Color.PINK, game.getGhostCurrentNodeIndex(ghost), nearestNotEdibleGhost(game));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
						nearestNotEdibleGhost(game), game.getGhostLastMoveMade(ghost), DM.PATH);
			case POWERPILL:
				return chaseLastPowerPill(game);
			case SEMIEDIBLE:
				GameView.addLines(game, Color.RED, game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex());
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getPacmanCurrentNodeIndex(),
						game.getGhostLastMoveMade(ghost), DM.PATH);
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
					int dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(ghost));
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
	    result[0] = startJunction;
	    int count = 1;

	   
	    for (MOVE move : game.getPossibleMoves(startJunction)) {
	        if (move == MOVE.NEUTRAL)
	            continue;

	        int current = game.getNeighbour(startJunction, move);
	        boolean pacmanInPath = false;

	       
	        while (current != -1 && !game.isJunction(current)) {
	            if (current == pacman) {
	                pacmanInPath = true; 
	                break;
	            }
	            int next = game.getNeighbour(current, move);
	            current = next;
	        }

	        
	        if (pacmanInPath)
	            continue;

	       
	        if (current != -1 && current != startJunction) {
	            result[count++] = current;
	            if (count == 3)
	                break;
	        }
	    }

	    return result;
	}
	
	private static int[] avoidPath = null;
	private static GHOST firstGhost = null;

	private MOVE chaseLastPowerPill(Game game) {
		
		int[] powerPills = game.getActivePowerPillsIndices();

		int targetPill = powerPills[powerPills.length - 1];
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		MOVE lastMove = game.getGhostLastMoveMade(ghost);
		
		if (game.getNumberOfActivePowerPills() != 1 || game.wasPacManEaten()) {
			firstGhost = null;
			avoidPath = null;
		}

		// Si es el primer fantasma y aún no se ha establecido
		if (firstGhost == null) {
			firstGhost = ghost;
			if (avoidPath == null) {
				avoidPath = game.getShortestPath(ghostPos, targetPill, lastMove);
				GameView.addPoints(game, Color.RED, avoidPath);
			}
			return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
		}

		// Si es el primer fantasma, vamos hacia la PP sin actualizar el avoidPath
		if (firstGhost == ghost) {
			GameView.addPoints(game, Color.YELLOW, game.getShortestPath(ghostPos, targetPill, lastMove));
			return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
		}

		// Si no es el primer fantasma, buscamos un camino alternativo hacia la PP sin
		// pasar por el avoidPath
		if (firstGhost != ghost) {
			int[] alternativePath = findAlternativePath(game, ghostPos, targetPill, avoidPath);
			GameView.addPoints(game, Color.WHITE, alternativePath);

			int nextNode = alternativePath[0];
			for (int i = 0; i < alternativePath.length - 1; i++) {
				if (alternativePath[i] == ghostPos) {
					nextNode = alternativePath[i + 1];
					break;
				}
			}
			return game.getNextMoveTowardsTarget(ghostPos, nextNode, lastMove, DM.PATH);
		}
		return game.getApproximateNextMoveTowardsTarget(ghostPos, targetPill, lastMove, DM.PATH);
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

		return game.getShortestPath(start, target, game.getGhostLastMoveMade(ghost));
	}

	private int nearestTarget(Game game, int[] junctions) {
		int ghostPos = game.getGhostCurrentNodeIndex(ghost);
		int pacmanPos = game.getPacmanCurrentNodeIndex();

		int minDist = game.getShortestPathDistance(ghostPos, pacmanPos, game.getGhostLastMoveMade(ghost));
		int nearestTarget = pacmanPos;

		for (int junction : junctions) {
			if (junction != -1) {
				int dist = game.getShortestPathDistance(ghostPos, junction, game.getGhostLastMoveMade(ghost));
				if (dist < minDist) {
					minDist = dist;
					nearestTarget = junction;
				}
			}
		}

		return nearestTarget;
	}
	
}