package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.List;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import pacman.game.Constants.MOVE;

public class MsPacManDescription implements CaseComponent {

	Integer id;
	Integer score;
	Integer time;
	Integer nearestPPill;
	Integer nearestGhost;
	Boolean edibleGhost;

	List<Integer> listPosGhost;
	List<Integer> ghostDistances;
	List<MOVE> ghostsLastMoves;

	Integer numEdibles;
	Integer ghostEdibleTime;

	Integer pacmanPos;
	MOVE pacmanLastMove;
	
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

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getNearestPPill() {
		return nearestPPill;
	}

	public void setNearestPPill(Integer nearestPPill) {
		this.nearestPPill = nearestPPill;
	}

	public Integer getNearestGhost() {
		return nearestGhost;
	}

	public void setNearestGhost(Integer nearestGhost) {
		this.nearestGhost = nearestGhost;
	}

	public Boolean getEdibleGhost() {
		return edibleGhost;
	}

	public void setEdibleGhost(Boolean edibleGhost) {
		this.edibleGhost = edibleGhost;
	}
	
	public List<Integer> getListPosGhost() {
		return listPosGhost;
	}

	public void setListPosGhost(List<Integer> listPosGhost) {
		this.listPosGhost = listPosGhost;
	}

	public List<Integer> getGhostDistances() {
		return ghostDistances;
	}

	public void setGhostDistances(List<Integer> ghostDistances) {
		this.ghostDistances = ghostDistances;
	}

	public List<MOVE> getGhostsLastMoves() {
		return ghostsLastMoves;
	}

	public void setGhostsLastMoves(List<MOVE> ghostsLastMoves) {
		this.ghostsLastMoves = ghostsLastMoves;
	}

	public Integer getNumEdibles() {
		return numEdibles;
	}

	public void setNumEdibles(Integer numEdibles) {
		this.numEdibles = numEdibles;
	}

	public Integer getGhostEdibleTime() {
		return ghostEdibleTime;
	}

	public void setGhostEdibleTime(Integer ghostEdibleTime) {
		this.ghostEdibleTime = ghostEdibleTime;
	}

	public Integer getPacmanPos() {
		return pacmanPos;
	}

	public void setPacmanPos(Integer pacmanPos) {
		this.pacmanPos = pacmanPos;
	}

	public MOVE getPacmanLastMove() {
		return pacmanLastMove;
	}

	public void setPacmanLastMove(MOVE pacmanLastMove) {
		this.pacmanLastMove = pacmanLastMove;
	}

	
	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [id=" + id + ", score=" + score + ", time=" + time + ", nearestPPill="
				+ nearestPPill + ", nearestGhost=" + nearestGhost + ", edibleGhost=" + edibleGhost + "]";
	}

}
