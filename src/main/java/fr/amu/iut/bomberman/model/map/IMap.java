package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Interface pour les opérations de base d'une carte de jeu
 */
public interface IMap {

    /**
     * Initialise la carte avec les dimensions spécifiées
     */
    void initialize(int width, int height);

    /**
     * Récupère une tuile à une position donnée
     */
    Tile getTile(Position position);

    /**
     * Récupère une tuile à des coordonnées données
     */
    Tile getTile(int x, int y);

    /**
     * Place une tuile à une position donnée
     */
    void setTile(Position position, Tile tile);

    /**
     * Place une tuile à des coordonnées données
     */
    void setTile(int x, int y, Tile tile);

    /**
     * @return La largeur de la carte
     */
    int getWidth();

    /**
     * @return La hauteur de la carte
     */
    int getHeight();

    /**
     * Vérifie si une position est valide dans la carte
     */
    default boolean isValidPosition(Position position) {
        return isValidPosition(position.getX(), position.getY());
    }

    /**
     * Vérifie si des coordonnées sont valides dans la carte
     */
    default boolean isValidPosition(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Place une bombe à la position spécifiée
     */
    boolean placeBomb(Position position);

    /**
     * Fait exploser une bombe à la position spécifiée
     */
    void explodeBomb(Position position);

    /**
     * Met à jour les explosions sur la carte
     */
    void updateExplosions();

    /**
     * Remet la carte dans son état initial
     */
    void reset();

    /**
     * Vérifie si une position est traversable par un joueur
     */
    default boolean isWalkable(Position position) {
        Tile tile = getTile(position);
        return tile != null && tile.isWalkable();
    }

    /**
     * Vérifie si une position est traversable par un joueur
     */
    default boolean isWalkable(int x, int y) {
        return isWalkable(new Position(x, y));
    }
}