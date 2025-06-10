package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Représente une tuile de la carte avec ses propriétés.
 */
public class Tile {
    public enum TileType {
        FLOOR, WALL, DESTRUCTIBLE_WALL, BOMB, EXPLOSION, POWERUP
    }

    private final Position position;
    private TileType type;
    private boolean walkable;
    private boolean destructible;
    private int explosionTimer;

    public Tile(Position position, TileType type) {
        this.position = position;
        this.type = type;
        updateProperties();
    }

    private void updateProperties() {
        this.walkable = type == TileType.FLOOR || type == TileType.POWERUP;
        this.destructible = type == TileType.DESTRUCTIBLE_WALL;
        this.explosionTimer = type == TileType.EXPLOSION ? 3 : 0;
    }

    // Getters
    public Position getPosition() { return position; }
    public TileType getType() { return type; }
    public boolean isWalkable() { return walkable; }
    public boolean isDestructible() { return destructible; }
    public int getExplosionTimer() { return explosionTimer; }

    // Setters
    public void setType(TileType type) {
        this.type = type;
        updateProperties();
    }

    public void decrementExplosionTimer() {
        if (explosionTimer > 0) explosionTimer--;
    }
}