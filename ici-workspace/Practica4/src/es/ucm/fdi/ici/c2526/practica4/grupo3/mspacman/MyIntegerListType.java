package es.ucm.fdi.ici.c2526.practica4.grupo3.mspacman;

import java.util.ArrayList;
import java.util.List;

public class MyIntegerListType implements es.ucm.fdi.gaia.jcolibri.connector.TypeAdaptor {

	private List<Integer> internalList;

	public MyIntegerListType() {
		internalList = new ArrayList<>();
	}

	public MyIntegerListType(List<Integer> l) {
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
			internalList.add(Integer.parseInt(x.trim()));
		}
	}

	@Override
	public String toString() {
		if (internalList == null || internalList.isEmpty())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < internalList.size(); i++) {
			sb.append(internalList.get(i));
			if (i < internalList.size() - 1)
				sb.append("/");
		}

		sb.append("]");
		return sb.toString();
	}

	/**
	 * Compara listas por contenido.
	 */

	@Override
	public boolean equals(Object o) {
		MyIntegerListType other = (MyIntegerListType) o;
		return this.internalList.equals(other.internalList);
	}

	public List<Integer> getList() {
		return internalList;
	}

	public void setList(List<Integer> list) {
		this.internalList = list;
	}
}
