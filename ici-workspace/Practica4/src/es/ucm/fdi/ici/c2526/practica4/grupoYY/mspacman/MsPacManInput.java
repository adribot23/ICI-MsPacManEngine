package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.ici.cbr.CBRInput;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManInput extends CBRInput {

	public MsPacManInput(Game game) {
		super(game);

	}

	Integer nearestGhost;
	Boolean edible;
	Integer nearestPPill;
	Integer score;
	Integer time;

	List<Integer> listPosGhost;
	List<Integer> ghostDistances;
	List<MOVE> ghostsLastMoves;

	Integer numEdibles;
	Integer ghostEdibleTime;

	Integer pacmanPos;
	MOVE pacmanLastMove;

	@Override
	public void parseInput() {
		computeNearestGhost(game);
		computeNearestPPill(game);
		time = game.getTotalTime();
		score = game.getScore();

		computeLists(game);

		pacmanPos = game.getPacmanCurrentNodeIndex();
		pacmanLastMove = game.getPacmanLastMoveMade();
	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();
		description.setEdibleGhost(edible);
		description.setNearestGhost(nearestGhost);
		description.setNearestPPill(nearestPPill);
		description.setScore(score);
		description.setTime(time);
		
		description.setListPosGhost(listPosGhost);
		description.setGhostDistances(ghostDistances);
		description.setGhostsLastMoves(ghostsLastMoves);
		
		description.setNumEdibles(numEdibles);
		description.setGhostEdibleTime(ghostEdibleTime);
		
		description.setPacmanPos(pacmanPos);
		description.setPacmanLastMove(pacmanLastMove);
		
		CBRQuery query = new CBRQuery();
		query.setDescription(description);
		return query;
	}

	private void computeNearestGhost(Game game) {
		nearestGhost = Integer.MAX_VALUE;
		edible = false;
		GHOST nearest = null;
		for (GHOST g : GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			int distance;
			if (pos != -1)
				distance = (int) game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			else
				distance = Integer.MAX_VALUE;
			if (distance < nearestGhost) {
				nearestGhost = distance;
				nearest = g;
			}
		}
		if (nearest != null)
			edible = game.isGhostEdible(nearest);
	}

	private void computeNearestPPill(Game game) {
		nearestPPill = Integer.MAX_VALUE;
		for (int pos : game.getPowerPillIndices()) {
			int distance = (int) game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			if (distance < nearestPPill)
				nearestPPill = distance;
		}
	}

	public void computeLists(Game game) {
		listPosGhost = new ArrayList<>();
		ghostDistances = new ArrayList<>();
		ghostsLastMoves = new ArrayList<>();
		numEdibles = 0;
		ghostEdibleTime = Integer.MAX_VALUE;

		for (GHOST g : GHOST.values()) {
			int pos = game.getGhostCurrentNodeIndex(g);
			listPosGhost.add(pos);
			int distance;
			if (pos != -1)
				distance = (int) game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			else
				distance = Integer.MAX_VALUE;
			ghostDistances.add(distance);
			MOVE m = game.getGhostLastMoveMade(g);
			if (m == null)
				ghostsLastMoves.add(MOVE.NEUTRAL);
			else
				ghostsLastMoves.add(m);

			if (game.isGhostEdible(g)) {
				numEdibles++;
				int edTime = game.getGhostEdibleTime(g);
				if (edTime < ghostEdibleTime)
					ghostEdibleTime = edTime;
			}
		}

	}
}
