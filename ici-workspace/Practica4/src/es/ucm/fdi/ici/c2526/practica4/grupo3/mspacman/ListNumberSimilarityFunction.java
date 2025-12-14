package es.ucm.fdi.ici.c2526.practica4.grupo3.mspacman;

import java.util.List;

import es.ucm.fdi.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;
import es.ucm.fdi.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;

public class ListNumberSimilarityFunction implements LocalSimilarityFunction {

	private double _interval;

	public ListNumberSimilarityFunction(double interval) {
		_interval = interval;
	}

	@Override
	public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {

		if (caseObject == null || queryObject == null)
			return 0;

		if (!(caseObject instanceof MyIntegerListType) || !(queryObject instanceof MyIntegerListType))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

		List<Integer> cList = ((MyIntegerListType) caseObject).getList();
		List<Integer> qList = ((MyIntegerListType) queryObject).getList();

		if (cList.size() != qList.size())
			throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

		Interval interval = new Interval(_interval);
		double total = 0;

		for (int i = 0; i < cList.size(); i++) {
			Integer v1 = cList.get(i);
			Integer v2 = qList.get(i);

			total += interval.compute(v1.doubleValue(), v2.doubleValue());
		}

		return total / cList.size();
	}

	@Override
	public boolean isApplicable(Object caseObject, Object queryObject) {
		return caseObject instanceof MyIntegerListType && queryObject instanceof MyIntegerListType;
	}
}
