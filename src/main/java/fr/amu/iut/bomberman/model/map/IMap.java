package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Interface définissant les opérations de base pour une carte de jeu.
 * Respecte le principe de ségrégation des interfaces (ISP).
 */
public interface IMap {

    /**
     * Initialise la carte avec les dimensions spécifiées.
     * @param width Largeur de la carte
     * @param height Hauteur de la carte
     */
    void initialize(int width, int height);

    /**
     * Récupère une tuile à une position donnée.
     * @param position Position de la tuile
     * @return La tuile à cette position, null si hors limites
     */
    Tile getTile(Position position);

    /**
     * Récupère une tuile à des coordonnées données.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return La tuile à cette position, null si hors limites
     */
    Tile getTile(int x, int y);

    /**
     * Place une tuile à une position donnée.
     * @param position Position où placer la tuile
     * @param tile La tuile à placer
     */
    void setTile(Position position, Tile tile);

    /**
     * Place une tuile à des coordonnées données.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @param tile La tuile à placer
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
     * Vérifie si une position est valide dans la carte.
     * @param position Position à vérifier
     * @return true si la position est valide
     */
    default boolean isValidPosition(Position position) {
        return isValidPosition(position.getX(), position.getY());
    }

    /**
     * Vérifie si des coordonnées sont valides dans la carte.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si la position est valide
     */
    default boolean isValidPosition(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Place une bombe à la position spécifiée.
     * @param position Position où placer la bombe
     * @return true si la bombe a été placée avec succès
     */
    boolean placeBomb(Position position);

    /**
     * Fait exploser une bombe à la position spécifiée.
     * @param position Position de la bombe à faire exploser
     */
    void explodeBomb(Position position);

    /**
     * Met à jour les explosions sur la carte.
     * Appelé à chaque frame pour gérer la durée des explosions.
     */
    void updateExplosions();

    /**
     * Remet la carte dans son état initial.
     */
    void reset();

    /**
     * Vérifie si une position est traversable par un joueur.
     * @param position Position à vérifier
     * @return true si la position est traversable
     */
    default boolean isWalkable(Position position) {
        Tile tile = getTile(position);
        return tile != null && tile.isWalkable();
    }

    /**
     * Vérifie si une position est traversable par un joueur.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si la position est traversable
     */
    default boolean isWalkable(int x, int y) {
        return isWalkable(new Position(x, y));
    }
}