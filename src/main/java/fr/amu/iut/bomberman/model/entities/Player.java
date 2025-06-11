package fr.amu.iut.bomberman.model.entities;

import javafx.scene.paint.Color;

/**
 * Joueur sans invincibilité - mort instantanée lors d'explosion
 */
public class Player {
    private int id;
    private double x, y; // Position en pixels
    private int gridX, gridY; // Position sur la grille
    private int lives;
    private int score;
    private int bombCount;
    private int maxBombs;
    private int explosionRange;
    private boolean alive;
    private Color color;
    private String name;

    private static final int CELL_SIZE = 32;

    public Player(int id, int startX, int startY, Color color, String name) {
        this.id = id;
        this.gridX = startX;
        this.gridY = startY;
        this.x = startX * CELL_SIZE + CELL_SIZE / 2.0;
        this.y = startY * CELL_SIZE + CELL_SIZE / 2.0;
        this.color = color;
        this.name = name;

        // Valeurs par défaut
        this.lives = 1; // MODIFIÉ: 1 seule vie = mort instantanée
        this.score = 0;
        this.bombCount = 0;
        this.maxBombs = 1;
        this.explosionRange = 2;
        this.alive = true;
    }

    public void update(double deltaTime, int dx, int dy) {
        if (!alive) return;
        // Plus de gestion d'invulnérabilité
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.gridX = (int) (x / CELL_SIZE);
        this.gridY = (int) (y / CELL_SIZE);
    }

    public boolean placeBomb() {
        if (!alive || bombCount >= maxBombs) {
            return false;
        }
        bombCount++;
        return true;
    }

    public void bombExploded() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    /**
     * MODIFIÉ: Mort instantanée, plus d'invincibilité
     */
    public void takeDamage() {
        if (!alive) return;

        lives = 0; // Mort instantanée
        alive = false;

        System.out.println("💀 " + name + " est mort instantanément!");
    }

    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Power-ups pour améliorer les capacités
     */
    public void increaseBombCapacity() {
        maxBombs++;
        System.out.println("💣 " + name + " peut maintenant placer " + maxBombs + " bombe(s)");
    }

    public void increaseExplosionRange() {
        explosionRange++;
        System.out.println("💥 Portée d'explosion de " + name + " augmentée à " + explosionRange);
    }

    public void increaseSpeed() {
        // Implémentation future pour la vitesse
        System.out.println("⚡ " + name + " est plus rapide!");
    }

    /**
     * Remet le joueur à sa position de départ (pour respawn éventuel)
     */
    public void respawn(int startX, int startY) {
        this.gridX = startX;
        this.gridY = startY;
        this.x = startX * CELL_SIZE + CELL_SIZE / 2.0;
        this.y = startY * CELL_SIZE + CELL_SIZE / 2.0;
        this.alive = true;
        this.lives = 1; // Une seule vie
        System.out.println("🔄 " + name + " respawn en (" + startX + "," + startY + ")");
    }

    // Getters
    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getBombCount() { return bombCount; }
    public int getMaxBombs() { return maxBombs; }
    public int getExplosionRange() { return explosionRange; }
    public boolean isAlive() { return alive; }
    public Color getColor() { return color; }
    public String getName() { return name; }

    // Setters
    public void setLives(int lives) { this.lives = lives; }
    public void setScore(int score) { this.score = score; }
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public void setExplosionRange(int explosionRange) { this.explosionRange = explosionRange; }
    public void setAlive(boolean alive) { this.alive = alive; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - Score: %d, Position: (%.1f, %.1f), Alive: %s",
                name, id, score, x, y, alive);
    }
}