package es.ucm.fdi.ici.c2526.practica2.grupoYY;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayFromNearestGhost;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayToNearestSafePPAction;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayToNearestSafePillAction;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.ERandomAction;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.transitions.*;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.*;
import es.ucm.fdi.ici.fsm.SimpleState;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.fsm.observers.GraphFSMObserver;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class NearestPillPacMan.
 */
public class MsPacMan extends PacmanController {
	FSM fsm;

	public MsPacMan() {
		setName("MsPacMan XX");

		fsm = new FSM("MsPacMan");

		GraphFSMObserver observer = new GraphFSMObserver(fsm.toString());
		fsm.addObserver(observer);

		// DEFENSA
		
		FSM defenseCFSM = new FSM("DEFENSE");
		GraphFSMObserver c1observer = new GraphFSMObserver(defenseCFSM.toString());
		defenseCFSM.addObserver(c1observer);

		SimpleState DGhost = new SimpleState("RunAwayFromNearestGhost", new DRunAwayFromNearestGhost());
		SimpleState DPowerPill = new SimpleState("RunAwayToNearestSafePPAction", new DRunAwayToNearestSafePPAction());
		SimpleState DPill = new SimpleState("RunAwayToNearestSafePillAction", new DRunAwayToNearestSafePillAction());

		Transition NearestPowerPillisSafeAndMoreThan2GhostsOut = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition NearestPowerPillNotSafe = new NearestPowerPillNotSafe();
		Transition NearestPillNotSafe = new NearestPillNotSafe();
		Transition NearestePowerPillNotSafeButPillYes = new NearestePowerPillNotSafeButPillYes();
		
		defenseCFSM.add(DGhost, NearestPowerPillisSafeAndMoreThan2GhostsOut, DPowerPill);
		defenseCFSM.add(DPowerPill, NearestPowerPillNotSafe, DPill);
		defenseCFSM.add(DPill, NearestPillNotSafe, DGhost);
		defenseCFSM.add(DGhost, NearestePowerPillNotSafeButPillYes, DPill);
		defenseCFSM.add(DPill, NearestPowerPillisSafeAndMoreThan2GhostsOut, DPowerPill);
		
		defenseCFSM.ready(DGhost);

		CompoundState defense = new CompoundState("defense", defenseCFSM);
		
		// ATAQUE

		FSM attackCFSM = new FSM("ATTACK");
		GraphFSMObserver c2observer = new GraphFSMObserver(attackCFSM.toString());
		attackCFSM.addObserver(c2observer);

		SimpleState AEdible = new SimpleState("ChaseNearestEdibleAction", new AChaseNearestEdibleAction());
		SimpleState AGroup = new SimpleState("ChaseSeveralGhostRouteAction", new AChaseSeveralGhostRouteAction());
		SimpleState APill = new SimpleState("RunToNearestPillAction", new ARunToNearestPillAction());

		Transition NearToEdibleGhost = new NearToEdibleGhost();
		Transition OnlyOneFarEdibleGhost = new OnlyOneFarEdibleGhost();
		Transition TwoOrMoreGhostsCloseEachOther = new TwoOrMoreGhostsCloseEachOther();
		Transition GhostEatenOrScatterGhosts = new GhostEatenOrScatterGhosts();
		
		attackCFSM.add(AEdible, OnlyOneFarEdibleGhost, APill);
		attackCFSM.add(APill, NearToEdibleGhost, AEdible);
		attackCFSM.add(AEdible, TwoOrMoreGhostsCloseEachOther, AGroup);
		attackCFSM.add(AGroup, GhostEatenOrScatterGhosts, AEdible);
		
		attackCFSM.ready(AEdible);

		CompoundState attack = new CompoundState("attack", attackCFSM);
		
		// ESTANDAR
		
		FSM standardCFSM = new FSM("STANDARD");
		GraphFSMObserver c3observer = new GraphFSMObserver(standardCFSM.toString());
		standardCFSM.addObserver(c3observer);

		SimpleState EPowerPill = new SimpleState("RunToNearestSafePPAction", new ERunToNearestSafePPAction());
		SimpleState EPill = new SimpleState("RunToNearestSafePillAction", new ERunToNearestSafePillAction());
		SimpleState ESafeZone = new SimpleState("RunToSafeZoneAction", new ERunToSafeZoneAction());
		SimpleState ERandom = new SimpleState("RandomAction", new ERandomAction());

		Transition NotSafeZone = new NotSafeZone();
		Transition ENearestPowerPillisSafeAndMoreThan2GhostsOut = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition ENearestPowerPillNotSafe = new NearestPowerPillNotSafe();
		Transition ENearestPillNotSafe = new NearestPillNotSafe();
		Transition ENearestePowerPillNotSafeButPillYes = new NearestePowerPillNotSafeButPillYes();
		
		standardCFSM.add(EPowerPill, ENearestPowerPillNotSafe, EPill);
		standardCFSM.add(EPill, ENearestPillNotSafe, ESafeZone);
		standardCFSM.add(ESafeZone, NotSafeZone, ERandom);
		standardCFSM.add(ERandom, ENearestePowerPillNotSafeButPillYes, EPill);
		standardCFSM.add(ERandom, ENearestPowerPillisSafeAndMoreThan2GhostsOut, EPowerPill);
		
		standardCFSM.ready(EPowerPill);

		CompoundState standard = new CompoundState("standard", standardCFSM);

		Transition PowerPillEaten = new PowerPillEaten();
		Transition NoEdibleGhosts = new NoEdibleGhosts();
		Transition NearToNotEdibleGhost = new NearToNotEdibleGhost();
		Transition NotNearToNotEdibleGhost = new NotNearToNotEdibleGhost();
		
		fsm.add(defense, PowerPillEaten, attack);
		fsm.add(attack, NearToNotEdibleGhost, defense);
		fsm.add(defense, NotNearToNotEdibleGhost, standard);
		fsm.add(standard, NearToNotEdibleGhost, defense);
		fsm.add(attack, NoEdibleGhosts, standard);
		fsm.add(standard, PowerPillEaten, attack);

		fsm.ready(standard);

		JFrame frame = new JFrame();
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
		main.add(c1observer.getAsPanel(true, null), BorderLayout.WEST);
		main.add(c2observer.getAsPanel(true, null), BorderLayout.SOUTH);
		main.add(c3observer.getAsPanel(true, null), BorderLayout.EAST);
		frame.getContentPane().add(main);
		frame.pack();
		frame.setVisible(true);

	}

	public void preCompute(String opponent) {
		fsm.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	@Override
	public MOVE getMove(Game game, long timeDue) {
		Input in = new MsPacManInput(game);
		return fsm.run(in);
	}

}