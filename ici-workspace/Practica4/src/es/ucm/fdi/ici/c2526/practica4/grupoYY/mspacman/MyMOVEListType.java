package es.ucm.fdi.ici.c2526.practica4.grupoYY.mspacman;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gaia.jcolibri.connector.TypeAdaptor;
import pacman.game.Constants.MOVE;

/**
 * Adaptador para listas de MOVE usando jCOLIBRI.
 * Almacena los valores como Strings (por ejemplo: [UP, LEFT, DOWN]).
 */
public class MyMOVEListType implements TypeAdaptor {

    private List<MOVE> internalList;

    public MyMOVEListType(List<MOVE> l) {
        internalList = l;
    }

    /**
     * Lee una cadena con formato [UP, LEFT, DOWN]
     */
    @Override
    public void fromString(String content) throws Exception {
    	internalList = new ArrayList<>();
        content = content.trim();
        if (content.startsWith("[")) content = content.substring(1);
        if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

        if (!content.isEmpty()) {
            for (String s : content.split(",")) {
                internalList.add(MOVE.valueOf(s.trim()));
            }
        }
    }

    /**
     * Convierte la lista a un String con formato [UP, LEFT, DOWN]
     */
    @Override
    public String toString() {
        if (internalList == null || internalList.isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < internalList.size(); i++) {
            sb.append(internalList.get(i).toString());
            if (i < internalList.size() - 1)
                sb.append(", ");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Comparación por contenido (necesario para jCOLIBRI)
     */
    @Override
    public boolean equals(Object o) {
        try {
            MyMOVEListType other = (MyMOVEListType) o;
            return this.internalList.equals(other.internalList);
        } catch (Exception e) {
            return false;
        }
    }

    // getters y setters normales
    public List<MOVE> getList() {
        return internalList;
    }

    public void setList(List<MOVE> list) {
        this.internalList = list;
    }
}
