package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.HashMap;
import java.util.Vector;

import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CBRCaseBase;
import es.ucm.fdi.gaia.jcolibri.method.retain.StoreCasesMethod;
import pacman.game.Game;

public class MsPacManStorageManager {

	Game game;
	CBRCaseBase caseBase;
	
	Vector<CBRCase> buffer; //Buffer con los casos pendientes de revisar y retener
	HashMap<CBRCase, CBRCaseBase> caseMap; //Mapa para saber a que CaseBase pertenece cada caso pendiente
	HashMap<CBRCaseBase, Integer> typeCont; //Contador de casos pendientes por cada CaseBase
	
	//Si aumentamos TIME_WINDOW se almacenaran mas casos pero se perdera su CaseBase asociado
	//por lo que se guardaran en csv distintos y seria errorneo (solucionado con hashMap)
	
	private final static int TIME_WINDOW = 3; //Number of cases to buffer before revising and retaining them.
	
	public MsPacManStorageManager()
	{
		this.buffer = new Vector<CBRCase>();
		this.caseMap = new HashMap<CBRCase, CBRCaseBase>();
		this.typeCont = new HashMap<CBRCaseBase, Integer>();
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void setCaseBase(CBRCaseBase caseBase)
	{
		this.caseBase = caseBase;
	}
	
	public void reviseAndRetain(CBRCase newCase)
	{			
		this.buffer.add(newCase);
		caseMap.put(newCase, this.caseBase);
		typeCont.put(this.caseBase, typeCont.getOrDefault(this.caseBase, 0)+1);
		
		//Buffer not full yet.
		if(this.buffer.size()<TIME_WINDOW)
			return;
		
		
		CBRCase bCase = this.buffer.remove(0);
		reviseCase(bCase);
		retainCase(bCase);
		
	}
	
	private void reviseCase(CBRCase bCase) {
		MsPacManDescription description = (MsPacManDescription)bCase.getDescription();
		int oldScore = description.getScore();
		int currentScore = game.getScore();
		int resultValue = currentScore - oldScore;
		
		MsPacManResult result = (MsPacManResult)bCase.getResult();
		result.setScore(resultValue);	
	}
	
	private void retainCase(CBRCase bCase)
	{
		//Store the old case right now into the case base
		//Alternatively we could store all them when game finishes in close() method
		
		//here you should also check if the case must be stored into persistence (too similar to existing ones, etc.)
		
		
		StoreCasesMethod.storeCase(caseMap.get(bCase), bCase);
		typeCont.put(caseMap.get(bCase), typeCont.get(caseMap.get(bCase))-1);
		caseMap.remove(bCase);
		
		
	}

	public void close() {
		for(CBRCase oldCase: this.buffer)
		{
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
}