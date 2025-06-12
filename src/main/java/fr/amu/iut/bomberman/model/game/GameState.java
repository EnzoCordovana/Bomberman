package fr.amu.iut.bomberman.model.game;

import fr.amu.iut.bomberman.model.entities.Player;

/**
 * Classe pour gérer l'état global du jeu Bomberman.
 * Contient les informations sur l'état de la partie, le gagnant, etc.
 * Thread-safe pour une utilisation dans un environnement multithreadé.
 */
public class GameState {
    /**
     * Indique si le jeu est actuellement en cours d'exécution.
     * Volatile pour assurer la visibilité entre threads sans synchronisation.
     */
    private volatile boolean running;

    /**
     * Indique si le jeu est actuellement en pause.
     * Volatile pour permettre la consultation thread-safe de l'état de pause.
     */
    private volatile boolean paused;

    /**
     * Indique si la partie est définitivement terminée.
     * Une fois à true, cette valeur ne doit plus changer pour la session de jeu courante.
     */
    private volatile boolean gameOver;

    /**
     * Référence vers le joueur gagnant de la partie.
     * null si aucun gagnant n'est encore déterminé, ou en cas de match nul.
     */
    private volatile Player winner;

    /**
     * Timestamp Unix en millisecondes du début de la partie.
     * Utilisé pour calculer le temps écoulé et le temps restant.
     */
    private volatile long startTime;

    /**
     * Durée totale autorisée pour une partie en secondes.
     * Par défaut fixée à 180 secondes soit 3 minutes.
     */
    private volatile int duration;

    /**
     * Constructeur par défaut qui initialise l'état du jeu.
     * Crée un nouvel état de jeu avec tous les paramètres remis à zéro.
     * La durée par défaut est de 3 minutes.
     */
    public GameState() {
        reset();
    }

    /**
     * Remet l'état du jeu à zéro pour une nouvelle partie.
     * Réinitialise tous les flags d'état, efface le gagnant et remet la durée par défaut.
     * Cette méthode est synchronisée pour éviter les conditions de course.
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
     * Démarre une nouvelle partie en initialisant les paramètres temporels.
     * Met le jeu en état "running", enlève la pause et marque le temps de début.
     * Le gagnant est remis à null pour cette nouvelle partie.
     */
    public synchronized void startGame() {
        running = true;
        paused = false;
        gameOver = false;
        winner = null;
        startTime = System.currentTimeMillis();
    }

    /**
     * Termine la partie en cours avec un gagnant optionnel.
     * Arrête le jeu, marque la partie comme terminée et enregistre le gagnant.
     *
     * @param winner Le joueur gagnant de la partie, peut être null pour un match nul
     */
    public synchronized void endGame(Player winner) {
        running = false;
        gameOver = true;
        this.winner = winner;
    }

    /**
     * Calcule le temps écoulé depuis le début de la partie.
     * Utilise la différence entre le timestamp actuel et le temps de début.
     *
     * @return Temps écoulé en secondes depuis le début, 0 si la partie n'a pas commencé
     */
    public synchronized long getElapsedTime() {
        if (startTime == 0) return 0;
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /**
     * Calcule le temps restant dans la partie courante.
     * Soustrait le temps écoulé de la durée totale configurée.
     *
     * @return Temps restant en secondes, 0 si la durée est dépassée
     */
    public synchronized long getRemainingTime() {
        long elapsed = getElapsedTime();
        return Math.max(0, duration - elapsed);
    }

    /**
     * Indique si le jeu est actuellement en cours d'exécution.
     * Un jeu running peut être temporairement en pause.
     *
     * @return true si le jeu est en cours, false sinon
     */
    public boolean isRunning() { return running; }

    /**
     * Modifie l'état d'exécution du jeu.
     * Méthode synchronisée pour éviter les modifications concurrentes.
     *
     * @param running true pour démarrer le jeu, false pour l'arrêter
     */
    public synchronized void setRunning(boolean running) { this.running = running; }

    /**
     * Indique si le jeu est actuellement en pause.
     * Un jeu en pause est toujours en état running mais temporairement suspendu.
     *
     * @return true si le jeu est en pause, false sinon
     */
    public boolean isPaused() { return paused; }

    /**
     * Modifie l'état de pause du jeu.
     * Méthode synchronisée pour la cohérence des états.
     *
     * @param paused true pour mettre en pause, false pour reprendre
     */
    public synchronized void setPaused(boolean paused) { this.paused = paused; }

    /**
     * Indique si la partie est définitivement terminée.
     * Une partie gameOver ne peut plus être relancée sans reset.
     *
     * @return true si la partie est terminée, false sinon
     */
    public boolean isGameOver() { return gameOver; }

    /**
     * Marque la partie comme terminée ou non.
     * Généralement utilisé en interne, préférer endGame pour terminer une partie.
     *
     * @param gameOver true pour marquer comme terminé, false sinon
     */
    public synchronized void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    /**
     * Retourne le joueur gagnant de la partie.
     * null si aucun gagnant n'est déterminé ou en cas de match nul.
     *
     * @return Le joueur gagnant ou null
     */
    public Player getWinner() { return winner; }

    /**
     * Définit le gagnant de la partie et termine automatiquement le jeu.
     * Si un gagnant est défini, la partie est automatiquement marquée comme terminée.
     *
     * @param winner Le joueur gagnant, null pour un match nul
     */
    public synchronized void setWinner(Player winner) {
        this.winner = winner;
        if (winner != null) {
            gameOver = true;
            running = false;
        }
    }

    /**
     * Retourne la durée totale configurée pour une partie.
     * Cette durée sert de référence pour calculer le temps restant.
     *
     * @return Durée de la partie en secondes
     */
    public int getDuration() { return duration; }

    /**
     * Modifie la durée totale autorisée pour une partie.
     * Affecte le calcul du temps restant pour les parties en cours.
     *
     * @param duration Nouvelle durée en secondes, doit être positive
     */
    public synchronized void setDuration(int duration) { this.duration = duration; }

    /**
     * Retourne le timestamp de début de la partie courante.
     * Utilisé principalement pour les calculs de temps internes.
     *
     * @return Timestamp Unix en millisecondes du début de partie, 0 si pas commencée
     */
    public long getStartTime() { return startTime; }
}