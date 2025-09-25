package es.ucm.fdi.ici.c2526.practica1.grupoF;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

	int minDistance, distance, limit = 20;
	GHOST nearGhost;
	Queue<Integer> q = new LinkedList<>();
	Set<Integer> s = new HashSet<>();

	@Override
	public MOVE getMove(Game game, long timeDue) {

		nearGhost = getNearestChasingGhost(game);

		if (nearGhost != null) {
			return game.getApproximateNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);
		}

		nearGhost = getNearestEdibleGhost(game);

		if (nearGhost != null) {
			return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(nearGhost), game.getPacmanLastMoveMade(), Constants.DM.PATH);
		}

		return game.getApproximateNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), getNearestPill(game),
				game.getPacmanLastMoveMade(), Constants.DM.PATH);
	}

	private GHOST getNearestChasingGhost(Game game) {
		minDistance = Integer.MAX_VALUE;
		nearGhost = null;
		for (GHOST ghostType : GHOST.values()) {
			if (game.getGhostLairTime(ghostType) == 0 && !game.isGhostEdible(ghostType)) {
				distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghostType), game.getPacmanLastMoveMade());
				if (minDistance > distance && distance < limit) {
					minDistance = distance;
					nearGhost = ghostType;
				}
			}
		}
		return nearGhost;
	}

	private GHOST getNearestEdibleGhost(Game game) {
		minDistance = Integer.MAX_VALUE;
		nearGhost = null;
		for (GHOST ghostType : GHOST.values()) {
			if (game.isGhostEdible(ghostType)) {
				distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghostType), game.getPacmanLastMoveMade());
				if (minDistance > distance && distance < limit) {
					minDistance = distance;
					nearGhost = ghostType;
				}
			}
		}
		return nearGhost;

	}

	int getNearestPill(Game game) {
		q.clear();
		s.clear();

		int current, index;

		q.add(game.getPacmanCurrentNodeIndex());
		s.add(game.getPacmanCurrentNodeIndex());

		while (!q.isEmpty()) {

			current = q.poll();

			if (game.isPillStillAvailable(current) || game.isPowerPillStillAvailable(current))
				return current;

			for (MOVE m : MOVE.values()) {
				index = game.getNeighbour(current, m);
				if (index != -1 && !s.contains(index)) {
					q.add(index);
					s.add(index);
				}
			}
		}

		return -1;

	}

	public String getName() {
		return "MsPacManNeutral";
	}

}
