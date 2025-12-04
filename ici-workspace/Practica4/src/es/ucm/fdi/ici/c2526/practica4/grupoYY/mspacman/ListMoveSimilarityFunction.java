package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.List;

import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import pacman.game.Constants.MOVE;

public class ListMoveSimilarityFunction implements LocalSimilarityFunction {

	@Override
	public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {

		if (caseObject == null || queryObject == null)
			return 0;

		// Deben ser MyMOVEListType
		if (!(caseObject instanceof MyMOVEListType) || !(queryObject instanceof MyMOVEListType))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

		List<MOVE> cList = ((MyMOVEListType) caseObject).getList();
		List<MOVE> qList = ((MyMOVEListType) queryObject).getList();

		if (cList.size() != qList.size())
			throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

		Equal equal = new Equal();
		double total = 0;

		for (int i = 0; i < cList.size(); i++) {

			MOVE m1 = cList.get(i);
			MOVE m2 = qList.get(i);

			total += equal.compute(m1, m2); // 1 si iguales, 0 si diferentes
		}

		return total / cList.size();
	}

	@Override
	public boolean isApplicable(Object caseObject, Object queryObject) {
		return caseObject instanceof MyMOVEListType && queryObject instanceof MyMOVEListType;
	}
}
