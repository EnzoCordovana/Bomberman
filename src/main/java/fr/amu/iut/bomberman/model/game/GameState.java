package fr.amu.iut.bomberman.model.game;

import fr.amu.iut.bomberman.model.entities.Player;

/**
 * Classe pour gérer l'état global du jeu Bomberman.
 * Contient les informations sur l'état de la partie, le gagnant, etc.
 * Thread-safe pour une utilisation dans un environnement multithreadé.
 */
public class GameState {
    /** Indique si le jeu est en cours */
    private volatile boolean running;

    /** Indique si le jeu est en pause */
    private volatile boolean paused;

    /** Indique si la partie est terminée */
    private volatile boolean gameOver;

    /** Le joueur gagnant (null si pas de gagnant ou match nul) */
    private volatile Player winner;

    /** Temps de début de la partie */
    private volatile long startTime;

    /** Durée de la partie en secondes */
    private volatile int duration;

    /**
     * Constructeur par défaut.
     */
    public GameState() {
        reset();
    }

    /**
     * Remet l'état du jeu à zéro.
     */
    public synchronized void reset() {
        running = false;
        paused = false;
        gameOver = false;
        winner = null;
        startTime = 0;
        duration = 180; // 3 minutes par défaut
    }

    /**
     * Démarre une nouvelle partie.
     */
    public synchronized void startGame() {
        running = true;
        paused = false;
        gameOver = false;
        winner = null;
        startTime = System.currentTimeMillis();
    }

    /**
     * Termine la partie.
     * @param winner Le joueur gagnant (peut être null)
     */
    public synchronized void endGame(Player winner) {
        running = false;
        gameOver = true;
        this.winner = winner;
    }

    /**
     * Calcule le temps écoulé depuis le début de la partie.
     * @return Temps écoulé en secondes
     */
    public synchronized long getElapsedTime() {
        if (startTime == 0) return 0;
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /**
     * Calcule le temps restant dans la partie.
     * @return Temps restant en secondes
     */
    public synchronized long getRemainingTime() {
        long elapsed = getElapsedTime();
        return Math.max(0, duration - elapsed);
    }

    // Getters et setters thread-safe
    public boolean isRunning() { return running; }
    public synchronized void setRunning(boolean running) { this.running = running; }

    public boolean isPaused() { return paused; }
    public synchronized void setPaused(boolean paused) { this.paused = paused; }

    public boolean isGameOver() { return gameOver; }
    public synchronized void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public Player getWinner() { return winner; }
    public synchronized void setWinner(Player winner) {
        this.winner = winner;
        if (winner != null) {
            gameOver = true;
            running = false;
        }
    }

    public int getDuration() { return duration; }
    public synchronized void setDuration(int duration) { this.duration = duration; }

    public long getStartTime() { return startTime; }
}