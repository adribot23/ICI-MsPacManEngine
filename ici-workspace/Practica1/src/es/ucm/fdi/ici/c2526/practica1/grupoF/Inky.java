package es.ucm.fdi.ici.c2526.practica1.grupoF;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import pacman.game.Game;
import pacman.game.GameView;

public class Inky implements GhostState {

	@Override
	public MOVE action(Game game, GHOST ghost, int posPacman) {

		int start = game.getGhostCurrentNodeIndex(ghost);
		int target = posPacman;

		// Calcula un camino evitando nodos ocupados por fantasmas
		List<Integer> path = bfsAvoidingGhosts(game, start, target, ghost);

		if (path.isEmpty()) {
			return MOVE.NEUTRAL;
		}

		// Primer paso del camino

		int nextNode = path.get(0);

		int[] arrayPath = new int[path.size()];
		for (int i = 0; i < path.size(); i++)
			arrayPath[i] = path.get(i);

		GameView.addPoints(game, Color.BLUE, arrayPath);
		return game.getMoveToMakeToReachDirectNeighbour(start, nextNode);
	}

	private List<Integer> bfsAvoidingGhosts(Game game, int start, int target, GHOST self) {
		Queue<List<Integer>> queue = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();

		// Inicializa la cola con un "camino vacío"
		List<Integer> startPath = new ArrayList<>();
		queue.add(startPath);
		visited.add(start);

		while (!queue.isEmpty()) {
			List<Integer> path = queue.poll();
			int current = (path.isEmpty() ? start : path.get(path.size() - 1));

			// Si hemos llegado al objetivo (posPacman) devolver camino
			if (current == target) {
				return path;
			}

			// Expande vecinos
			for (MOVE move : game.getPossibleMoves(current)) {
				int next = game.getNeighbour(current, move);

				if (next != -1 && !visited.contains(next) && !isNodeOccupiedByGhost(game, next, self)) {
					visited.add(next);

					// Construir un nuevo camino extendiendo el actual
					List<Integer> newPath = new ArrayList<>(path);
					newPath.add(next);
					queue.add(newPath);
				}
			}
		}

		// No se encontró ningún camino válido
		return Collections.emptyList();
	}

	private boolean isNodeOccupiedByGhost(Game game, int node, GHOST self) {
		for (GHOST g : GHOST.values()) {
			if (g != self && game.getGhostCurrentNodeIndex(g) == node) {
				return true;
			}
		}
		return false;
	}
}