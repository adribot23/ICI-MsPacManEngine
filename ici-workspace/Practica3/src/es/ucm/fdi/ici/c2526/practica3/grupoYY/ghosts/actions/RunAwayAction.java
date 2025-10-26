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
    // GHOST --> Si hay algun fantasma no comestible, huir hacia el
    // POWERPILL --> Si Pacman cerca de una PP huir del Pacman o hacia el nodo mas lejano
    enum STRATEGY {PACMAN, GHOST, POWERPILL};
    STRATEGY runAwayStrategy; 
	public RunAwayAction(GHOST ghost) {
		this.ghost = ghost;
	}

	@Override
	public void parseFact(Fact actionFact) {
		try {
			Value value = actionFact.getSlotValue("runawaystrategy");
			if(value == null)
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
		//Here you can use the runAwayStrategy value obtained from the asserted fact
		/*****************************************************************************/
		
		if (game.doesGhostRequireAction(ghost))        //if it requires an action
        {
			switch(runAwayStrategy) {
        	case STRATEGY.PACMAN:
        		return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                        game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH);
			case STRATEGY.GHOST:
        		return game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
        				nearestNotEdibleGhost(game), game.getGhostLastMoveMade(ghost), DM.PATH);
        	case STRATEGY.POWERPILL:
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
	
	private int nearestNotEdibleGhost(Game game) {
		int dist = 0, minDist = Integer.MAX_VALUE, posNearNotEdible = 0;
		int posCurrGhost = game.getGhostCurrentNodeIndex(ghost);
		for(GHOST g : GHOST.values()) {
			if(g != ghost && !game.isGhostEdible(g)) {
				int posGhost = game.getGhostCurrentNodeIndex(g);
				dist = game.getShortestPathDistance(posCurrGhost, posGhost, game.getGhostLastMoveMade(g));
				if(dist < minDist) {
					minDist = dist;
					posNearNotEdible = posGhost;
				}
			}
		}
		return posNearNotEdible;
	}
	
	public int getNearPowerPill(Game game) {

		int[] powerPills = game.getActivePowerPillsIndices();
		int nearPowerPill = -1;
		int minDistance = Integer.MAX_VALUE;
		int posPacman = game.getPacmanCurrentNodeIndex();
		MOVE lastMove = game.getPacmanLastMoveMade();

		for (int pp : powerPills) {
			int distance = game.getShortestPathDistance(posPacman, pp, lastMove);

			// Hacer constante
			if (distance < 30 && distance < minDistance) {
				minDistance = distance;
				nearPowerPill = pp;
			}
		}
		return nearPowerPill;
	}
}
