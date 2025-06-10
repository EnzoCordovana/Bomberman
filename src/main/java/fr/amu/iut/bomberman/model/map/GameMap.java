package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de la carte de jeu Bomberman.
 */
public class GameMap {
    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_HEIGHT = 13;

    private final Tile[][] tiles;
    private final int width;
    private final int height;
    private final List<Position> destructibleWalls = new ArrayList<>();
    private final List<Position> bombs = new ArrayList<>();

    public GameMap() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        initializeMap();
    }

    private void initializeMap() {
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

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Tile getTile(Position position) {
        return getTile(position.getX(), position.getY());
    }
    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y][x];
        }
        return null;
    }

    // Méthodes de jeu
    public boolean placeBomb(Position position) {
        Tile tile = getTile(position);
        if (tile != null && tile.isWalkable()) {
            tile.setType(Tile.TileType.BOMB);
            bombs.add(position);
            return true;
        }
        return false;
    }

    public void explodeBomb(Position position) {
        Tile bombTile = getTile(position);
        if (bombTile != null && bombTile.getType() == Tile.TileType.BOMB) {
            bombTile.setType(Tile.TileType.EXPLOSION);
            bombs.remove(position);

            // Explosion en croix
            explodeDirection(position, 1, 0);  // Droite
            explodeDirection(position, -1, 0); // Gauche
            explodeDirection(position, 0, 1);  // Bas
            explodeDirection(position, 0, -1); // Haut
        }
    }

    private void explodeDirection(Position start, int dx, int dy) {
        int x = start.getX() + dx;
        int y = start.getY() + dy;

        while (true) {
            Tile tile = getTile(x, y);
            if (tile == null || tile.getType() == Tile.TileType.WALL) {
                break;
            }

            if (tile.isDestructible()) {
                tile.setType(Tile.TileType.EXPLOSION);
                destructibleWalls.remove(new Position(x, y));
                break;
            }

            tile.setType(Tile.TileType.EXPLOSION);
            x += dx;
            y += dy;
        }
    }

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

    public void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                if (isBorder(x, y)) {
                    tiles[y][x].setType(Tile.TileType.WALL);
                } else if (x % 2 == 0 && y % 2 == 0) {
                    tiles[y][x].setType(Tile.TileType.DESTRUCTIBLE_WALL);
                } else {
                    tiles[y][x].setType(Tile.TileType.FLOOR);
                }
            }
        }
        bombs.clear();
        destructibleWalls.clear();
    }
}