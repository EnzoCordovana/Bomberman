package fr.amu.iut.bomberman.model.common;

import java.util.Objects;

/**
 * Représente une position dans le jeu avec des coordonnées x et y.
 */
public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Calcule la distance de Manhattan entre cette position et une autre.
     * @param other L'autre position
     * @return La distance de Manhattan
     */
    public int manhattanDistance(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Vérifie si cette position est adjacente à une autre position.
     * @param other L'autre position
     * @return true si les positions sont adjacentes
     */
    public boolean isAdjacentTo(Position other) {
        return manhattanDistance(other) == 1;
    }

    /**
     * Crée une nouvelle position en ajoutant un décalage.
     * @param dx Décalage en x
     * @param dy Décalage en y
     * @return Nouvelle position
     */
    public Position offset(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    /**
     * Retourne la position au nord (y-1).
     */
    public Position north() {
        return offset(0, -1);
    }

    /**
     * Retourne la position au sud (y+1).
     */
    public Position south() {
        return offset(0, 1);
    }

    /**
     * Retourne la position à l'est (x+1).
     */
    public Position east() {
        return offset(1, 0);
    }

    /**
     * Retourne la position à l'ouest (x-1).
     */
    public Position west() {
        return offset(-1, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Position(%d, %d)", x, y);
    }
}