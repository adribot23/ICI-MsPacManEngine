import es.ucm.fdi.ici.c2526.practica4.grupo3.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

	public static void main(String[] args) {
		Executor executor = new Executor.Builder().setTickLimit(4000).setVisual(true).setScaleFactor(2.5).build();

		PacmanController pacMan = new MsPacMan();
		// GhostController ghosts = new AggressiveGhosts();
		// GhostController ghosts = new GhostsRandom();
		GhostController ghosts = new es.ucm.fdi.ici.c2526.practica1.grupoB.Ghosts();
		System.out.println(executor.runGame(pacMan, ghosts, 10) // last parameter defines speed
		);
	}

}