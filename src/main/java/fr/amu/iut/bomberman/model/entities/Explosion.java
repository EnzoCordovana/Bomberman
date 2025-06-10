package fr.amu.iut.bomberman.model.entities;

/**
 * Représente une explosion dans le jeu Bomberman.
 * Gère la durée de vie et la zone d'effet de l'explosion.
 */
public class Explosion {
    private int x, y; // Position sur la grille
    private long timeCreated;
    private long duration; // Durée de vie de l'explosion
    private boolean active;

    public static final long DEFAULT_DURATION = 1000; // 1 seconde

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.timeCreated = System.currentTimeMillis();
        this.duration = DEFAULT_DURATION;
        this.active = true;
    }

    public Explosion(int x, int y, long duration) {
        this.x = x;
        this.y = y;
        this.timeCreated = System.currentTimeMillis();
        this.duration = duration;
        this.active = true;
    }

    /**
     * Met à jour l'état de l'explosion.
     * @return true si l'explosion est encore active
     */
    public boolean update() {
        if (!active) return false;

        if (System.currentTimeMillis() - timeCreated >= duration) {
            active = false;
        }

        return active;
    }

    /**
     * Calcule le pourcentage de temps écoulé (pour l'animation).
     * @return Pourcentage entre 0.0 et 1.0
     */
    public double getTimeProgress() {
        if (!active) return 1.0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.min(1.0, (double) elapsed / duration);
    }

    /**
     * Désactive l'explosion.
     */
    public void deactivate() {
        this.active = false;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return active; }
    public long getTimeCreated() { return timeCreated; }
    public long getDuration() { return duration; }
}