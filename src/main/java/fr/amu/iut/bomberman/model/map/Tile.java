package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Tile corrigé pour MVP Bomberman
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
        switch (type) {
            case FLOOR:
            case POWERUP:
                this.walkable = true;
                this.destructible = false;
                this.explosionTimer = 0;
                break;
            case WALL:
                this.walkable = false;
                this.destructible = false;
                this.explosionTimer = 0;
                break;
            case DESTRUCTIBLE_WALL:
                this.walkable = false;
                this.destructible = true;
                this.explosionTimer = 0;
                break;
            case BOMB:
                this.walkable = false;
                this.destructible = false;
                this.explosionTimer = 0;
                break;
            case EXPLOSION:
                this.walkable = true;
                this.destructible = false;
                this.explosionTimer = 60; // 60 frames à 60 FPS = 1 seconde
                break;
        }
    }

    // Getters
    public Position getPosition() {
        return position;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public int getExplosionTimer() {
        return explosionTimer;
    }

    // Setters
    public void setType(TileType type) {
        this.type = type;
        updateProperties();
    }

    public void decrementExplosionTimer() {
        if (explosionTimer > 0) {
            explosionTimer--;
        }
    }

    @Override
    public String toString() {
        return String.format("Tile{pos=%s, type=%s, walkable=%s}",
                position, type, walkable);
    }
}