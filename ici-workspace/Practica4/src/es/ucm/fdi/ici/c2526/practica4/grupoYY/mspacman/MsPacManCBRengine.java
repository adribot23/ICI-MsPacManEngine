package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import pacman.game.Constants.MOVE;


public class MsPacManCBRengine implements StandardCBRApplication {

	private MOVE action;
	private MsPacManStorageManager storageManager;
	CustomPlainTextConnector connector;
	CachedLinearCaseBase caseBase;
	NNConfig simConfig;
	
	  private Map<String, CachedLinearCaseBase> loadedCaseBases = new HashMap<>();
	final static String TEAM = "grupoYY"; // Cuidado!! poner el grupo aquí

	final static String CONNECTOR_FILE_PATH = "es/ucm/fdi/ici/c2526/practica4/" + TEAM
			+ "/mspacman/plaintextconfig.xml";
	final static String CASE_BASE_PATH = "cbrdata" + File.separator + TEAM + File.separator + "mspacman"
			+ File.separator;

	public MsPacManCBRengine(MsPacManStorageManager storageManager) {
		this.storageManager = storageManager;
	}

	@Override
	public void configure() throws ExecutionException {
		connector = new CustomPlainTextConnector();
		//caseBase = new CachedLinearCaseBase();
		
		//si quito esto se queda pillado al principio no se por que
		connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH)); 
		
		//this.storageManager.setCaseBase(caseBase);

		simConfig = new NNConfig();
		simConfig.setDescriptionSimFunction(new Average());
		simConfig.addMapping(new Attribute("score", MsPacManDescription.class), new Interval(15000));
		simConfig.addMapping(new Attribute("time", MsPacManDescription.class), new Interval(4000));
		simConfig.addMapping(new Attribute("nearestPPill", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("nearestGhost", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("edibleGhost", MsPacManDescription.class), new Equal());
		
		simConfig.addMapping(new Attribute("listPosGhost", MsPacManDescription.class), new ListNumberSimilarityFunction(650));
		simConfig.addMapping(new Attribute("ghostDistances", MsPacManDescription.class), new ListNumberSimilarityFunction(650));
		simConfig.addMapping(new Attribute("ghostsLastMoves", MsPacManDescription.class), new ListMoveSimilarityFunction());
		
		simConfig.addMapping(new Attribute("numEdibles", MsPacManDescription.class), new Interval(4));
		simConfig.addMapping(new Attribute("ghostEdibleTime", MsPacManDescription.class), new Interval(2000));
		
		simConfig.addMapping(new Attribute("pacmanPos", MsPacManDescription.class), new Interval(650));
		simConfig.addMapping(new Attribute("pacmanLastMove", MsPacManDescription.class), new Equal());
	
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		//caseBase.init(connector);
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
		  
		    if (!loadedCaseBases.containsKey(filename)) {
		    	connector = new CustomPlainTextConnector();
		    	connector.initFromXMLfile(FileIO.findFile(CONNECTOR_FILE_PATH));
		    	connector.setCaseBaseFile(CASE_BASE_PATH, filename);

		        caseBase = new CachedLinearCaseBase();
		        caseBase.init(connector);

		        loadedCaseBases.put(filename, caseBase);
		    } else {
		    	caseBase = loadedCaseBases.get(filename);
		    }

		    // Asignar el case-base activo al storageManager
		    storageManager.setCaseBase(caseBase);

		    // Recuperación y reutilización
		    if (caseBase.getCases().isEmpty()) {
		        this.action = MOVE.NEUTRAL;
		    } else {
		        Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, simConfig);
		        this.action = reuse(eval);
		    }

		    // Crear y retener el nuevo caso
		    CBRCase newCase = createNewCase(query);
		    storageManager.reviseAndRetain(newCase);
		}

	private MOVE reuse(Collection<RetrievalResult> eval) {
		// This simple implementation only uses 1NN
		// Consider using kNNs with majority voting
		RetrievalResult first = SelectCases.selectTopKRR(eval, 1).iterator().next();
		CBRCase mostSimilarCase = first.get_case();
		double similarity = first.getEval();

		/*
		 * if(Math.random()<.2) { ArrayList<CBRCase> toforget = new
		 * ArrayList<CBRCase>(); toforget.add(mostSimilarCase);
		 * this.caseBase.forgetCases(toforget);
		 * System.out.println(mostSimilarCase.getID()); }
		 */
		MsPacManResult result = (MsPacManResult) mostSimilarCase.getResult();
		MsPacManSolution solution = (MsPacManSolution) mostSimilarCase.getSolution();

		// Now compute a solution for the query

		// Here, it simply takes the action of the 1NN
		MOVE action = solution.getAction();

		// But if not enough similarity or bad case, choose another move randomly
		if ((similarity < 0.7) || (result.getScore() < 100)) {
			int index = (int) Math.floor(Math.random() * 4);
			if (MOVE.values()[index] == action)
				index = (index + 1) % 4;
			action = MOVE.values()[index];
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

	    // USAR EL CASEBASE ACTIVO
	    //CachedLinearCaseBase activeCaseBase = (CachedLinearCaseBase) storageManager.getCaseBase();

	    int newId = caseBase.getNextId()+ storageManager.getTypePendingCases(caseBase);
	    
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
		
		for (CachedLinearCaseBase cb : loadedCaseBases.values()) {
	        cb.close();
	    }
	}

}