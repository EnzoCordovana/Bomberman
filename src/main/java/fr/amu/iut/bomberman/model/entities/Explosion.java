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

    /** Indique si l'explosion peut infliger des dégâts */
    private boolean damaging;

    /** Durée par défaut d'une explosion (1 seconde) */
    public static final long DEFAULT_DURATION = 1000;

    /** Durée pendant laquelle l'explosion inflige des dégâts (0.5 seconde) */
    public static final long DAMAGE_DURATION = 500;

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
        this.damaging = true;

        // DEBUG: Afficher la création de l'explosion
        System.out.println("💥 Explosion créée en (" + x + "," + y + ") à " + timeCreated);
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
        this.damaging = true;

        // DEBUG: Afficher la création de l'explosion
        System.out.println("💥 Explosion créée en (" + x + "," + y + ") à " + timeCreated + " (durée: " + duration + "ms)");
    }

    /**
     * Met à jour l'état de l'explosion.
     * Vérifie si elle doit être désactivée selon sa durée de vie.
     *
     * @return true si l'explosion est encore active
     */
    public boolean update() {
        if (!active) return false;

        long elapsed = System.currentTimeMillis() - timeCreated;

        // Arrêter les dégâts après DAMAGE_DURATION
        if (damaging && elapsed >= DAMAGE_DURATION) {
            damaging = false;
            System.out.println("🔥 Explosion (" + x + "," + y + ") n'inflige plus de dégâts après " + elapsed + "ms");
        }

        // Désactiver complètement après la durée totale
        if (elapsed >= duration) {
            active = false;
            damaging = false;
            System.out.println("💨 Explosion (" + x + "," + y + ") désactivée après " + elapsed + "ms");
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
     * Vérifie si l'explosion peut encore infliger des dégâts.
     * Les explosions n'infligent des dégâts que pendant une courte période.
     *
     * @return true si l'explosion peut infliger des dégâts
     */
    public boolean canDamage() {
        return active && damaging;
    }

    /**
     * Vérifie si l'explosion est dans sa phase de disparition.
     * Utilisé pour les effets visuels de fade-out.
     *
     * @return true si l'explosion est en train de disparaître
     */
    public boolean isFading() {
        if (!active) return false;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return elapsed > DAMAGE_DURATION;
    }

    /**
     * Désactive manuellement l'explosion avant la fin de sa durée.
     */
    public void deactivate() {
        if (active) {
            System.out.println("🚫 Explosion (" + x + "," + y + ") désactivée manuellement");
        }
        this.active = false;
        this.damaging = false;
    }

    /**
     * Force l'arrêt des dégâts sans désactiver l'explosion.
     * Utilisé quand l'explosion doit rester visible mais ne plus être dangereuse.
     */
    public void stopDamage() {
        if (damaging) {
            System.out.println("⚡ Explosion (" + x + "," + y + ") arrête d'infliger des dégâts");
        }
        this.damaging = false;
    }

    /**
     * Calcule le temps restant avant désactivation complète.
     *
     * @return Temps restant en millisecondes
     */
    public long getTimeRemaining() {
        if (!active) return 0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.max(0, duration - elapsed);
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
     * Indique si l'explosion peut infliger des dégâts.
     *
     * @return true si l'explosion est dangereuse
     */
    public boolean isDamaging() { return damaging; }

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
        return String.format("Explosion{pos=(%d,%d), active=%s, damaging=%s, progress=%.2f, timeRemaining=%dms}",
                x, y, active, damaging, getTimeProgress(), getTimeRemaining());
    }
}