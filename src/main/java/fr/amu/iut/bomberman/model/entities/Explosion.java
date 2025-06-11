package fr.amu.iut.bomberman.model.entities;

/**
 * Représente une entité d'explosion temporaire dans le jeu.
 * Gère la durée de vie et les animations des explosions.
 * Utilisée pour l'affichage visuel et la détection de collision avec les joueurs.
 */
public class Explosion {

    /** Position X de l'explosion sur la grille */
    private int x, y;

    /** Timestamp de création de l'explosion */
    private long timeCreated;

    /** Durée de vie de l'explosion en millisecondes */
    private long duration;

    /** Indique si l'explosion est encore active */
    private boolean active;

    /** Durée par défaut d'une explosion (1.5 seconde) */
    public static final long DEFAULT_DURATION = 1500;

    /**
     * Constructeur d'explosion avec durée par défaut.
     *
     * @param x Position X sur la grille
     * @param y Position Y sur la grille
     */
    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.timeCreated = System.currentTimeMillis();
        this.duration = DEFAULT_DURATION;
        this.active = true;
    }

    /**
     * Constructeur d'explosion avec durée personnalisée.
     *
     * @param x Position X sur la grille
     * @param y Position Y sur la grille
     * @param duration Durée de vie en millisecondes
     */
    public Explosion(int x, int y, long duration) {
        this.x = x;
        this.y = y;
        this.timeCreated = System.currentTimeMillis();
        this.duration = duration;
        this.active = true;
    }

    /**
     * Met à jour l'état de l'explosion.
     * Vérifie si elle doit être désactivée selon sa durée de vie.
     *
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
     * Calcule le pourcentage de temps écoulé depuis la création.
     * Utilisé pour les effets d'animation et de fade-out.
     *
     * @return Pourcentage entre 0.0 et 1.0
     */
    public double getTimeProgress() {
        if (!active) return 1.0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.min(1.0, (double) elapsed / duration);
    }

    /**
     * Désactive manuellement l'explosion avant la fin de sa durée.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Retourne la position X de l'explosion.
     *
     * @return Coordonnée X sur la grille
     */
    public int getX() { return x; }

    /**
     * Retourne la position Y de l'explosion.
     *
     * @return Coordonnée Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Indique si l'explosion est encore active.
     *
     * @return true si l'explosion est active
     */
    public boolean isActive() { return active; }

    /**
     * Retourne le timestamp de création.
     *
     * @return Timestamp en millisecondes
     */
    public long getTimeCreated() { return timeCreated; }

    /**
     * Retourne la durée de vie configurée.
     *
     * @return Durée en millisecondes
     */
    public long getDuration() { return duration; }

    /**
     * Représentation textuelle de l'explosion pour le debug.
     *
     * @return Description de l'explosion
     */
    @Override
    public String toString() {
        return String.format("Explosion{pos=(%d,%d), active=%s, progress=%.2f}",
                x, y, active, getTimeProgress());
    }
}