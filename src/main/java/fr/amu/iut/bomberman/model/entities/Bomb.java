package fr.amu.iut.bomberman.model.entities;

/**
 * Représente une bombe dans le jeu Bomberman.
 * Gère le décompte avant explosion et les propriétés de l'explosion.
 */
public class Bomb {
    private int x, y; // Position sur la grille
    private int ownerId; // ID du joueur qui a posé la bombe
    private int explosionRange;
    private long timeCreated;
    private long explosionDelay; // En millisecondes
    private boolean exploded;

    public static final long DEFAULT_EXPLOSION_DELAY = 3000; // 3 secondes

    public Bomb(int x, int y, int ownerId, int explosionRange) {
        this.x = x;
        this.y = y;
        this.ownerId = ownerId;
        this.explosionRange = explosionRange;
        this.timeCreated = System.currentTimeMillis();
        this.explosionDelay = DEFAULT_EXPLOSION_DELAY;
        this.exploded = false;
    }

    /**
     * Vérifie si la bombe doit exploser.
     * @return true si la bombe doit exploser
     */
    public boolean shouldExplode() {
        return !exploded && (System.currentTimeMillis() - timeCreated >= explosionDelay);
    }

    /**
     * Déclenche l'explosion de la bombe.
     */
    public void explode() {
        this.exploded = true;
    }

    /**
     * Calcule le temps restant avant l'explosion.
     * @return Temps restant en millisecondes
     */
    public long getTimeRemaining() {
        if (exploded) return 0;
        long remaining = explosionDelay - (System.currentTimeMillis() - timeCreated);
        return Math.max(0, remaining);
    }

    /**
     * Calcule le pourcentage de temps écoulé (pour l'animation).
     * @return Pourcentage entre 0.0 et 1.0
     */
    public double getTimeProgress() {
        if (exploded) return 1.0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.min(1.0, (double) elapsed / explosionDelay);
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getOwnerId() { return ownerId; }
    public int getExplosionRange() { return explosionRange; }
    public boolean isExploded() { return exploded; }
    public long getTimeCreated() { return timeCreated; }
    public long getExplosionDelay() { return explosionDelay; }

    // Setters
    public void setExplosionDelay(long explosionDelay) { this.explosionDelay = explosionDelay; }
}