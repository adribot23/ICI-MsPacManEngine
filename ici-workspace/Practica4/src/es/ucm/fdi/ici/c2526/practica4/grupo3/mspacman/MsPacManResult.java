package es.ucm.fdi.ici.c2526.practica4.grupo3.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;

public class MsPacManResult implements CaseComponent {

	Integer id;
	Integer score;
	Boolean pacManDead;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Boolean getPacManDead() {
		return pacManDead;
	}

	public void setPacManDead(Boolean pacManDead) {
		this.pacManDead = pacManDead;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManResult.class);
	}

	@Override
	public String toString() {
		return "MsPacManResult [id=" + id + ", score=" + score + ", pacManDead=" + pacManDead + "]";
	}

}
