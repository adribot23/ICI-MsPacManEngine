package es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.transitions;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2526.practica2.grupoYY.mspacman.MsPacManInput;
import es.ucm.fdi.ici.fsm.Transition;

public class NearestePowerPillNotSafeButPillYes implements Transition {

	@Override
	public boolean evaluate(Input in) {
		MsPacManInput input = (MsPacManInput) in;

		return (input.avoidPowerPills() || input.getNearestSafePowerPill() == -1) && input.getNearestSafePill() != -1;

	}

}
