package es.ucm.fdi.ici.c2526.practica4.grupo3.mspacman;

import java.util.HashMap;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import es.ucm.fdi.ici.c2526.practica4.grupo3.CBRengine.CachedLinearCaseBase;
import pacman.game.Game;

public class MsPacManStorageManager {

	Game game;
	CBRCaseBase caseBase;

	Vector<CBRCase> buffer; // Buffer con los casos pendientes de revisar y retener
	HashMap<CBRCase, CBRCaseBase> caseMap; // Mapa para saber a que CaseBase pertenece cada caso pendiente
	HashMap<CBRCaseBase, Integer> typeCont; // Contador de casos pendientes en el buffer por tipo de CaseBase

	private final static int TIME_WINDOW = 3; // Number of cases to buffer before revising and retaining them.

	public MsPacManStorageManager() {
		this.buffer = new Vector<CBRCase>();
		this.caseMap = new HashMap<CBRCase, CBRCaseBase>();
		this.typeCont = new HashMap<CBRCaseBase, Integer>();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setCaseBase(CBRCaseBase caseBase) {
		this.caseBase = caseBase;

	}

	public void reviseAndRetain(CBRCase newCase) {
		this.buffer.add(newCase);
		caseMap.put(newCase, this.caseBase);
		typeCont.put(this.caseBase, typeCont.getOrDefault(this.caseBase, 0) + 1);

		// Buffer not full yet.
		if (this.buffer.size() < TIME_WINDOW)
			return;

		CBRCase bCase = this.buffer.remove(0);
		reviseCase(bCase);
		retainCase(bCase);

	}

	private void reviseCase(CBRCase bCase) {
		MsPacManDescription description = (MsPacManDescription) bCase.getDescription();
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		int resultValue = currentScore - oldScore;

		int oldPacmanLives = description.getPacmanLives();
		int actualPacmanLives = game.getPacmanNumberOfLivesRemaining();

		MsPacManResult result = (MsPacManResult) bCase.getResult();

		result.setScore(resultValue);
		result.setPacManDead(oldPacmanLives > actualPacmanLives);

	}

	private void retainCase(CBRCase bCase) {
		// Store the old case right now into the case base
		// Alternatively we could store all them when game finishes in close() method

		// here you should also check if the case must be stored into persistence (too
		// similar to existing ones, etc.)

		MsPacManResult result = (MsPacManResult) bCase.getResult();

		boolean shouldRetain = false;

		if (!result.getPacManDead() && result.getScore() > 0)
			shouldRetain = true;

		if (shouldRetain) {
			CBRCaseBase correctCaseBase = caseMap.get(bCase);
			int realId = ((CachedLinearCaseBase) correctCaseBase).getNextId();

			// Sobreescribimos los IDs del caso con el ID correcto y secuencial
			MsPacManDescription desc = (MsPacManDescription) bCase.getDescription();
			MsPacManSolution sol = (MsPacManSolution) bCase.getSolution();
			MsPacManResult res = (MsPacManResult) bCase.getResult();

			desc.setId(realId);
			sol.setId(realId);
			res.setId(realId);

			StoreCasesMethod.storeCase(caseMap.get(bCase), bCase);
		} else {

			System.out
					.println("Caso descartado (Malo): Score=" + result.getScore() + ", Dead=" + result.getPacManDead());
		}

		typeCont.put(caseMap.get(bCase), typeCont.get(caseMap.get(bCase)) - 1);
		caseMap.remove(bCase);

	}

	public void close() {
		for (CBRCase oldCase : this.buffer) {
			reviseCase(oldCase);
			retainCase(oldCase);
		}
		this.buffer.removeAllElements();
	}

	public int getPendingCases() {
		return this.buffer.size();
	}

	public int getTypePendingCases(CBRCaseBase caseBase) {
		return this.typeCont.getOrDefault(caseBase, 0);
	}

	public CBRCaseBase getCaseBase() {
		return this.caseBase;
	}

	public long getTotalCaseBasesSize() {
		long total = 0;
		for (CBRCaseBase cb : typeCont.keySet()) {
			total += cb.getCases().size();
		}
		return total;
	}
}