package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

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
import es.ucm.fdi.ici.c2526.practica4.grupoYY.CBRengine.Average;
import es.ucm.fdi.ici.c2526.practica4.grupoYY.CBRengine.CachedLinearCaseBase;
import es.ucm.fdi.ici.c2526.practica4.grupoYY.CBRengine.CustomPlainTextConnector;
import gate.util.Pair;
import pacman.game.Constants.MOVE;

public class MsPacManCBRengine implements StandardCBRApplication {

	private Map<String, Pair> connectorsCaseBases = new HashMap<>();

	private MOVE action;
	private MsPacManStorageManager storageManager;
	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;

	final static String TEAM = "grupoYY"; // Cuidado!! poner el grupo aquí

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
		simConfig.addMapping(new Attribute("score", MsPacManDescription.class), new Interval(15000));
		simConfig.addMapping(new Attribute("time", MsPacManDescription.class), new Interval(4000));
		simConfig.addMapping(new Attribute("nearestPPill", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("nearestGhost", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("edibleGhost", MsPacManDescription.class), new Equal());

		simConfig.addMapping(new Attribute("listPosGhost", MsPacManDescription.class),
				new ListNumberSimilarityFunction(650));
		simConfig.addMapping(new Attribute("ghostDistances", MsPacManDescription.class),
				new ListNumberSimilarityFunction(650));
		simConfig.addMapping(new Attribute("ghostsLastMoves", MsPacManDescription.class),
				new ListMoveSimilarityFunction());

		simConfig.addMapping(new Attribute("numEdibles", MsPacManDescription.class), new Interval(4));
		simConfig.addMapping(new Attribute("ghostEdibleTime", MsPacManDescription.class), new Interval(2000));

		simConfig.addMapping(new Attribute("pacmanPos", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("pacmanLastMove", MsPacManDescription.class), new Equal());
		
		simConfig.setWeight(new Attribute("nearestGhost", MsPacManDescription.class), 0.25);
		simConfig.setWeight(new Attribute("ghostDistances", MsPacManDescription.class), 0.20);
		simConfig.setWeight(new Attribute("edibleGhost", MsPacManDescription.class), 0.10);
		simConfig.setWeight(new Attribute("ghostEdibleTime", MsPacManDescription.class), 0.10);
		simConfig.setWeight(new Attribute("numEdibles", MsPacManDescription.class), 0.07);
		simConfig.setWeight(new Attribute("nearestPPill", MsPacManDescription.class), 0.08);
		simConfig.setWeight(new Attribute("ghostsLastMoves", MsPacManDescription.class), 0.07);
		simConfig.setWeight(new Attribute("pacmanLastMove", MsPacManDescription.class), 0.05);
		simConfig.setWeight(new Attribute("pacmanPos", MsPacManDescription.class), 0.03);
		simConfig.setWeight(new Attribute("listPosGhost", MsPacManDescription.class), 0.03);
		simConfig.setWeight(new Attribute("time", MsPacManDescription.class), 0.005);
		simConfig.setWeight(new Attribute("score", MsPacManDescription.class), 0.005);

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
			this.action = MOVE.NEUTRAL;
		} else {
			Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query,
					simConfig);
			this.action = reuse(eval);
		}

		// Crear y retener el nuevo caso
		CBRCase newCase = createNewCase(query);
		// storageManager.reviseAndRetain(newCase,caseBase); nos lo ha dicho el profe
		storageManager.reviseAndRetain(newCase);
	}

	private MOVE reuse(Collection<RetrievalResult> eval) {
		// This simple implementation only uses 1NN
		// Consider using kNNs with majority voting
		Map<MOVE, Integer> moveScores = new HashMap<>();

		int k = 5;
		Collection<RetrievalResult> casos = SelectCases.selectTopKRR(eval, k);

		for (RetrievalResult r : casos) {
		    CBRCase c = r.get_case();
		    MsPacManSolution s = (MsPacManSolution) c.getSolution();
		    MsPacManResult res = (MsPacManResult) c.getResult();

		    MOVE move = s.getAction();
		    int score = res.getScore();

		    // Si no existe, inicializa con 0
		    if (!moveScores.containsKey(move)) {
		        moveScores.put(move, score);
		    } else {
		        // Si ya existe, suma
		        moveScores.put(move, moveScores.get(move) + score);
		    }
		}
		
		Integer maxScore= 0;
		MOVE bestMove= MOVE.NEUTRAL;
		for(Entry<MOVE, Integer> m :moveScores.entrySet()) {
			if(m.getValue()>maxScore) {
				maxScore= m.getValue();
				bestMove= m.getKey();
			}
				
		}
		return bestMove;
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

		// USAR EL CASEBASE ACTIVO
		// CachedLinearCaseBase activeCaseBase = (CachedLinearCaseBase)
		// storageManager.getCaseBase();

		int newId = caseBase.getNextId() + storageManager.getTypePendingCases(caseBase);

		newDescription.setId(newId);
		newResult.setId(newId);
		newSolution.setId(newId);
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