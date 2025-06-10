package fr.amu.iut.bomberman.model.entities;

import javafx.scene.paint.Color;

/**
 * Représente un joueur dans le jeu Bomberman.
 * Gère la position, les statistiques et les actions du joueur.
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
    private double speed;
    private boolean alive;
    private Color color;
    private String name;
    private boolean invulnerable; // Invulnérabilité temporaire après dégâts
    private long invulnerabilityEnd;

    // Constantes
    public static final double DEFAULT_SPEED = 100.0; // pixels par seconde
    public static final int CELL_SIZE = 32;
    public static final long INVULNERABILITY_DURATION = 2000; // 2 secondes

    public Player(int id, int startX, int startY, Color color, String name) {
        this.id = id;
        this.gridX = startX;
        this.gridY = startY;
        this.x = startX * CELL_SIZE + CELL_SIZE / 2.0; // Centre de la cellule
        this.y = startY * CELL_SIZE + CELL_SIZE / 2.0;
        this.color = color;
        this.name = name;

        // Valeurs par défaut
        this.lives = 3;
        this.score = 0;
        this.bombCount = 0;
        this.maxBombs = 1;
        this.explosionRange = 1;
        this.speed = DEFAULT_SPEED;
        this.alive = true;
        this.invulnerable = false;
        this.invulnerabilityEnd = 0;
    }

    /**
     * Met à jour la position du joueur.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour (en secondes)
     * @param dx Direction horizontale (-1, 0, 1)
     * @param dy Direction verticale (-1, 0, 1)
     */
    public void update(double deltaTime, int dx, int dy) {
        if (!alive) return;

        // Mettre à jour l'invulnérabilité
        if (invulnerable && System.currentTimeMillis() > invulnerabilityEnd) {
            invulnerable = false;
        }

        // Déplacement
        if (dx != 0 || dy != 0) {
            move(deltaTime, dx, dy);
        }
    }

    /**
     * Déplace le joueur vers la direction spécifiée.
     * @param deltaTime Temps écoulé depuis la dernière mise à jour
     * @param dx Déplacement horizontal (-1, 0, 1)
     * @param dy Déplacement vertical (-1, 0, 1)
     */
    private void move(double deltaTime, int dx, int dy) {
        double distance = speed * deltaTime;

        double newX = x + dx * distance;
        double newY = y + dy * distance;

        // Mise à jour de la position
        this.x = newX;
        this.y = newY;

        // Mise à jour de la position sur la grille (basée sur le centre du joueur)
        this.gridX = (int) (x / CELL_SIZE);
        this.gridY = (int) (y / CELL_SIZE);
    }

    /**
     * Définit la position exacte du joueur.
     * @param newX Nouvelle position X
     * @param newY Nouvelle position Y
     */
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.gridX = (int) (x / CELL_SIZE);
        this.gridY = (int) (y / CELL_SIZE);
    }

    /**
     * Place une bombe à la position actuelle du joueur.
     * @return true si la bombe a été placée, false sinon
     */
    public boolean placeBomb() {
        if (!alive || bombCount >= maxBombs) {
            return false;
        }

        bombCount++;
        return true;
    }

    /**
     * Appelé quand une bombe du joueur explose.
     */
    public void bombExploded() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    /**
     * Le joueur prend des dégâts.
     */
    public void takeDamage() {
        if (!alive || invulnerable) return;

        lives--;
        if (lives <= 0) {
            alive = false;
        } else {
            // Activer l'invulnérabilité temporaire
            invulnerable = true;
            invulnerabilityEnd = System.currentTimeMillis() + INVULNERABILITY_DURATION;
        }
    }

    /**
     * Ajoute des points au score du joueur.
     * @param points Points à ajouter
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Améliore les capacités du joueur (power-ups).
     */
    public void increaseBombCapacity() {
        maxBombs++;
    }

    public void increaseExplosionRange() {
        explosionRange++;
    }

    public void increaseSpeed() {
        speed = Math.min(speed + 20, DEFAULT_SPEED * 2); // Limite à 2x la vitesse
    }

    public void addLife() {
        lives++;
    }

    /**
     * Vérifie si le joueur peut se déplacer vers une position.
     * @param newX Nouvelle position X
     * @param newY Nouvelle position Y
     * @return true si le mouvement est possible
     */
    public boolean canMoveTo(double newX, double newY) {
        // Calculer les limites du joueur (hitbox)
        double halfSize = CELL_SIZE * 0.3; // Le joueur occupe 60% d'une cellule

        double left = newX - halfSize;
        double right = newX + halfSize;
        double top = newY - halfSize;
        double bottom = newY + halfSize;

        // Vérifier les cellules occupées par la hitbox
        int leftGrid = (int) (left / CELL_SIZE);
        int rightGrid = (int) (right / CELL_SIZE);
        int topGrid = (int) (top / CELL_SIZE);
        int bottomGrid = (int) (bottom / CELL_SIZE);

        // Pour l'instant, on retourne true - la vérification sera faite dans GameState
        return true;
    }

    /**
     * Remet le joueur à sa position de départ.
     */
    public void respawn(int startX, int startY) {
        this.gridX = startX;
        this.gridY = startY;
        this.x = startX * CELL_SIZE + CELL_SIZE / 2.0;
        this.y = startY * CELL_SIZE + CELL_SIZE / 2.0;
        this.alive = true;
        this.invulnerable = true;
        this.invulnerabilityEnd = System.currentTimeMillis() + INVULNERABILITY_DURATION;
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
    public double getSpeed() { return speed; }
    public boolean isAlive() { return alive; }
    public boolean isInvulnerable() { return invulnerable; }
    public Color getColor() { return color; }
    public String getName() { return name; }

    // Setters
    public void setLives(int lives) { this.lives = lives; }
    public void setScore(int score) { this.score = score; }
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public void setExplosionRange(int explosionRange) { this.explosionRange = explosionRange; }
    public void setSpeed(double speed) { this.speed = speed; }
    public void setAlive(boolean alive) { this.alive = alive; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) - Lives: %d, Score: %d, Position: (%.1f, %.1f)",
                name, id, lives, score, x, y);
    }
}