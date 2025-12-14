package es.ucm.fdi.ici.c2526.practica4.grupo3.mspacman;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import es.ucm.fdi.gaia.jcolibri.cbraplications.StandardCBRApplication;
import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRQuery;
import es.ucm.fdi.gaia.jcolibri.exception.ExecutionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.RetrievalResult;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.selection.SelectCases;
import es.ucm.fdi.gaia.jcolibri.util.FileIO;
import es.ucm.fdi.ici.c2526.practica4.grupo3.CBRengine.Average;
import es.ucm.fdi.ici.c2526.practica4.grupo3.CBRengine.CachedLinearCaseBase;
import es.ucm.fdi.ici.c2526.practica4.grupo3.CBRengine.CustomPlainTextConnector;
import gate.util.Pair;
import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;

public class MsPacManCBRengine implements StandardCBRApplication {

	private Map<String, Pair> connectorsCaseBases = new HashMap<>();

	private MOVE action;
	private MsPacManStorageManager storageManager;
	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;

	final static String TEAM = "grupo3"; // Cuidado!! poner el grupo aquí

	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2526/practica4/" + TEAM
			+ "/mspacman/plaintextconfig.xml";
	final static String CASE_BASE_PATH = "cbrdata" + File.separator + TEAM + File.separator + "mspacman"
			+ File.separator;

	final static String[] DISTANCES = { "lejos", "media", "cerca" };

	public MsPacManCBRengine(MsPacManStorageManager storageManager) {
		this.storageManager = storageManager;
	}

	@Override
	public void configure() throws ExecutionException {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				for (String s : DISTANCES) {
					connector = new CustomPlainTextConnector();
					connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));

					String filename = "nivel" + i + File.separator + (j == 0 ? "edible" : "noEdible") + File.separator
							+ s + ".csv";
					connector.setCaseBaseFile(CASE_BASE_PATH, filename);

					caseBase = new CachedLinearCaseBase();
					Pair p = new Pair(connector, caseBase);

