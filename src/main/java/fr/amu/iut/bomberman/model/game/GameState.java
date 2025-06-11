package fr.amu.iut.bomberman.model.game;

import fr.amu.iut.bomberman.model.entities.Player;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * État du jeu thread-safe pour le multithreading
 */
public class GameState {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicReference<Player> winner = new AtomicReference<>(null);
    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicInteger gameDuration = new AtomicInteger(300); // 5 minutes par défaut

    public GameState() {
        reset();
    }

    /**
     * Remet l'état du jeu à zéro de manière thread-safe
     */
    public void reset() {
        running.set(false);
        paused.set(false);
        winner.set(null);
        startTime.set(System.currentTimeMillis());
    }

    /**
     * Calcule le temps écoulé depuis le début de la partie
     */
    public long getElapsedTime() {
        if (!running.get()) return 0;
        return (System.currentTimeMillis() - startTime.get()) / 1000;
    }

    /**
     * Calcule le temps restant de la partie
     */
    public long getRemainingTime() {
        return Math.max(0, gameDuration.get() - getElapsedTime());
    }

    /**
     * Vérifie si le temps est écoulé
     */
    public boolean isTimeUp() {
        return getRemainingTime() <= 0;
    }

    /**
     * Vérifie si la partie est terminée
     */
    public boolean isGameOver() {
        return !running.get() || winner.get() != null || isTimeUp();
    }

    // Getters thread-safe
    public boolean isRunning() {
        return running.get();
    }

    public boolean isPaused() {
        return paused.get();
    }

    public Player getWinner() {
        return winner.get();
    }

    public long getStartTime() {
        return startTime.get();
    }

    public int getGameDuration() {
        return gameDuration.get();
    }

    // Setters thread-safe
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public void setWinner(Player winner) {
        this.winner.set(winner);
    }

    public void setGameDuration(int gameDuration) {
        this.gameDuration.set(gameDuration);
    }

    /**
     * Démarre la partie
     */
    public void start() {
        startTime.set(System.currentTimeMillis());
        running.set(true);
        paused.set(false);
    }

    /**
     * Arrête la partie
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
     * Formate le temps restant en MM:SS
     */
    public String getFormattedTimeRemaining() {
        long remaining = getRemainingTime();
        long minutes = remaining / 60;
        long seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Obtient le statut de la partie sous forme de texte
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

    @Override
    public String toString() {
        return String.format("GameState{running=%s, paused=%s, winner=%s, timeRemaining=%s}",
                running.get(), paused.get(),
                winner.get() != null ? winner.get().getName() : "none",
                getFormattedTimeRemaining());
    }
}