package fr.amu.iut.bomberman.model.entities;

/**
 * Bombe avec debug pour identifier le problème d'explosion
 */
public class Bomb {
    private int x, y; // Position sur la grille
    private int ownerId; // ID du joueur qui a posé la bombe
    private int explosionRange;
    private long timeCreated;
    private long explosionDelay; // En millisecondes
    private boolean exploded;

    public static final long DEFAULT_EXPLOSION_DELAY = 1500; // 1.5 secondes

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
     * Vérifie si la bombe doit exploser avec debug détaillé
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
     * Déclenche l'explosion de la bombe
     */
    public void explode() {
        if (!exploded) {
            this.exploded = true;
            long elapsed = System.currentTimeMillis() - timeCreated;
            System.out.println("🔥 EXPLOSION DÉCLENCHÉE! Bombe (" + x + "," + y + ") après " + elapsed + "ms");
        }
    }

    /**
     * Temps restant avant explosion
     */
    public long getTimeRemaining() {
        if (exploded) return 0;
        long remaining = explosionDelay - (System.currentTimeMillis() - timeCreated);
        return Math.max(0, remaining);
    }

    /**
     * Pourcentage de temps écoulé (pour animation)
     */
    public double getTimeProgress() {
        if (exploded) return 1.0;
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.min(1.0, (double) elapsed / explosionDelay);
    }

    /**
     * Méthode de debug pour forcer l'explosion (pour les tests)
     */
    public void forceExplode() {
        System.out.println("🚨 EXPLOSION FORCÉE! Bombe (" + x + "," + y + ")");
        this.exploded = true;
    }

    /**
     * Vérifie si la bombe est prête à exploser (sans déclencher)
     */
    public boolean isReadyToExplode() {
        return !exploded && (System.currentTimeMillis() - timeCreated >= explosionDelay);
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
    public void setExplosionDelay(long explosionDelay) {
        this.explosionDelay = explosionDelay;
        System.out.println("⚡ Délai d'explosion modifié pour bombe (" + x + "," + y + "): " + explosionDelay + "ms");
    }

    @Override
    public String toString() {
        return String.format("Bomb{pos=(%d,%d), owner=%d, exploded=%s, timeRemaining=%dms, progress=%.2f}",
                x, y, ownerId, exploded, getTimeRemaining(), getTimeProgress());
    }
}