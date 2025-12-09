import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import es.ucm.fdi.ici.c2526.practica4.grupoYY.MsPacMan;
import pacman.Executor;
import pacman.controllers.GhostController;
import pacman.controllers.PacmanController;

public class EvaluatorTest {

	public static void main(String[] args) {
		Executor executor = new Executor.Builder().setTickLimit(1000).setVisual(true).setScaleFactor(2.5).build();

		PacmanController pacMan = new MsPacMan();
		//GhostController ghosts = new AggressiveGhosts();
		// GhostController ghosts = new GhostsRandom();
		  GhostController ghosts = new es.ucm.fdi.ici.c2526.practica1.grupoG.Ghosts();
		//   GhostController ghosts = new es.ucm.fdi.ici.c2526.practica1.grupoB.Ghosts();
		  //GhostController ghosts = new es.ucm.fdi.ici.c2526.practica1.grupoC.Ghosts();
		 //  GhostController ghosts = new es.ucm.fdi.ici.c2526.practica1.grupoM.Ghosts();
		
		int trials = 50;

		ArrayList<?>[] stats = executor.runCBRExperiment(pacMan, ghosts, trials,
				pacMan.getClass().getName() + " - " + ghosts.getClass().getName());
		;
		System.out.println("Scores: " + stats[0]);
		System.out.println("Time: " + stats[1]);
		System.out.println("Cases: " + stats[2]);
		System.out.println("Steps: " + stats[3]);

		displayCharts(stats);

	}

	public static void displayCharts(ArrayList<?>[] stats) {
		double[] xData = new double[stats[0].size()];
		for (double i = 0; i < stats[0].size(); ++i)
			xData[(int) i] = i;
		// Create Chart
		XYChart chart0 = QuickChart.getChart("Scores", "Game", "Score", "Score", xData,
				ArrayUtils.toPrimitive(stats[0].toArray(new Double[0])));
		XYChart chart1 = QuickChart.getChart("Time", "Game", "Time", "Time", xData,
				ArrayUtils.toPrimitive(stats[1].toArray(new Double[0])));
		XYChart chart2 = QuickChart.getChart("Cases", "Game", "Cases", "Cases", xData,
				ArrayUtils.toPrimitive(stats[2].toArray(new Double[0])));
		XYChart chart3 = QuickChart.getChart("Steps", "Game", "Steps", "Steps", xData,
				ArrayUtils.toPrimitive(stats[3].toArray(new Double[0])));

		List<XYChart> charts = new ArrayList<XYChart>();
		charts.add(chart0);
		charts.add(chart1);
		charts.add(chart2);
		charts.add(chart3);

		// Show it
		new SwingWrapper<XYChart>(charts).displayChartMatrix();
	}

}
