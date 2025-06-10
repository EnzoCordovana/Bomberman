package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.model.common.Position;

/**
 * Contrôleur pour la gestion de la carte de jeu.
 */
public class MapController {
    private final GameMap map;

    public MapController(GameMap map) {
        this.map = map;
    }

    public boolean placeBomb(Position position) {
        return map.placeBomb(position);
    }

    public void explodeBomb(Position position) {
        map.explodeBomb(position);
    }

    public void update() {
        map.updateExplosions();
    }

    public void reset() {
        map.reset();
    }

    public GameMap getMap() {
        return map;
    }
}