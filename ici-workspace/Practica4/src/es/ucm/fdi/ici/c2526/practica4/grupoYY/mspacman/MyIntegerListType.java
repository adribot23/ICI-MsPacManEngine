package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.ArrayList;
import java.util.List;

/**
 * MyIntegerListType.java
 * Adaptación para listas de Integer con formato [1, 2, 3]
 * Compatible con jCOLIBRI2.
 */

public class MyIntegerListType implements es.ucm.fdi.gaia.jcolibri.connector.TypeAdaptor {

    private List<Integer> internalList;

    public MyIntegerListType(List<Integer> l) {
        internalList = l;
    }

    /**
     * Convierte un String con formato [1, 2, 3] en una lista de Integer.
     */
    @Override
    public void fromString(String content) throws Exception {
    	internalList = new ArrayList<>();
        content = content.trim();
        if (content.startsWith("[")) content = content.substring(1);
        if (content.endsWith("]")) content = content.substring(0, content.length() - 1);
        
        if (!content.isEmpty()) {
            for (String s : content.split(",")) {
                internalList.add(Integer.parseInt(s.trim()));
            }
        }
    }

    /**
     * Convierte la lista a un String con formato [1, 2, 3]
     */
    @Override
    public String toString() {
        if (internalList == null || internalList.isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < internalList.size(); i++) {
            sb.append(internalList.get(i));
            if (i < internalList.size() - 1)
                sb.append(", ");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Compara listas por contenido.
     */
    
    @Override
    public boolean equals(Object o) {
        try {
            MyIntegerListType other = (MyIntegerListType) o;
            return this.internalList.equals(other.internalList);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Integer> getList() {
        return internalList;
    }

    public void setList(List<Integer> list) {
        this.internalList = list;
    }
}
