package es.ucm.fdi.ici.c2526.practica3.grupoYY.ghosts.actions;

import java.awt.Color;

import es.ucm.fdi.ici.rules.RulesAction;
import jess.Fact;
import jess.JessException;
import jess.Value;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

public class RunAwayAction implements RulesAction {

	GHOST ghost;

	// PACMAN --> Si fantasma comestible, huye del pacman normal
	// POWERPILL --> Si Pacman cerca de una PP huir del Pacman o hacia el nodo mas
	// lejano
	// SCATTER --> Alejarse de otro fantasma comestible
	// LASTPOWERPILL --> Alejarse de la power pill si es la ultima

	enum STRATEGY {
		PACMAN, SCATTER, ALONE, LASTPOWERPILL
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
		if (game.doesGhostRequireAction(ghost)) // if it requires an action
		{
			switch (runAwayStrategy) {
			case PACMAN:
				return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
						game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
			case SCATTER:
				return scatterMove(game, ghost);
			case ALONE:
				GameView.addPoints(game, Color.YELLOW,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), getFarthestNodeFromPacman(game)));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
						getFarthestNodeFromPacman(game), game.getGhostLastMoveMade(ghost), DM.PATH);
			case LASTPOWERPILL:
				GameView.addPoints(game, Color.YELLOW,
						game.getShortestPath(game.getGhostCurrentNodeIndex(ghost), getFarthestNodeFromPowerPill(game)));
				return game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
						getFarthestNodeFromPowerPill(game), game.getGhostLastMoveMade(ghost), DM.PATH);
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

	private int getFarthestNodeFromPowerPill(Game game) {
		int[] powerPills = game.getActivePowerPillsIndices();
		int lastPowerPill = powerPills[powerPills.length - 1];
		int[] options = game.getPillIndices();

		return game.getFarthestNodeIndexFromNodeIndex(lastPowerPill, options, DM.PATH);
	}

	private int getFarthestNodeFromPacman(Game game) {
		int posPacman = game.getPacmanCurrentNodeIndex();
		int[] options = game.getPillIndices();

		return game.getFarthestNodeIndexFromNodeIndex(posPacman, options, DM.PATH);
	}

	private MOVE scatterMove(Game game, GHOST ghost) {

		int dist = 0, minDist = Integer.MAX_VALUE, posNearNotEdible = -1;
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);

		for (GHOST g : GHOST.values()) {
			if (g != ghost && game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(ghost));
				if (dist < minDist) {
					minDist = dist;
					posNearNotEdible = posGhost;
				}
			}
		}
		return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost), posNearNotEdible,
				game.getGhostLastMoveMade(ghost), DM.PATH);

	}
}
