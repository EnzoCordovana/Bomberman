package fr.amu.iut.bomberman.model.map;

import fr.amu.iut.bomberman.model.common.Position;

/**
 * Représente une tuile individuelle de la carte de jeu Bomberman.
 * Chaque tuile a un type, une position et des propriétés définissant son comportement.
 * Gère les différents états possibles : sol, mur, explosion, etc.
 */
public class Tile {

    /**
     * Énumération des différents types de tuiles possibles dans le jeu.
     */
    public enum TileType {
        /** Sol libre, traversable par les joueurs */
        FLOOR,

        /** Mur indestructible bloquant le passage et les explosions */
        WALL,

        /** Mur destructible, peut être détruit par les explosions */
        DESTRUCTIBLE_WALL,

        /** Emplacement d'une bombe (état temporaire) */
        BOMB,

        /** Zone d'explosion active, dangereuse pour les joueurs */
        EXPLOSION,

        /** Power-up récupérable par les joueurs */
        POWERUP
    }

    /** Position fixe de cette tuile sur la carte */
    private final Position position;

    /** Type actuel de la tuile */
    private TileType type;

    /** Indique si cette tuile est traversable par les joueurs */
    private boolean walkable;

    /** Indique si cette tuile peut être détruite par une explosion */
    private boolean destructible;

    /** Timer pour la durée de vie des explosions (en frames) */
    private int explosionTimer;

    /**
     * Constructeur d'une tuile.
     *
     * @param position Position de la tuile sur la carte
     * @param type Type initial de la tuile
     */
    public Tile(Position position, TileType type) {
        this.position = position;
        this.type = type;
        updateProperties();
    }

    /**
     * Met à jour les propriétés de la tuile selon son type.
     * Définit automatiquement si elle est traversable, destructible, etc.
     */
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

    /**
     * Retourne la position de cette tuile sur la carte.
     *
     * @return Position immuable de la tuile
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Retourne le type actuel de la tuile.
     *
     * @return Type de la tuile
     */
    public TileType getType() {
        return type;
    }

    /**
     * Indique si cette tuile peut être traversée par un joueur.
     *
     * @return true si la tuile est traversable
     */
    public boolean isWalkable() {
        return walkable;
    }

    /**
     * Indique si cette tuile peut être détruite par une explosion.
     *
     * @return true si la tuile est destructible
     */
    public boolean isDestructible() {
        return destructible;
    }

    /**
     * Retourne le temps restant pour une explosion sur cette tuile.
     *
     * @return Timer d'explosion en frames (0 si pas d'explosion)
     */
    public int getExplosionTimer() {
        return explosionTimer;
    }

    /**
     * Modifie le type de cette tuile et met à jour ses propriétés.
     *
     * @param type Nouveau type de la tuile
     */
    public void setType(TileType type) {
        this.type = type;
        updateProperties();
    }

    /**
     * Décrémente le timer d'explosion d'une unité.
     * Utilisé pour gérer la durée de vie des explosions.
     */
    public void decrementExplosionTimer() {
        if (explosionTimer > 0) {
            explosionTimer--;
        }
    }

    /**
     * Représentation textuelle de la tuile pour le debug.
     *
     * @return Description de la tuile
     */
    @Override
    public String toString() {
        return String.format("Tile{pos=%s, type=%s, walkable=%s}",
                position, type, walkable);
    }
}