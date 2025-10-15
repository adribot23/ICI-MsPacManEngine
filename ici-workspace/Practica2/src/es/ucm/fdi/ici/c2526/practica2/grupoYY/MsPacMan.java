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
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.transitions.NearestPowerPillisSafeAndMoreThan2GhostsOut;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.transitions.RandomTransition;
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

		SimpleState state1 = new SimpleState("state1", new ERandomAction());
		SimpleState state2 = new SimpleState("state2", new ERandomAction());
		SimpleState state3 = new SimpleState("state3", new ERandomAction());

		Transition tran1 = new RandomTransition(.3);
		Transition tran2 = new RandomTransition(.2);
		Transition tran3 = new RandomTransition(.1);
		Transition tran4 = new RandomTransition(.01);

		FSM defenseCFSM = new FSM("DEFENSE");
		GraphFSMObserver c1observer = new GraphFSMObserver(defenseCFSM.toString());
		defenseCFSM.addObserver(c1observer);

		SimpleState DGhost = new SimpleState("RunAwayFromNearestGhost", new DRunAwayFromNearestGhost());
		SimpleState DPowerPill = new SimpleState("RunAwayToNearestSafePPAction", new DRunAwayToNearestSafePPAction());
		SimpleState DPill = new SimpleState("RunAwayToNearestSafePillAction", new DRunAwayToNearestSafePillAction());

		Transition defense = new NearestPowerPillisSafeAndMoreThan2GhostsOut();
		

		defenseCFSM.add(DGhost, defense, DPowerPill);
		defenseCFSM.add(DPowerPill, defense, DPill);
		
		defenseCFSM.ready(DGhost);

		CompoundState defense = new CompoundState("defense", defenseCFSM);

		fsm.add(state1, tran1, defense);
		fsm.add(defense, tran2, state1);
		fsm.add(defense, tran3, state2);
		fsm.add(state2, tran4, defense);

		fsm.ready(state1);

		JFrame frame = new JFrame();
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(observer.getAsPanel(true, null), BorderLayout.CENTER);
		main.add(c1observer.getAsPanel(true, null), BorderLayout.SOUTH);
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