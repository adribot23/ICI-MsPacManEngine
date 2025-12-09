package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import es.ucm.fdi.gaia.jcolibri.cbrcore.Attribute;
import es.ucm.fdi.gaia.jcolibri.cbrcore.CaseComponent;
import pacman.game.Constants.MOVE;

public class MsPacManDescription implements CaseComponent {

	// === PACMAN ===
	Integer pacmanLives;
	Integer pacmanPos;
	MOVE pacmanLastMove;

	// === GHOSTS ===
	Integer nearestGhost;
	Boolean edibleGhost;
	Integer numEdibles;
	Integer ghostEdibleTime;
	MyIntegerListType listPosGhost;
	MyIntegerListType ghostDistances;
	MyMOVEListType ghostsLastMoves;

	// === PILLS ===
	Integer nearestPill;
	Integer nearestPPill;
	Integer remainingPills;
	Integer remainingPowerPills;

	// === OTROS ===
	Integer score;
	Integer time;
	Integer id;

	// --- Pacman ---
	public Integer getPacmanLives() {
		return pacmanLives;
	}

	public void setPacmanLives(Integer pacmanLives) {
		this.pacmanLives = pacmanLives;
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

	// --- Ghosts ---
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

	public MyIntegerListType getListPosGhost() {
		return listPosGhost;
	}

	public void setListPosGhost(MyIntegerListType listPosGhost) {
		this.listPosGhost = listPosGhost;
	}

	public MyIntegerListType getGhostDistances() {
		return ghostDistances;
	}

	public void setGhostDistances(MyIntegerListType ghostDistances) {
		this.ghostDistances = ghostDistances;
	}

	public MyMOVEListType getGhostsLastMoves() {
		return ghostsLastMoves;
	}

	public void setGhostsLastMoves(MyMOVEListType ghostsLastMoves) {
		this.ghostsLastMoves = ghostsLastMoves;
	}

	// --- Pills ---
	public Integer getNearestPill() {
		return nearestPill;
	}

	public void setNearestPill(Integer nearestPill) {
		this.nearestPill = nearestPill;
	}

	public Integer getNearestPPill() {
		return nearestPPill;
	}

	public void setNearestPPill(Integer nearestPPill) {
		this.nearestPPill = nearestPPill;
	}

	public Integer getRemainingPills() {
		return remainingPills;
	}

	public void setRemainingPills(Integer remainingPills) {
		this.remainingPills = remainingPills;
	}

	public Integer getRemainingPowerPills() {
		return remainingPowerPills;
	}

	public void setRemainingPowerPills(Integer remainingPowerPills) {
		this.remainingPowerPills = remainingPowerPills;
	}

	// --- Otros ---
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

	// --- ID ---
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id", MsPacManDescription.class);
	}

	@Override
	public String toString() {
		return "MsPacManDescription [" + "id=" + id + ", pacmanLives=" + pacmanLives + ", pacmanPos=" + pacmanPos
				+ ", pacmanLastMove=" + pacmanLastMove + ", nearestGhost=" + nearestGhost + ", edibleGhost="
				+ edibleGhost + ", numEdibles=" + numEdibles + ", ghostEdibleTime=" + ghostEdibleTime
				+ ", listPosGhost=" + listPosGhost + ", ghostDistances=" + ghostDistances + ", ghostsLastMoves="
				+ ghostsLastMoves + ", nearestPill=" + nearestPill + ", nearestPPill=" + nearestPPill
				+ ", remainingPills=" + remainingPills + ", remainingPowerPills=" + remainingPowerPills + ", score="
				+ score + ", time=" + time + "]";
	}
}
