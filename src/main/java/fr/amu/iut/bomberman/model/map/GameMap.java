package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de la carte de jeu Bomberman.
 * Implémente l'interface IMap pour respecter l'architecture existante.
 */
public class GameMap implements IMap {
    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_HEIGHT = 13;

    private Tile[][] tiles;
    private int width;
    private int height;
    private final List<Position> destructibleWalls = new ArrayList<>();
    private final List<Position> bombs = new ArrayList<>();

    public GameMap() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        initialize(width, height);
    }

    @Override
    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        initializeMap();
    }

    private void initializeMap() {
        destructibleWalls.clear();
        bombs.clear();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);

                // Bords indestructibles
                if (isBorder(x, y)) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.WALL);
                }
                // Murs destructibles (pattern classique)
                else if (x % 2 == 0 && y % 2 == 0) {
                    tiles[y][x] = new Tile(pos, Tile.TileType.DESTRUCTIBLE_WALL);
                    destructibleWalls.add(pos);
                }
                // Sol
                else {
                    tiles[y][x] = new Tile(pos, Tile.TileType.FLOOR);
                }
            }
        }
    }

    private boolean isBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    // Implémentation de IMap
    @Override
    public Tile getTile(Position position) {
        return getTile(position.getX(), position.getY());
    }

    @Override
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[y][x];
        }
        return null;
    }

    @Override
    public void setTile(Position position, Tile tile) {
        setTile(position.getX(), position.getY(), tile);
    }

    @Override
    public void setTile(int x, int y, Tile tile) {
        if (isValidPosition(x, y)) {
            tiles[y][x] = tile;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean placeBomb(Position position) {
        Tile tile = getTile(position);
        if (tile != null && tile.isWalkable()) {
            tile.setType(Tile.TileType.BOMB);
            bombs.add(position);
            return true;
        }
        return false;
    }

    @Override
    public void explodeBomb(Position position) {
        Tile bombTile = getTile(position);
        if (bombTile != null && bombTile.getType() == Tile.TileType.BOMB) {
            bombTile.setType(Tile.TileType.EXPLOSION);
            bombs.remove(position);

            // Explosion en croix avec portée limitée
            int explosionRange = 2; // Portée de l'explosion
            explodeDirection(position, 1, 0, explosionRange);  // Droite
            explodeDirection(position, -1, 0, explosionRange); // Gauche
            explodeDirection(position, 0, 1, explosionRange);  // Bas
            explodeDirection(position, 0, -1, explosionRange); // Haut
        }
    }

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
                break; // Mur destructible arrête l'explosion après destruction
            }

            // Continuer l'explosion sur les cases vides
            if (tile.getType() == Tile.TileType.FLOOR) {
                tile.setType(Tile.TileType.EXPLOSION);
            }
        }
    }

    @Override
    public void updateExplosions() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];
                if (tile.getType() == Tile.TileType.EXPLOSION) {
                    tile.decrementExplosionTimer();
                    if (tile.getExplosionTimer() <= 0) {
                        tile.setType(Tile.TileType.FLOOR);
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        destructibleWalls.clear();
        bombs.clear();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (isBorder(x, y)) {
                    tiles[y][x].setType(Tile.TileType.WALL);
                } else if (x % 2 == 0 && y % 2 == 0) {
                    tiles[y][x].setType(Tile.TileType.DESTRUCTIBLE_WALL);
                    destructibleWalls.add(pos);
                } else {
                    tiles[y][x].setType(Tile.TileType.FLOOR);
                }
            }
        }
    }

    // Méthodes utilitaires supplémentaires

    /**
     * Récupère toutes les positions des bombes actuellement sur la carte.
     * @return Liste des positions des bombes
     */
    public List<Position> getBombPositions() {
        return new ArrayList<>(bombs);
    }

    /**
     * Récupère toutes les positions des murs destructibles.
     * @return Liste des positions des murs destructibles
     */
    public List<Position> getDestructibleWalls() {
        return new ArrayList<>(destructibleWalls);
    }

    /**
     * Compte le nombre de murs destructibles restants.
     * @return Nombre de murs destructibles
     */
    public int getDestructibleWallCount() {
        return destructibleWalls.size();
    }

    /**
     * Vérifie s'il y a une bombe à la position spécifiée.
     * @param position Position à vérifier
     * @return true s'il y a une bombe à cette position
     */
    public boolean hasBomb(Position position) {
        return bombs.contains(position);
    }

    /**
     * Trouve toutes les positions libres (sol) sur la carte.
     * @return Liste des positions libres
     */
    public List<Position> getFreePositions() {
        List<Position> freePositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];
                if (tile.getType() == Tile.TileType.FLOOR) {
                    freePositions.add(new Position(x, y));
                }
            }
        }
        return freePositions;
    }

    /**
     * Vérifie si la position est dans une zone de départ de joueur.
     * Les zones de départ sont dans les coins et doivent rester libres.
     * @param position Position à vérifier
     * @return true si c'est une zone de départ
     */
    public boolean isStartingArea(Position position) {
        int x = position.getX();
        int y = position.getY();

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
}