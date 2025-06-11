package fr.amu.iut.bomberman.model.entities;

import javafx.scene.paint.Color;

/**
 * Représente un joueur dans le jeu Bomberman.
 * Gère la position, les statistiques, les capacités et l'état vital du joueur.
 * Implémente une logique de mort instantanée lors d'explosion (pas d'invincibilité).
 */
public class Player {

    /** Identifiant unique du joueur */
    private int id;

    /** Position du joueur en pixels pour l'affichage */
    private double x, y;

    /** Position du joueur sur la grille de jeu */
    private int gridX, gridY;

    /** Nombre de vies restantes */
    private int lives;

    /** Score accumulé pendant la partie */
    private int score;

    /** Nombre de bombes actuellement placées */
    private int bombCount;

    /** Nombre maximum de bombes que le joueur peut placer */
    private int maxBombs;

    /** Portée des explosions des bombes du joueur */
    private int explosionRange;

    /** Indique si le joueur est encore vivant */
    private boolean alive;

    /** Couleur d'affichage du joueur */
    private Color color;

    /** Nom du joueur */
    private String name;

    /** Taille d'une cellule en pixels pour les conversions */
    private static final int CELL_SIZE = 32;

    /**
     * Constructeur d'un joueur.
     *
     * @param id Identifiant unique du joueur
     * @param startX Position de départ X sur la grille
     * @param startY Position de départ Y sur la grille
     * @param color Couleur d'affichage du joueur
     * @param name Nom du joueur
     */
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

    /**
     * Met à jour l'état du joueur (méthode pour compatibilité future).
     *
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param dx Déplacement en X (non utilisé actuellement)
     * @param dy Déplacement en Y (non utilisé actuellement)
     */
    public void update(double deltaTime, int dx, int dy) {
        if (!alive) return;
        // Plus de gestion d'invulnérabilité
    }

    /**
     * Modifie la position du joueur et met à jour les coordonnées de grille.
     *
     * @param newX Nouvelle position X en pixels
     * @param newY Nouvelle position Y en pixels
     */
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.gridX = (int) (x / CELL_SIZE);
        this.gridY = (int) (y / CELL_SIZE);
    }

    /**
     * Tente de faire placer une bombe par le joueur.
     * Vérifie si le joueur peut encore placer des bombes.
     *
     * @return true si la bombe peut être placée
     */
    public boolean placeBomb() {
        if (!alive || bombCount >= maxBombs) {
            return false;
        }
        bombCount++;
        return true;
    }

    /**
     * Notifie le joueur qu'une de ses bombes a explosé.
     * Libère un slot de bombe pour en placer une nouvelle.
     */
    public void bombExploded() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    /**
     * Inflige des dégâts au joueur (mort instantanée).
     * Dans cette version, les joueurs meurent immédiatement sans invincibilité.
     */
    public void takeDamage() {
        if (!alive) return;

        lives = 0; // Mort instantanée
        alive = false;

        System.out.println("💀 " + name + " est mort instantanément!");
    }

    /**
     * Ajoute des points au score du joueur.
     *
     * @param points Nombre de points à ajouter
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Augmente la capacité maximale de bombes du joueur.
     * Power-up permettant de placer plus de bombes simultanément.
     */
    public void increaseBombCapacity() {
        maxBombs++;
        System.out.println("💣 " + name + " peut maintenant placer " + maxBombs + " bombe(s)");
    }

    /**
     * Augmente la portée des explosions du joueur.
     * Power-up rendant les bombes plus destructrices.
     */
    public void increaseExplosionRange() {
        explosionRange++;
        System.out.println("💥 Portée d'explosion de " + name + " augmentée à " + explosionRange);
    }

    /**
     * Augmente la vitesse de déplacement du joueur.
     * Power-up pour une meilleure mobilité.
     */
    public void increaseSpeed() {
        // Implémentation future pour la vitesse
        System.out.println("⚡ " + name + " est plus rapide!");
    }

    /**
     * Fait réapparaître le joueur à sa position de départ.
     * Utilisé pour les respawns éventuels dans des modes de jeu futurs.
     *
     * @param startX Position de respawn X sur la grille
     * @param startY Position de respawn Y sur la grille
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

    /**
     * Retourne l'identifiant unique du joueur.
     *
     * @return ID du joueur
     */
    public int getId() { return id; }

    /**
     * Retourne la position X du joueur en pixels.
     *
     * @return Position X
     */
    public double getX() { return x; }

    /**
     * Retourne la position Y du joueur en pixels.
     *
     * @return Position Y
     */
    public double getY() { return y; }

    /**
     * Retourne la position X du joueur sur la grille.
     *
     * @return Coordonnée X de grille
     */
    public int getGridX() { return gridX; }

    /**
     * Retourne la position Y du joueur sur la grille.
     *
     * @return Coordonnée Y de grille
     */
    public int getGridY() { return gridY; }

    /**
     * Retourne le nombre de vies restantes.
     *
     * @return Nombre de vies
     */
    public int getLives() { return lives; }

    /**
     * Retourne le score actuel du joueur.
     *
     * @return Score du joueur
     */
    public int getScore() { return score; }

    /**
     * Retourne le nombre de bombes actuellement placées.
     *
     * @return Nombre de bombes actives
     */
    public int getBombCount() { return bombCount; }

    /**
     * Retourne le nombre maximum de bombes que le joueur peut placer.
     *
     * @return Limite de bombes
     */
    public int getMaxBombs() { return maxBombs; }

    /**
     * Retourne la portée des explosions des bombes du joueur.
     *
     * @return Portée d'explosion
     */
    public int getExplosionRange() { return explosionRange; }

    /**
     * Indique si le joueur est encore vivant.
     *
     * @return true si le joueur est vivant
     */
    public boolean isAlive() { return alive; }

    /**
     * Retourne la couleur d'affichage du joueur.
     *
     * @return Couleur du joueur
     */
    public Color getColor() { return color; }

    /**
     * Retourne le nom du joueur.
     *
     * @return Nom du joueur
     */
    public String getName() { return name; }

    /**
     * Modifie le nombre de vies du joueur.
     *
     * @param lives Nouveau nombre de vies
     */
    public void setLives(int lives) { this.lives = lives; }

    /**
     * Modifie le score du joueur.
     *
     * @param score Nouveau score
     */
    public void setScore(int score) { this.score = score; }

    /**
     * Modifie la limite de bombes du joueur.
     *
     * @param maxBombs Nouvelle limite
     */
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }

    /**
     * Modifie la portée d'explosion du joueur.
     *
     * @param explosionRange Nouvelle portée
     */
    public void setExplosionRange(int explosionRange) { this.explosionRange = explosionRange; }

    /**
     * Modifie l'état vital du joueur.
     *
     * @param alive true si le joueur est vivant
     */
    public void setAlive(boolean alive) { this.alive = alive; }

    /**
     * Représentation textuelle du joueur pour le debug.
     *
     * @return Description complète du joueur
     */
    @Override
    public String toString() {
        return String.format("%s (ID: %d) - Score: %d, Position: (%.1f, %.1f), Alive: %s",
                name, id, score, x, y, alive);
    }
}