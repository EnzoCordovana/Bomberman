package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente la carte de jeu du Bomberman avec sa logique de gestion.
 * Implémente l'interface IMap et gère la génération procédurale du terrain.
 * Responsable de la gestion des explosions, des collisions et de l'état de la carte.
 */
public class GameMap implements IMap {

    /** Grille bidimensionnelle représentant la carte */
    private Tile[][] tiles;

    /** Largeur de la carte en nombre de tuiles */
    private int width;

    /** Hauteur de la carte en nombre de tuiles */
    private int height;

    /** Liste des positions des murs destructibles pour optimisation */
    private final List<Position> destructibleWalls = new ArrayList<>();

    /**
     * Constructeur de la carte de jeu.
     * Initialise une nouvelle carte avec les dimensions spécifiées.
     *
     * @param width Largeur de la carte en tuiles
     * @param height Hauteur de la carte en tuiles
     */
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        initialize(width, height);
    }

    /**
     * {@inheritDoc}
     * Initialise la structure de la carte avec génération procédurale.
     */
    @Override
    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        initializeMap();
    }

    /**
     * Génère le contenu de la carte selon les règles du Bomberman.
     * Place les murs fixes, les zones de départ et les murs destructibles aléatoires.
     */
    private void initializeMap() {
        destructibleWalls.clear();
        Random random = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);

                // Bords indestructibles
                if (isBorder(x, y)) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.WALL);
                }
                // Murs fixes indestructibles (pattern en damier)
                else if (x % 2 == 0 && y % 2 == 0) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.WALL);
                }
                // Zones de départ des joueurs (toujours libres)
                else if (isStartingArea(pos)) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.FLOOR);
                }
                // Murs destructibles aléatoires (70% de chance)
                else if (random.nextDouble() < 0.7) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.DESTRUCTIBLE_WALL);
                    destructibleWalls.add(pos);
                }
                // Sol libre
                else {
                    tiles[y][x] = new Tile(pos, Tile.TileType.FLOOR);
                }
            }
        }
    }

    /**
     * Vérifie si une position correspond à un bord de la carte.
     *
     * @param x Coordonnée X à vérifier
     * @param y Coordonnée Y à vérifier
     * @return true si la position est sur un bord
     */
    private boolean isBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    /**
     * Vérifie si une position fait partie d'une zone de départ de joueur.
     * Les zones de départ sont toujours libres de murs destructibles.
     *
     * @param position Position à vérifier
     * @return true si la position est une zone de départ
     */
    private boolean isStartingArea(Position position) {
        int x = position.getX();
        int y = position.getY();

        // Zone joueur 1 (coin supérieur gauche)
        if ((x == 1 && y == 1) || (x == 1 && y == 2) || (x == 2 && y == 1)) {
            return true;
        }
        // Zone joueur 2 (coin supérieur droit)
        if ((x == width-2 && y == 1) || (x == width-2 && y == 2) || (x == width-3 && y == 1)) {
            return true;
        }
        // Zone joueur 3 (coin inférieur gauche)
        if ((x == 1 && y == height-2) || (x == 1 && y == height-3) || (x == 2 && y == height-2)) {
            return true;
        }
        // Zone joueur 4 (coin inférieur droit)
        if ((x == width-2 && y == height-2) || (x == width-2 && y == height-3) || (x == width-3 && y == height-2)) {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getTile(Position position) {
        return getTile(position.getX(), position.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTile(Position position, Tile tile) {
        setTile(position.getX(), position.getY(), tile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTile(int x, int y, Tile tile) {
        if (isValidPosition(x, y)) {
            tiles[y][x] = tile;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     * Vérifie que la position est libre pour placer une bombe.
     */
    @Override
    public boolean placeBomb(Position position) {
        Tile tile = getTile(position);
        if (tile != null && tile.isWalkable()) {
            // On ne change pas le type de tuile ici, juste on autorise
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * Déclenche une explosion en croix avec une portée configurable.
     */
    @Override
    public void explodeBomb(Position position) {
        // Explosion au centre
        Tile centerTile = getTile(position);
        if (centerTile != null) {
            centerTile.setType(Tile.TileType.EXPLOSION);
        }

        // Explosion en croix avec portée de 2
        int explosionRange = 2;
        explodeDirection(position, 1, 0, explosionRange);  // Droite
        explodeDirection(position, -1, 0, explosionRange); // Gauche
        explodeDirection(position, 0, 1, explosionRange);  // Bas
        explodeDirection(position, 0, -1, explosionRange); // Haut
    }

    /**
     * Propage l'explosion dans une direction donnée jusqu'à rencontrer un obstacle.
     *
     * @param start Position de départ de l'explosion
     * @param dx Direction X (-1, 0, ou 1)
     * @param dy Direction Y (-1, 0, ou 1)
     * @param range Portée maximale de l'explosion
     */
    private void explodeDirection(Position start, int dx, int dy, int range) {
        for (int i = 1; i <= range; i++) {
            int x = start.getX() + (dx * i);
            int y = start.getY() + (dy * i);

            Tile tile = getTile(x, y);
            if (tile == null || tile.getType() == Tile.TileType.WALL) {
                break; // Mur indestructible arrête l'explosion
            }

            if (tile.getType() == Tile.TileType.DESTRUCTIBLE_WALL) {
                tile.setType(Tile.TileType.EXPLOSION);
                destructibleWalls.remove(new Position(x, y));
                break; // Mur destructible arrête l'explosion
            }

            if (tile.getType() == Tile.TileType.FLOOR) {
                tile.setType(Tile.TileType.EXPLOSION);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Met à jour les timers d'explosion et nettoie les explosions terminées.
     */
    @Override
    public void updateExplosions() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];
                if (tile != null && tile.getType() == Tile.TileType.EXPLOSION) {
                    tile.decrementExplosionTimer();
                    if (tile.getExplosionTimer() <= 0) {
                        tile.setType(Tile.TileType.FLOOR);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * Remet la carte dans son état initial pour une nouvelle partie.
     */
    @Override
    public void reset() {
        destructibleWalls.clear();
        initializeMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}