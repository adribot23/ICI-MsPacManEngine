package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

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

        if (!(caseObject instanceof List<?>) || !(queryObject instanceof List<?>))
            throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

        List<?> cList = (List<?>) caseObject;
        List<?> qList = (List<?>) queryObject;

        if (cList.size() != qList.size())
            throw new NoApplicableSimilarityFunctionException(this.getClass(), caseObject.getClass());

        Interval interval = new Interval(_interval);
        double total = 0;

        for (int i = 0; i < cList.size(); i++) {
            Object o1 = cList.get(i);
            Object o2 = qList.get(i);

            if (o1 instanceof Number && o2 instanceof Number) {
                double v1 = ((Number) o1).doubleValue();
                double v2 = ((Number) o2).doubleValue();
                total += interval.compute(v1, v2);
            } else {
                throw new NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
            }
        }

        return total / cList.size();
    }

    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return caseObject instanceof List<?> && queryObject instanceof List<?>;
    }
}