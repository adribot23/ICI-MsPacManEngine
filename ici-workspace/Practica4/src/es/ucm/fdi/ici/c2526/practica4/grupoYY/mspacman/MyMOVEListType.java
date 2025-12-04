package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.connector.TypeAdaptor;
import pacman.game.Constants.MOVE;

public class MyMOVEListType implements TypeAdaptor {

	private List<MOVE> internalList;

	public MyMOVEListType() {
		internalList = new ArrayList<>();
	}

	public MyMOVEListType(List<MOVE> l) {
		internalList = l;
	}

	@Override
	public void fromString(String content) throws Exception {
		internalList = new ArrayList<>();

		// Quitar corchetes
		String s = content.substring(1, content.length() - 1).trim();

		if (s.isEmpty())
			return;

		for (String x : s.split("/")) {
			internalList.add(MOVE.valueOf(x.trim()));
		}
	}

	@Override
	public String toString() {
		if (internalList == null || internalList.isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < internalList.size(); i++) {
			sb.append(internalList.get(i).toString());
			if (i < internalList.size() - 1)
				sb.append("/");
		}

		sb.append("]");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		try {
			MyMOVEListType other = (MyMOVEListType) o;
			return this.internalList.equals(other.internalList);
		} catch (Exception e) {
			return false;
		}
	}

	public List<MOVE> getList() {
		return internalList;
	}

	public void setList(List<MOVE> list) {
		this.internalList = list;
	}
}
