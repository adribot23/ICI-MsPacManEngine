package es.ucm.fdi.ici.c2526.practica2.grupoYY;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.ici.fsm.CompoundState;
import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.MsPacManInput;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayFromNearestGhost;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayToNearestSafePPAction;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.DRunAwayToNearestSafePillAction;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.actions.ERunToPillByAlternativeWayAction;
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
		Transition NearestPowerPillisSafeAndMoreThan2GhostsOut2 = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition NearestPowerPillAndPillNotSafe = new NearestPowerPillAndPillNotSafe();
		Transition NearestPillNotSafe = new NearestPillNotSafe();
		Transition NearestePowerPillNotSafeButPillYes = new NearestePowerPillNotSafeButPillYes();
		Transition NearestePowerPillNotSafeButPillYes2 = new NearestePowerPillNotSafeButPillYes();

		defenseCFSM.add(DGhost, NearestPowerPillisSafeAndMoreThan2GhostsOut, DPowerPill);
		defenseCFSM.add(DGhost, NearestePowerPillNotSafeButPillYes, DPill);
		
		defenseCFSM.add(DPowerPill, NearestPowerPillAndPillNotSafe, DGhost);
		defenseCFSM.add(DPowerPill, NearestePowerPillNotSafeButPillYes2, DPill);
		
		defenseCFSM.add(DPill, NearestPillNotSafe, DGhost);
		defenseCFSM.add(DPill, NearestPowerPillisSafeAndMoreThan2GhostsOut2, DPowerPill);

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
		attackCFSM.add(AEdible, TwoOrMoreGhostsCloseEachOther, AGroup);
		
		attackCFSM.add(APill, NearToEdibleGhost, AEdible);
		
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
		SimpleState ERandom = new SimpleState("RandomAction", new ERunToPillByAlternativeWayAction());

		Transition ENearestPowerPillisSafeAndMoreThan2GhostsOut = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition ENearestPowerPillisSafeAndMoreThan2GhostsOut1 = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition ENearestPowerPillisSafeAndMoreThan2GhostsOut2 = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		Transition ENearestePowerPillNotSafeButPillYes = new NearestePowerPillNotSafeButPillYes();
		Transition ENearestePowerPillNotSafeButPillYes1 = new NearestePowerPillNotSafeButPillYes();
		Transition ENearestPowerPillNotSafe = new NearestPowerPillAndPillNotSafe();
		Transition ENearestPillNotSafe = new NearestPillNotSafe();
		Transition NotSafeZone = new NotSafeZone();

		standardCFSM.add(EPowerPill, ENearestPowerPillNotSafe, EPill);

		standardCFSM.add(EPill, ENearestPillNotSafe, ESafeZone);
		standardCFSM.add(EPill, ENearestPowerPillisSafeAndMoreThan2GhostsOut, EPowerPill);

		standardCFSM.add(ESafeZone, NotSafeZone, ERandom);
		standardCFSM.add(ESafeZone, ENearestPowerPillisSafeAndMoreThan2GhostsOut1, EPowerPill);
		standardCFSM.add(ESafeZone, ENearestePowerPillNotSafeButPillYes, EPill);

		standardCFSM.add(ERandom, ENearestePowerPillNotSafeButPillYes1, EPill);
		standardCFSM.add(ERandom, ENearestPowerPillisSafeAndMoreThan2GhostsOut2, EPowerPill);

		standardCFSM.ready(EPowerPill);

		CompoundState standard = new CompoundState("standard", standardCFSM);

		Transition PowerPillEatenAndGhostOutside = new PowerPillEatenAndGhostOutside();
		Transition PowerPillEatenAndGhostOutside1 = new PowerPillEatenAndGhostOutside();
		Transition NoEdibleGhosts = new NoEdibleGhosts();
		Transition NearToNotEdibleGhost = new NearToNotEdibleGhost();
		Transition NearToNotEdibleGhost2 = new NearToNotEdibleGhost();
		Transition NotNearToNotEdibleGhost = new NotNearToNotEdibleGhost();

		fsm.add(defense, PowerPillEatenAndGhostOutside, attack);
		fsm.add(defense, NotNearToNotEdibleGhost, standard);
		
		fsm.add(attack, NearToNotEdibleGhost, defense);
		fsm.add(attack, NoEdibleGhosts, standard);
		
		fsm.add(standard, NearToNotEdibleGhost2, defense);
		fsm.add(standard, PowerPillEatenAndGhostOutside1, attack);

		fsm.ready(standard);

		JFrame frame = new JFrame();
		JPanel main = new JPanel();
		//main.setLayout(new BorderLayout());
		main.setLayout(new GridLayout(2,2));
		main.add(observer.getAsPanel(true, null));
		main.add(c1observer.getAsPanel(true, null));
		main.add(c2observer.getAsPanel(true, null));
		main.add(c3observer.getAsPanel(true, null));
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