import es.ucm.fdi.ici.c2425.practica0.garcia.Ghosts;
import es.ucm.fdi.ici.c2425.practica0.garcia.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

	public static void main(String[] args) {
		Executor executor = new Executor.Builder().setTickLimit(4000).setVisual(true).setScaleFactor(2.5).build();

		// PacmanController pacMan = new MsPacManRandom();
		// PacmanController pacMan = new MsPacManRunAway();
		PacmanController pacMan = new MsPacMan();

		//GhostController ghosts = new GhostsRandom();
		//GhostController ghosts = new GhostsAggresive();
		GhostController ghosts = new Ghosts();

		System.out.println(executor.runGame(pacMan, ghosts, 50) // last parameter defines speed
		);
	}

}
