package fr.amu.iut.bomberman.model.common;

import java.util.Objects;

/**
 * Classe immutable représentant une position dans le jeu avec des coordonnées x et y.
 * Fournit des méthodes utilitaires pour les calculs de distance et de déplacement.
 * Utilisée pour représenter les positions sur la grille de jeu.
 */
public class Position {

    /** Coordonnée X (finale, immutable) */
    private final int x;

    /** Coordonnée Y (finale, immutable) */
    private final int y;

    /**
     * Constructeur d'une position.
     *
     * @param x Coordonnée X
     * @param y Coordonnée Y
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la coordonnée X de cette position.
     *
     * @return Coordonnée X
     */
    public int getX() {
        return x;
    }

    /**
     * Retourne la coordonnée Y de cette position.
     *
     * @return Coordonnée Y
     */
    public int getY() {
        return y;
    }

    /**
     * Calcule la distance de Manhattan entre cette position et une autre.
     * La distance de Manhattan est la somme des différences absolues des coordonnées.
     *
     * @param other L'autre position
     * @return La distance de Manhattan
     */
    public int manhattanDistance(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Vérifie si cette position est adjacente à une autre position.
     * Deux positions sont adjacentes si leur distance de Manhattan est de 1.
     *
     * @param other L'autre position
     * @return true si les positions sont adjacentes
     */
    public boolean isAdjacentTo(Position other) {
        return manhattanDistance(other) == 1;
    }

    /**
     * Crée une nouvelle position en ajoutant un décalage à cette position.
     *
     * @param dx Décalage en x
     * @param dy Décalage en y
     * @return Nouvelle position décalée
     */
    public Position offset(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    /**
     * Retourne la position au nord de cette position (y-1).
     *
     * @return Position au nord
     */
    public Position north() {
        return offset(0, -1);
    }

    /**
     * Retourne la position au sud de cette position (y+1).
     *
     * @return Position au sud
     */
    public Position south() {
        return offset(0, 1);
    }

    /**
     * Retourne la position à l'est de cette position (x+1).
     *
     * @return Position à l'est
     */
    public Position east() {
        return offset(1, 0);
    }

    /**
     * Retourne la position à l'ouest de cette position (x-1).
     *
     * @return Position à l'ouest
     */
    public Position west() {
        return offset(-1, 0);
    }

    /**
     * Vérifie l'égalité avec un autre objet.
     * Deux positions sont égales si elles ont les mêmes coordonnées.
     *
     * @param obj Objet à comparer
     * @return true si les positions sont égales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    /**
     * Calcule le code de hachage de cette position.
     *
     * @return Code de hachage basé sur les coordonnées
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Représentation textuelle de la position.
     *
     * @return Chaîne au format "Position(x, y)"
     */
    @Override
    public String toString() {
        return String.format("Position(%d, %d)", x, y);
    }
}