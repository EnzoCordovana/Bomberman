package fr.amu.iut.bomberman.model.map;

/**
 * Représente la carte de jeu du Bomberman.
 * Gère la disposition des blocs selon le principe Single Responsibility.
 */
public class Map implements IMap {
    private Block[][] grid;
    private int width;
    private int height;

    /**
     * Initialise la carte avec des dimensions spécifiées.
     * Crée une carte typique de Bomberman avec des blocs indestructibles sur les bordures
     * et en damier, et des blocs destructibles aléatoirement placés.
     *
     * @param width Largeur de la carte (doit être impaire pour le gameplay classique)
     * @param height Hauteur de la carte (doit être impaire pour le gameplay classique)
     */
    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Block[width][height];

        createBombermanMap();
    }

    /**
     * Crée une carte classique de Bomberman.
     */
    private void createBombermanMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isBorder(x, y) || isIndestructiblePattern(x, y)) {
                    // Blocs indestructibles sur les bordures et en damier
                    grid[x][y] = new IndestructibleBlock(x, y);
                } else if (isStartingArea(x, y)) {
                    // Zones de départ des joueurs (coins) restent vides
                    grid[x][y] = new EmptyBlock(x, y);
                } else if (Math.random() < 0.7) {
                    // 70% de chance d'avoir un bloc destructible
                    grid[x][y] = new DestructibleBlock(x, y);
                } else {
                    // Bloc vide
                    grid[x][y] = new EmptyBlock(x, y);
                }
            }
        }
    }

    /**
     * Vérifie si une position est sur la bordure de la carte.
     */
    private boolean isBorder(int x, int y) {
        return x == 0 || y == 0 || x == width - 1 || y == height - 1;
    }

    /**
     * Vérifie si une position doit contenir un bloc indestructible selon le pattern classique.
     * Les blocs indestructibles sont placés en damier sur les positions paires.
     */
    private boolean isIndestructiblePattern(int x, int y) {
        return x % 2 == 0 && y % 2 == 0;
    }

    /**
     * Vérifie si une position est dans la zone de départ d'un joueur.
     * Les zones de départ sont dans les coins et leurs cases adjacentes.
     */
    private boolean isStartingArea(int x, int y) {
        // Coin supérieur gauche (joueur 1)
        if ((x == 1 && y == 1) || (x == 1 && y == 2) || (x == 2 && y == 1)) {
            return true;
        }
        // Coin supérieur droit (joueur 2)
        if ((x == width - 2 && y == 1) || (x == width - 2 && y == 2) || (x == width - 3 && y == 1)) {
            return true;
        }
        // Coin inférieur gauche (joueur 3)
        if ((x == 1 && y == height - 2) || (x == 1 && y == height - 3) || (x == 2 && y == height - 2)) {
            return true;
        }
        // Coin inférieur droit (joueur 4)
        if ((x == width - 2 && y == height - 2) || (x == width - 2 && y == height - 3) || (x == width - 3 && y == height - 2)) {
            return true;
        }
        return false;
    }

    public Block getBlock(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    public void setBlock(int x, int y, Block block) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = block;
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}