					connectorsCaseBases.put(filename, p);

				}
			}
		}

		this.storageManager.setCaseBase(caseBase);

		simConfig = new NNConfig();

		simConfig.setDescriptionSimFunction(new Average());
		// === PACMAN ===
		simConfig.addMapping(new Attribute("pacmanPos", MsPacManDescription.class), new Interval(300));
		simConfig.addMapping(new Attribute("pacmanLastMove", MsPacManDescription.class), new Equal());

		// === GHOSTS ===
		simConfig.addMapping(new Attribute("nearestGhost", MsPacManDescription.class), new Interval(130));
		simConfig.addMapping(new Attribute("edibleGhost", MsPacManDescription.class), new Equal());
		simConfig.addMapping(new Attribute("numEdibles", MsPacManDescription.class), new Interval(4));
		simConfig.addMapping(new Attribute("ghostEdibleTime", MsPacManDescription.class), new Interval(200));
		simConfig.addMapping(new Attribute("listPosGhost", MsPacManDescription.class),
				new ListNumberSimilarityFunction(1000));
		simConfig.addMapping(new Attribute("ghostDistances", MsPacManDescription.class),
				new ListNumberSimilarityFunction(130));
		simConfig.addMapping(new Attribute("ghostsLastMoves", MsPacManDescription.class),
				new ListMoveSimilarityFunction());

		// === PILLS ===
		simConfig.addMapping(new Attribute("nearestPill", MsPacManDescription.class), new Interval(300));
		simConfig.addMapping(new Attribute("nearestPPill", MsPacManDescription.class), new Interval(300));
		simConfig.addMapping(new Attribute("remainingPills", MsPacManDescription.class), new Interval(200));
		simConfig.addMapping(new Attribute("remainingPowerPills", MsPacManDescription.class), new Interval(4));

		// ==== PESOS ====
		simConfig.setWeight(new Attribute("pacmanPos", MsPacManDescription.class), 20.0);
		simConfig.setWeight(new Attribute("pacmanLastMove", MsPacManDescription.class), 50.0);

		simConfig.setWeight(new Attribute("nearestGhost", MsPacManDescription.class), 60.0);
		simConfig.setWeight(new Attribute("edibleGhost", MsPacManDescription.class), 65.0);
		simConfig.setWeight(new Attribute("numEdibles", MsPacManDescription.class), 5.0);
		simConfig.setWeight(new Attribute("ghostEdibleTime", MsPacManDescription.class), 20.0);
		simConfig.setWeight(new Attribute("listPosGhost", MsPacManDescription.class), 15.0);
		simConfig.setWeight(new Attribute("ghostDistances", MsPacManDescription.class), 25.0);
		simConfig.setWeight(new Attribute("ghostsLastMoves", MsPacManDescription.class), 10.0);

		simConfig.setWeight(new Attribute("nearestPill", MsPacManDescription.class), 30.0);
		simConfig.setWeight(new Attribute("nearestPPill", MsPacManDescription.class), 25.0);
		simConfig.setWeight(new Attribute("remainingPills", MsPacManDescription.class), 2.0);
		simConfig.setWeight(new Attribute("remainingPowerPills", MsPacManDescription.class), 2.0);

	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		for (Entry<String, Pair> p : connectorsCaseBases.entrySet()) {
			connector = (CustomPlainTextConnector) p.getValue().first;
			caseBase = (CachedLinearCaseBase) p.getValue().second;
			caseBase.init(connector);
		}
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {

		MsPacManDescription d = (MsPacManDescription) query.getDescription();
		int level = storageManager.game.getCurrentLevel();

		// Determinar rango de distancia
		String distRange;
		if (d.getNearestGhost() <= 30)
			distRange = "cerca";
		else if (d.getNearestGhost() <= 70)
			distRange = "media";
		else
			distRange = "lejos";

		// Construir ruta del CSV correspondiente
		String filename = "nivel" + level + File.separator + (d.getEdibleGhost() ? "edible" : "noEdible")
				+ File.separator + distRange + ".csv";

		// Cargar o recuperar el CachedLinearCaseBase correspondiente
		Pair p = connectorsCaseBases.get(filename);
		connector = (CustomPlainTextConnector) p.first;
		caseBase = (CachedLinearCaseBase) p.second;

		storageManager.setCaseBase(caseBase);

		// Recuperación y reutilización
		if (caseBase.getCases().isEmpty()) {

			int mejorPos = 0, distance = d.getNearestGhost();

			for (Integer pos : d.getListPosGhost().getList()) {

				if (distance == storageManager.game.getDistance(storageManager.game.getPacmanCurrentNodeIndex(), pos,
						DM.PATH)) {
					mejorPos = pos;
				}

			}

			if (d.getEdibleGhost()) {
				this.action = storageManager.game.getApproximateNextMoveTowardsTarget(d.getPacmanPos(), mejorPos,
						d.pacmanLastMove, DM.PATH);
			} else {
				this.action = storageManager.game.getApproximateNextMoveAwayFromTarget(d.getPacmanPos(), mejorPos,
						d.pacmanLastMove, DM.PATH);
			}
		} else {
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query,
					simConfig);
			this.action = reuse(eval, query);
		}

		// Crear y retener el nuevo caso
		CBRCase newCase = createNewCase(query);

		storageManager.reviseAndRetain(newCase);
	}

	private MOVE reuse(Collection<RetrievalResult> eval, CBRQuery query) {

		int k = 5;

		Collection<RetrievalResult> topCases = SelectCases.selectTopKRR(eval, k);

		Map<MOVE, Double> moveScores = new HashMap<>();
		double averageSimilarity = 0.0;

		for (RetrievalResult r : topCases) {
			CBRCase c = r.get_case();
			MsPacManSolution s = (MsPacManSolution) c.getSolution();
			MsPacManResult res = (MsPacManResult) c.getResult();

			MOVE move = s.getAction();
			int score = res.getScore();
			double similarity = r.getEval();

			// Cuanto más similar, más peso tiene el score obtenido
			averageSimilarity += similarity;
			double weightedScore = score * similarity;
			moveScores.put(move, moveScores.getOrDefault(move, 0.0) + weightedScore);
		}

		averageSimilarity /= topCases.size();

		if (averageSimilarity < 0.90) { // La media de similitud es baja, moverse aleatoriamente
			int index = (int) Math.floor(Math.random() * 4);
			if (MOVE.values()[index] == action)
				index = (index + 1) % 4;
			action = MOVE.values()[index];
		} else { // Elegir el movimiento con mayor weightedScore
			action = MOVE.NEUTRAL;
			double maxScore = Double.NEGATIVE_INFINITY;

			for (Map.Entry<MOVE, Double> entry : moveScores.entrySet()) {
				if (entry.getValue() > maxScore) {
					maxScore = entry.getValue();
					action = entry.getKey();
				}
			}
		}
		return action;
	}

	/**
	 * Creates a new case using the query as description, storing the action into
	 * the solution and setting the proper id number
	 */
	private CBRCase createNewCase(CBRQuery query) {

		CBRCase newCase = new CBRCase();
		MsPacManDescription newDescription = (MsPacManDescription) query.getDescription();
		MsPacManResult newResult = new MsPacManResult();
		MsPacManSolution newSolution = new MsPacManSolution();

		// El id lo asignamaos al retener el caso en el StorageManager
		newSolution.setAction(this.action);
		newCase.setDescription(newDescription);
		newCase.setResult(newResult);
		newCase.setSolution(newSolution);

		return newCase;
	}

	public MOVE getSolution() {
		return this.action;
	}

	@Override
	public void postCycle() throws ExecutionException {
		this.storageManager.close();
		for (Pair p : connectorsCaseBases.values()) {
			caseBase = (CachedLinearCaseBase) p.second;
			caseBase.close();
		}
	}

}