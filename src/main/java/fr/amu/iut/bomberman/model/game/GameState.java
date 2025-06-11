package fr.amu.iut.bomberman.model.game;

import fr.amu.iut.bomberman.model.entities.Player;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Représente l'état global du jeu de manière thread-safe.
 * Utilise des classes atomiques pour gérer le multithreading.
 * Gère les états de pause, victoire, temps de jeu et conditions de fin.
 */
public class GameState {

    /** Indique si le jeu est en cours d'exécution */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /** Indique si le jeu est actuellement en pause */
    private final AtomicBoolean paused = new AtomicBoolean(false);

    /** Référence vers le joueur gagnant (null si pas de gagnant) */
    private final AtomicReference<Player> winner = new AtomicReference<>(null);

    /** Timestamp de début de partie */
    private final AtomicLong startTime = new AtomicLong(0);

    /** Durée maximale d'une partie en secondes */
    private final AtomicInteger gameDuration = new AtomicInteger(300); // 5 minutes par défaut

    /**
     * Constructeur de l'état de jeu.
     * Initialise l'état à ses valeurs par défaut.
     */
    public GameState() {
        reset();
    }

    /**
     * Remet l'état du jeu à zéro de manière thread-safe.
     * Utilisé pour commencer une nouvelle partie.
     */
    public void reset() {
        running.set(false);
        paused.set(false);
        winner.set(null);
        startTime.set(System.currentTimeMillis());
    }

    /**
     * Calcule le temps écoulé depuis le début de la partie.
     *
     * @return Temps écoulé en secondes
     */
    public long getElapsedTime() {
        if (!running.get()) return 0;
        return (System.currentTimeMillis() - startTime.get()) / 1000;
    }

    /**
     * Calcule le temps restant avant la fin de la partie.
     *
     * @return Temps restant en secondes
     */
    public long getRemainingTime() {
        return Math.max(0, gameDuration.get() - getElapsedTime());
    }

    /**
     * Vérifie si le temps de jeu est écoulé.
     *
     * @return true si le temps est écoulé
     */
    public boolean isTimeUp() {
        return getRemainingTime() <= 0;
    }

    /**
     * Vérifie si la partie est terminée (arrêtée, gagnant, ou temps écoulé).
     *
     * @return true si la partie est finie
     */
    public boolean isGameOver() {
        return !running.get() || winner.get() != null || isTimeUp();
    }

    /**
     * Indique si le jeu est en cours d'exécution.
     *
     * @return true si le jeu fonctionne
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Indique si le jeu est en pause.
     *
     * @return true si le jeu est en pause
     */
    public boolean isPaused() {
        return paused.get();
    }

    /**
     * Retourne le joueur gagnant de la partie.
     *
     * @return Le joueur gagnant ou null si pas de gagnant
     */
    public Player getWinner() {
        return winner.get();
    }

    /**
     * Retourne le timestamp de début de partie.
     *
     * @return Timestamp de début en millisecondes
     */
    public long getStartTime() {
        return startTime.get();
    }

    /**
     * Retourne la durée configurée pour une partie.
     *
     * @return Durée en secondes
     */
    public int getGameDuration() {
        return gameDuration.get();
    }

    /**
     * Définit si le jeu est en cours d'exécution.
     *
     * @param running true pour démarrer, false pour arrêter
     */
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    /**
     * Définit l'état de pause du jeu.
     *
     * @param paused true pour mettre en pause
     */
    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    /**
     * Définit le joueur gagnant de la partie.
     *
     * @param winner Le joueur gagnant
     */
    public void setWinner(Player winner) {
        this.winner.set(winner);
    }

    /**
     * Définit la durée maximale d'une partie.
     *
     * @param gameDuration Durée en secondes
     */
    public void setGameDuration(int gameDuration) {
        this.gameDuration.set(gameDuration);
    }

    /**
     * Démarre la partie en initialisant le timestamp.
     */
    public void start() {
        startTime.set(System.currentTimeMillis());
        running.set(true);
        paused.set(false);
    }

    /**
     * Arrête la partie.
     */
    public void stop() {
        running.set(false);
    }

    /**
     * Met en pause / reprend la partie
     */
    public void togglePause() {
        paused.set(!paused.get());
    }

    /**
     * Formate le temps restant au format MM:SS.
     *
     * @return Chaîne formatée du temps restant
     */
    public String getFormattedTimeRemaining() {
        long remaining = getRemainingTime();
        long minutes = remaining / 60;
        long seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Obtient le statut de la partie sous forme de texte lisible.
     *
     * @return Description textuelle de l'état
     */
    public String getStatusText() {
        if (!running.get()) {
            return "Arrêtée";
        } else if (paused.get()) {
            return "En pause";
        } else if (isTimeUp()) {
            return "Temps écoulé";
        } else if (winner.get() != null) {
            return "Terminée - " + winner.get().getName() + " gagne!";
        } else {
            return "En cours";
        }
    }

    /**
     * Représentation textuelle de l'état pour le debug.
     *
     * @return Description complète de l'état
     */
    @Override
    public String toString() {
        return String.format("GameState{running=%s, paused=%s, winner=%s, timeRemaining=%s}",
                running.get(), paused.get(),
                winner.get() != null ? winner.get().getName() : "none",
                getFormattedTimeRemaining());
    }
}