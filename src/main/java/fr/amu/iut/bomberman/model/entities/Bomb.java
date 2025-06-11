package fr.amu.iut.bomberman.model.entities;

/**
 * Représente une bombe dans le jeu Bomberman.
 * Gère le timer d'explosion, les animations et les propriétés de la bombe.
 * Inclut des fonctionnalités de debug pour identifier les problèmes d'explosion.
 */
public class Bomb {
    /** Position X de la bombe sur la grille */
    private int x, y;

    /** Identifiant du joueur propriétaire de la bombe */
    private int ownerId;

    /** Portée de l'explosion de cette bombe */
    private int explosionRange;

    /** Timestamp de création de la bombe */
    private long timeCreated;

    /** Délai avant explosion en millisecondes */
    private long explosionDelay;

    /** Indique si la bombe a déjà explosé */
    private boolean exploded;

    /** Délai par défaut avant explosion (1.5 secondes) */
    public static final long DEFAULT_EXPLOSION_DELAY = 1500;

    /**
     * Constructeur d'une bombe.
     *
     * @param x Position X sur la grille
     * @param y Position Y sur la grille
     * @param ownerId Identifiant du joueur propriétaire
     * @param explosionRange Portée de l'explosion
     */
    public Bomb(int x, int y, int ownerId, int explosionRange) {
        this.x = x;
        this.y = y;
        this.ownerId = ownerId;
        this.explosionRange = explosionRange;
        this.timeCreated = System.currentTimeMillis();
        this.explosionDelay = DEFAULT_EXPLOSION_DELAY;
        this.exploded = false;

        // DEBUG: Afficher la création de la bombe
        System.out.println("💣 Bombe créée en (" + x + "," + y + ") par joueur " + (ownerId + 1) +
                " à " + timeCreated + " (explosion dans " + explosionDelay + "ms)");
    }

    /**
     * Vérifie si la bombe doit exploser maintenant.
     * Inclut des messages de debug détaillés pour le diagnostic.
     *
     * @return true si la bombe est prête à exploser
     */
    public boolean shouldExplode() {
        if (exploded) {
            return false; // Déjà explosée
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - timeCreated;
        boolean shouldExplode = elapsed >= explosionDelay;

        // DEBUG: Afficher l'état toutes les 1s
        if (elapsed % 1000 < 50) { // Affiche environ toutes les 1s
            System.out.println("⏰ Bombe (" + x + "," + y + ") - Elapsed: " + elapsed + "ms / " + explosionDelay + "ms" +
                    " - ShouldExplode: " + shouldExplode + " - TimeRemaining: " + getTimeRemaining() + "ms");
        }

        if (shouldExplode && !exploded) {
            System.out.println("💥 BOMBE PRÊTE À EXPLOSER! (" + x + "," + y + ") après " + elapsed + "ms");
        }

        return shouldExplode;
    }

    /**
     * Déclenche manuellement l'explosion de la bombe.
     * Marque la bombe comme explosée pour éviter les doublons.
     */
    public void explode() {
        if (!exploded) {
            this.exploded = true;
            long elapsed = System.currentTimeMillis() - timeCreated;
            System.out.println("🔥 EXPLOSION DÉCLENCHÉE! Bombe (" + x + "," + y + ") après " + elapsed + "ms");
        }
    }

    /**
     * Calcule le temps restant avant explosion.
     *
     * @return Temps restant en millisecondes (0 si explosée)
     */
    public long getTimeRemaining() {
        if (exploded) return 0;
        long remaining = explosionDelay - (System.currentTimeMillis() - timeCreated);
        return Math.max(0, remaining);
    }

    /**
     * Calcule le pourcentage de temps écoulé depuis la création.
     * Utilisé pour les animations de clignotement.
     *
     * @return Pourcentage entre 0.0 et 1.0
     */
    public double getTimeProgress() {
        if (exploded) return 1.0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.min(1.0, (double) elapsed / explosionDelay);
    }

    /**
     * Force l'explosion immédiate de la bombe (pour les tests).
     */
    public void forceExplode() {
        System.out.println("🚨 EXPLOSION FORCÉE! Bombe (" + x + "," + y + ")");
        this.exploded = true;
    }

    /**
     * Vérifie si la bombe est prête à exploser sans déclencher l'explosion.
     *
     * @return true si le délai est écoulé
     */
    public boolean isReadyToExplode() {
        return !exploded && (System.currentTimeMillis() - timeCreated >= explosionDelay);
    }

    /**
     * Retourne la position X de la bombe.
     *
     * @return Coordonnée X sur la grille
     */
    public int getX() { return x; }

    /**
     * Retourne la position Y de la bombe.
     *
     * @return Coordonnée Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Retourne l'identifiant du joueur propriétaire.
     *
     * @return ID du joueur propriétaire
     */
    public int getOwnerId() { return ownerId; }

    /**
     * Retourne la portée d'explosion de la bombe.
     *
     * @return Portée en nombre de cases
     */
    public int getExplosionRange() { return explosionRange; }

    /**
     * Indique si la bombe a déjà explosé.
     *
     * @return true si explosée
     */
    public boolean isExploded() { return exploded; }

    /**
     * Retourne le timestamp de création de la bombe.
     *
     * @return Timestamp en millisecondes
     */
    public long getTimeCreated() { return timeCreated; }

    /**
     * Retourne le délai configuré avant explosion.
     *
     * @return Délai en millisecondes
     */
    public long getExplosionDelay() { return explosionDelay; }

    /**
     * Modifie le délai avant explosion de la bombe.
     *
     * @param explosionDelay Nouveau délai en millisecondes
     */
    public void setExplosionDelay(long explosionDelay) {
        this.explosionDelay = explosionDelay;
        System.out.println("⚡ Délai d'explosion modifié pour bombe (" + x + "," + y + "): " + explosionDelay + "ms");
    }

    /**
     * Représentation textuelle de la bombe pour le debug.
     *
     * @return Description complète de la bombe
     */
    @Override
    public String toString() {
        return String.format("Bomb{pos=(%d,%d), owner=%d, exploded=%s, timeRemaining=%dms, progress=%.2f}",
                x, y, ownerId, exploded, getTimeRemaining(), getTimeProgress());
    }
}