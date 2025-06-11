package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameMap corrigé pour MVP Bomberman
 */
public class GameMap implements IMap {
    private Tile[][] tiles;
    private int width;
    private int height;
    private final List<Position> destructibleWalls = new ArrayList<>();

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

    private boolean isBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

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
            // On ne change pas le type de tuile ici, juste on autorise
            return true;
        }
        return false;
    }

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

    @Override
    public void reset() {
        destructibleWalls.clear();
        initializeMap();
    }

    // Méthodes de vérification héritées de IMap
    @Override
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}