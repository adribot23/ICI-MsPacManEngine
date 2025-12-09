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

	// Atributos pacman
	Integer pacmanLives;
	Integer pacmanPos;
	MOVE pacmanLastMove;

	// Atributos fantasmas
	Integer nearestGhost;
	Boolean edible;
	Integer numEdibles;
	Integer ghostEdibleTime;
	List<Integer> listPosGhost;
	List<Integer> ghostDistances;
	List<MOVE> ghostsLastMoves;

	// Atributos pills
	Integer nearestPill;
	Integer nearestPPill;
	Integer remainingPills;
	Integer remainingPowerPills;

	// Otros atributos
	Integer score;
	Integer time;

	@Override
	public void parseInput() {

		pacmanLives = game.getPacmanNumberOfLivesRemaining();
		pacmanPos = game.getPacmanCurrentNodeIndex();
		pacmanLastMove = game.getPacmanLastMoveMade();

		computeNearestGhost(game);
		computeLists(game); // numEdibles, ghostEdibleTime, listPosGhost, ghostDistances, ghostsLastMoves

		computeNearestPill(game);
		computeNearestPPill(game);

		remainingPills = game.getNumberOfActivePills();
		remainingPowerPills = game.getNumberOfActivePowerPills();

		score = game.getScore();
		time = game.getTotalTime();

	}

	@Override
	public CBRQuery getQuery() {
		MsPacManDescription description = new MsPacManDescription();

		description.setPacmanLives(pacmanLives);
		description.setPacmanPos(pacmanPos);
		description.setPacmanLastMove(pacmanLastMove);

		description.setNearestGhost(nearestGhost);
		description.setEdibleGhost(edible);
		description.setNumEdibles(numEdibles);
		description.setGhostEdibleTime(ghostEdibleTime);

		description.setListPosGhost(new MyIntegerListType(listPosGhost));
		description.setGhostDistances(new MyIntegerListType(ghostDistances));
		description.setGhostsLastMoves(new MyMOVEListType(ghostsLastMoves));

		
		description.setNearestPill(nearestPill);
		description.setNearestPPill(nearestPPill);
		description.setRemainingPills(remainingPills);
		description.setRemainingPowerPills(remainingPowerPills);

		
		description.setScore(score);
		description.setTime(time);

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
		for (int pos : game.getActivePowerPillsIndices()) {
			int distance = (int) game.getDistance(game.getPacmanCurrentNodeIndex(), pos, DM.PATH);
			if (distance < nearestPPill)
				nearestPPill = distance;
		}
	}

	private void computeNearestPill(Game game) {
		nearestPill = Integer.MAX_VALUE;
		int pacman = game.getPacmanCurrentNodeIndex();
		for (int pillIndex : game.getActivePillsIndices()) {
			int distance = (int) game.getDistance(pacman, pillIndex, DM.PATH);
			if (distance < nearestPill) {
				nearestPill = distance;
			}
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
