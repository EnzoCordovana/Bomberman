package fr.amu.iut.bomberman.model.map;

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
     * Récupère un bloc à une position donnée.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return Le bloc à cette position, null si hors limites
     */
    Block getBlock(int x, int y);

    /**
     * Place un bloc à une position donnée.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @param block Le bloc à placer
     */
    void setBlock(int x, int y, Block block);

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
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si la position est valide
     */
    default boolean isValidPosition(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }
}