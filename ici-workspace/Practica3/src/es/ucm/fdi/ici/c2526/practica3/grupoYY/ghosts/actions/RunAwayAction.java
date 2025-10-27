package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class RunAwayAction implements RulesAction {

	GHOST ghost;

	// PACMAN --> Si fantasma comestible, huye del pacman normal
	// POWERPILL --> Si Pacman cerca de una PP huir del Pacman o hacia el nodo mas
	// lejano

	enum STRATEGY {
		PACMAN, POWERPILL, SCATTER, LASTPOWERPILL
	};

	STRATEGY runAwayStrategy;

	public RunAwayAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public void parseFact(Fact actionFact) {
		try {
			Value value = actionFact.getSlotValue("runawaystrategy");
			if (value == null)
				return;
			String strategyValue = value.stringValue(null);
			runAwayStrategy = STRATEGY.valueOf(strategyValue);
		} catch (JessException e) {
			e.printStackTrace();
		}

	}

	@Override
	public MOVE execute(Game game) {
		/*****************************************************************************/
		// Here you can use the runAwayStrategy value obtained from the asserted fact
		/*****************************************************************************/

		if (game.doesGhostRequireAction(ghost)) // if it requires an action
		{
			switch (runAwayStrategy) {
			case PACMAN:
				return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
			case POWERPILL:
				return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
						getNearPowerPill(game), game.getGhostLastMoveMade(ghost), DM.PATH);
			case SCATTER:
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
						scatterMove(game, ghost), game.getGhostLastMoveMade(ghost), DM.PATH);
			case LASTPOWERPILL:
				return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
						getNearPowerPill(game), game.getGhostLastMoveMade(ghost), DM.PATH);
			default:
				throw new IllegalArgumentException("Unexpected value: " + runAwayStrategy.toString());
			}
		}

		return MOVE.NEUTRAL;
	}

	@Override
	public String getActionId() {
		return ghost + "runsAway";
	}

	private int getNearPowerPill(Game game) {

		int[] powerPills = game.getActivePowerPillsIndices();
		int nearPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;
		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		for (int pp : powerPills) {
			int distance = game.getShortestPathDistance(posPacman, pp, lastMove);

			if (distance < minDistance) {
				minDistance = distance;
				nearPowerPill = pp;
			}
		}
		return nearPowerPill;
	}

	private int scatterMove(Game game, GHOST ghost) {
		int current = game.getGhostCurrentNodeIndex(ghost);
		int pacman = game.getPacmanCurrentNodeIndex();

		// Recolectamos nodos “seguros” (distancia suficiente de Pacman y otros
		// comestibles)
		int[] allNodes = game.getPillIndices();
		int safestNode = current;
		int maxScore = Integer.MIN_VALUE;

		for (int node : allNodes) {
			int distToPacman = game.getShortestPathDistance(node, pacman, game.getGhostLastMoveMade(ghost));
			int minDistToEdibleGhost = Integer.MAX_VALUE;

			for (GHOST g : GHOST.values()) {
				if (g != ghost && game.isGhostEdible(g)) {
					int d = game.getShortestPathDistance(node, game.getGhostCurrentNodeIndex(g),
							game.getGhostLastMoveMade(g));
					if (d < minDistToEdibleGhost)
						minDistToEdibleGhost = d;
				}
			}

			// Calculamos una “puntuación” simple: más lejos de Pacman y de otros fantasmas
			// comibles
			int score = distToPacman + minDistToEdibleGhost;
			if (score > maxScore) {
				maxScore = score;
				safestNode = node;
			}
		}

		// Devuelve el nodo más seguro
		return safestNode;
	}
}
