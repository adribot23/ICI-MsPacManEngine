import es.ucm.fdi.ici.c2526.practica1.grupoF.Ghosts;
import es.ucm.fdi.ici.c2526.practica1.grupoF.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class ExecutorTest {

	public static void main(String[] args) {
		Executor executor = new Executor.Builder().setTickLimit(4000).setVisual(true).setScaleFactor(2.5).build();

		PacmanController pacMan = new MsPacMan();
		GhostController ghosts = new Ghosts();

		System.out.println(executor.runGame(pacMan, ghosts, 20) // last parameter defines speed
		);
	}

}
