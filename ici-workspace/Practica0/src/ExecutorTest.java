import es.ucm.fdi.ici.c2425.practica0.garcia.GhostsAggresive;
import es.ucm.fdi.ici.c2425.practica0.garcia.GhostsRandom;
import es.ucm.fdi.ici.c2425.practica0.garcia.MsPacManRandom;
import es.ucm.fdi.ici.c2425.practica0.garcia.MsPacManRunAway;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

	public static void main(String[] args) {
		Executor executor = new Executor.Builder().setTickLimit(4000).setVisual(true).setScaleFactor(2.5).build();

		PacmanController pacMan = new MsPacManRunAway();
		GhostController ghosts = new GhostsAggresive();

		System.out.println(executor.runGame(pacMan, ghosts, 30) // last parameter defines speed
		);
	}

}
