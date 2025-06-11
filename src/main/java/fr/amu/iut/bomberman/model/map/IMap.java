package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Interface définissant les opérations de base d'une carte de jeu Bomberman.
 * Fournit les méthodes essentielles pour la manipulation des tuiles,
 * la gestion des bombes et des explosions, ainsi que les vérifications de validité.
 */
public interface IMap {

    /**
     * Initialise la carte avec les dimensions spécifiées.
     *
     * @param width Largeur de la carte en tuiles
     * @param height Hauteur de la carte en tuiles
     */
    void initialize(int width, int height);

    /**
     * Récupère une tuile à une position donnée.
     *
     * @param position Position de la tuile à récupérer
     * @return La tuile à cette position, ou null si invalide
     */
    Tile getTile(Position position);

    /**
     * Récupère une tuile à des coordonnées données.
     *
     * @param x Coordonnée X de la tuile
     * @param y Coordonnée Y de la tuile
     * @return La tuile à ces coordonnées, ou null si invalides
     */
    Tile getTile(int x, int y);

    /**
     * Place une tuile à une position donnée.
     *
     * @param position Position où placer la tuile
     * @param tile Tuile à placer
     */
    void setTile(Position position, Tile tile);

    /**
     * Place une tuile à des coordonnées données.
     *
     * @param x Coordonnée X où placer la tuile
     * @param y Coordonnée Y où placer la tuile
     * @param tile Tuile à placer
     */
    void setTile(int x, int y, Tile tile);

    /**
     * Retourne la largeur de la carte en nombre de tuiles.
     *
     * @return La largeur de la carte
     */
    int getWidth();

    /**
     * Retourne la hauteur de la carte en nombre de tuiles.
     *
     * @return La hauteur de la carte
     */
    int getHeight();

    /**
     * Vérifie si une position est valide dans les limites de la carte.
     *
     * @param position Position à vérifier
     * @return true si la position est dans les limites
     */
    default boolean isValidPosition(Position position) {
        return isValidPosition(position.getX(), position.getY());
    }

    /**
     * Vérifie si des coordonnées sont valides dans les limites de la carte.
     *
     * @param x Coordonnée X à vérifier
     * @param y Coordonnée Y à vérifier
     * @return true si les coordonnées sont dans les limites
     */
    default boolean isValidPosition(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * Tente de placer une bombe à la position spécifiée.
     *
     * @param position Position où placer la bombe
     * @return true si la bombe a pu être placée
     */
    boolean placeBomb(Position position);

    /**
     * Déclenche l'explosion d'une bombe à la position spécifiée.
     * Propage l'explosion selon les règles du jeu.
     *
     * @param position Position de l'explosion
     */
    void explodeBomb(Position position);

    /**
     * Met à jour l'état des explosions sur la carte.
     * Gère les timers et nettoie les explosions terminées.
     */
    void updateExplosions();

    /**
     * Remet la carte dans son état initial.
     * Utilisé pour recommencer une partie.
     */
    void reset();

    /**
     * Vérifie si une position est traversable par un joueur.
     *
     * @param position Position à vérifier
     * @return true si un joueur peut se déplacer sur cette position
     */
    default boolean isWalkable(Position position) {
        Tile tile = getTile(position);
        return tile != null && tile.isWalkable();
    }

    /**
     * Vérifie si des coordonnées correspondent à une position traversable.
     *
     * @param x Coordonnée X à vérifier
     * @param y Coordonnée Y à vérifier
     * @return true si un joueur peut se déplacer sur cette position
     */
    default boolean isWalkable(int x, int y) {
        return isWalkable(new Position(x, y));
    }
}