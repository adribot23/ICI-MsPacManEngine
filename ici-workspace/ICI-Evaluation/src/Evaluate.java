import es.ucm.fdi.ici.PacManParallelEvaluator;
import es.ucm.fdi.ici.Scores;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.MsPacMan;


public class Evaluate {

	public static void main(String[] args) {
		PacManParallelEvaluator evaluator = new PacManParallelEvaluator();
		Scores scores = evaluator.evaluate();
		scores.printScoreAndRanking();

	}

}
