package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import fr.amu.iut.bomberman.model.game.GameEngine;
import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.view.MapView;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contrôleur principal de la vue de jeu Bomberman.
 * Gère l'interface de jeu, les entrées utilisateur et coordonne le rendu.
 * Implémente un système multithreadé pour optimiser les performances.
 * Utilise des contrôles distincts pour chaque joueur.
 */
public class PlayController implements Initializable {

    /** Zone d'affichage du jeu */
    @FXML
    private AnchorPane gameArea;

    /** Carte de jeu */
    private GameMap gameMap;

    /** Moteur de jeu */
    private GameEngine gameEngine;

    /** Vue graphique de la carte */
    private MapView mapView;

    /** Référence à la fenêtre principale */
    private Stage primaryStage;

    /** Pool de threads pour la logique de jeu */
    private ExecutorService gameThreadPool;

    /** Planificateur pour les tâches périodiques */
    private ScheduledExecutorService gameScheduler;

    /** Timer pour le rendu d'animation */
    private AnimationTimer renderTimer;

    /** Tâche asynchrone de mise à jour du jeu */
    private CompletableFuture<Void> gameUpdateTask;

    /** Ensemble thread-safe des touches pressées */
    private final Set<KeyCode> pressedKeys = ConcurrentHashMap.newKeySet();

    /** Timestamp du dernier mouvement pour limiter la fréquence */
    private final AtomicLong lastMoveTime = new AtomicLong(0);

    /** Indicateur d'état du jeu */
    private final AtomicBoolean gameRunning = new AtomicBoolean(false);

    /** Délai minimum entre les mouvements (100ms) */
    private static final long MOVE_DELAY_NS = 100_000_000L;

    /** FPS cible pour le rendu */
    private static final int TARGET_FPS = 60;

    /** Temps par frame en nanosecondes */
    private static final long FRAME_TIME_NS = 1_000_000_000L / TARGET_FPS;

    /** Fréquence de mise à jour de la logique de jeu */
    private static final int GAME_UPDATE_HZ = 120;

    /**
     * Classe interne définissant les contrôles d'un joueur.
     */
    private static class PlayerControls {

        /** Touches de contrôle pour un joueur */
        public final KeyCode up, down, left, right, bomb;

        /**
         * Constructeur des contrôles d'un joueur.
         *
         * @param up Touche pour aller vers le haut
         * @param down Touche pour aller vers le bas
         * @param left Touche pour aller à gauche
         * @param right Touche pour aller à droite
         * @param bomb Touche pour placer une bombe
         */
        public PlayerControls(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.bomb = bomb;
        }
    }

    /** Configuration des contrôles pour chaque joueur */
    private static final PlayerControls[] PLAYER_CONTROLS = {
            new PlayerControls(KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.E),           // Joueur 1: ZQSD + E
            new PlayerControls(KeyCode.O, KeyCode.L, KeyCode.K, KeyCode.M, KeyCode.P), // Joueur 2: OKLM + P
            new PlayerControls(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER),              // Joueur 3: fleches + enter
            new PlayerControls(KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0) // Joueur 4: Pavé numérique
    };

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * Configure tous les composants nécessaires au jeu.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeThreadPools();
        initializeGameComponents();
        setupGameArea();
        startMultithreadedGame();
        displayControlsInfo();
    }

    /**
     * Affiche les informations de contrôle dans la console.
     * Aide les joueurs à connaître leurs touches de commande.
     */
    private void displayControlsInfo() {
        System.out.println("🎮 === CONTRÔLES DU JEU ===");
        System.out.println("👤 Joueur 1 (Rouge): Z Q S D + E");
        System.out.println("👤 Joueur 2 (Bleu): OKLM + P");
        System.out.println("👤 Joueur 3 (Vert): fleche + entrer");
        System.out.println("👤 Joueur 4 (Orange): PAVÉ NUM + 0");
        System.out.println("===============================");
    }

    /**
     * Initialise les pools de threads optimisés pour le jeu.
     * Configure les threads avec les bonnes priorités.
     */
    private void initializeThreadPools() {
        gameThreadPool = Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                r -> {
                    Thread t = new Thread(r, "GameThread");
                    t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    return t;
                }
        );

        gameScheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "GameScheduler");
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            return t;
        });

        // Processeurs logiques
        System.out.println("Threads initialisés - Processeurs: " + Runtime.getRuntime().availableProcessors());
    }

    /**
     * Initialise les composants de base du jeu.
     * Crée la carte, le moteur et la vue.
     */
    private void initializeGameComponents() {
        gameMap = new GameMap(15, 13);
        gameEngine = new GameEngine(gameMap);
        gameEngine.initializeGame(3);
        mapView = new MapView(gameEngine);

        System.out.println("Composants de jeu initialisés");
    }

    /**
     * Configure la zone de jeu et centre l'affichage.
     * Met en place la gestion des entrées utilisateur.
     */
    private void setupGameArea() {
        Platform.runLater(() -> {
            gameArea.getChildren().clear();

            double gameAreaWidth = 800;
            double gameAreaHeight = 600;

            double centerX = (gameAreaWidth - mapView.getViewWidth()) / 2;
            double centerY = (gameAreaHeight - mapView.getViewHeight()) / 2;

            mapView.setLayoutX(Math.max(0, centerX));
            mapView.setLayoutY(Math.max(0, centerY));

            gameArea.getChildren().add(mapView);

            setupInputHandling();

            System.out.println("Zone de jeu configurée");
        });
    }

    /**
     * Configure la gestion des entrées clavier de manière thread-safe.
     * Associe les touches aux actions de jeu.
     */
    private void setupInputHandling() {
        gameArea.setFocusTraversable(true);
        gameArea.requestFocus();

        gameArea.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();

            CompletableFuture.runAsync(() -> {
                if (pressedKeys.add(keyCode)) {
                    handleInstantAction(keyCode);
                }
            }, gameThreadPool);

            event.consume();
        });

        gameArea.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            event.consume();
        });
    }

    /**
     * Démarre le système de jeu multithreadé.
     * Lance tous les threads nécessaires au fonctionnement.
     */
    private void startMultithreadedGame() {
        gameRunning.set(true);

        startGameLogicThread();
        startRenderThread();
        startPlayerMovementThread();

        System.out.println("Système multithreadé démarré");
    }

    /**
     * Démarre le thread dédié à la logique de jeu.
     * Met à jour l'état du jeu à haute fréquence.
     */
    private void startGameLogicThread() {
        gameUpdateTask = CompletableFuture.runAsync(() -> {
            long lastUpdate = System.nanoTime();
            final long updateInterval = 1_000_000_000L / GAME_UPDATE_HZ;

            while (gameRunning.get() && !Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();

                if (now - lastUpdate >= updateInterval) {
                    double deltaTime = (now - lastUpdate) / 1_000_000_000.0;

                    try {
                        gameEngine.update(deltaTime);
                        lastUpdate = now;

                        if (gameEngine.getGameState().isGameOver()) {
                            handleGameOver();
                            break;
                        }

                    } catch (Exception e) {
                        System.err.println("Erreur dans la logique de jeu: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, gameThreadPool);
    }

    /**
     * Démarre le thread dédié au rendu graphique.
     * Maintient un FPS stable pour l'affichage.
     */
    private void startRenderThread() {
        renderTimer = new AnimationTimer() {
            private long lastRender = 0;

            @Override
            public void handle(long now) {
                if (now - lastRender >= FRAME_TIME_NS) {
                    try {
                        mapView.update();
                        lastRender = now;
                    } catch (Exception e) {
                        System.err.println("Erreur de rendu: " + e.getMessage());
                    }
                }
            }
        };

        renderTimer.start();
    }

    /**
     * Démarre le thread dédié aux mouvements des joueurs.
     * Traite les entrées clavier avec un délai approprié.
     */
    private void startPlayerMovementThread() {
        gameScheduler.scheduleAtFixedRate(() -> {
            if (!gameRunning.get()) return;

            long now = System.nanoTime();
            if (now - lastMoveTime.get() >= MOVE_DELAY_NS) {
                try {
                    processPlayerMovements();
                    lastMoveTime.set(now);
                } catch (Exception e) {
                    System.err.println("Erreur de mouvement: " + e.getMessage());
                }
            }
        }, 0, 5, TimeUnit.MILLISECONDS);
    }

    /**
     * Traite les mouvements de tous les joueurs avec leurs contrôles respectifs.
     * Vérifie les touches pressées et exécute les déplacements.
     */
    private void processPlayerMovements() {
        Set<KeyCode> currentKeys = new HashSet<>(pressedKeys);

        CompletableFuture.runAsync(() -> {
            // Traiter chaque joueur avec ses propres contrôles
            for (int playerId = 0; playerId < Math.min(PLAYER_CONTROLS.length, gameEngine.getPlayers().size()); playerId++) {
                PlayerControls controls = PLAYER_CONTROLS[playerId];

                int dx = 0, dy = 0;

                // Mouvements horizontaux
                if (currentKeys.contains(controls.left)) dx = -1;
                else if (currentKeys.contains(controls.right)) dx = 1;

                // Mouvements verticaux
                if (currentKeys.contains(controls.up)) dy = -1;
                else if (currentKeys.contains(controls.down)) dy = 1;

                // Déplacer le joueur si nécessaire
                if (dx != 0 || dy != 0) {
                    boolean moved = gameEngine.movePlayer(playerId, dx, dy);
                    if (moved) {
                        // Debug optionnel
                        // System.out.println("Joueur " + (playerId + 1) + " déplacé");
                    }
                }
            }
        }, gameThreadPool);
    }

    /**
     * Traite les actions instantanées comme le placement de bombes.
     * Associe chaque touche au bon joueur.
     *
     * @param keyCode Touche pressée à traiter
     */
    private void handleInstantAction(KeyCode keyCode) {
        // Vérifier quelle bombe correspond à quelle touche
        for (int playerId = 0; playerId < Math.min(PLAYER_CONTROLS.length, gameEngine.getPlayers().size()); playerId++) {
            if (PLAYER_CONTROLS[playerId].bomb == keyCode) {
                boolean bombPlaced = gameEngine.placeBomb(playerId);
                if (bombPlaced) {
                    System.out.println("💣 Joueur " + (playerId + 1) + " a placé une bombe!");
                }
                return; // Sortir dès qu'on trouve le bon joueur
            }
        }

        // Autres actions (pause, restart, etc.)
        switch (keyCode) {
            case ESCAPE:
                handlePause();
                break;
            case F5:
                restartGame();
                break;
        }
    }

    /**
     * Gère la fin de partie de manière asynchrone.
     * Affiche les résultats et retourne au menu après un délai.
     */
    private void handleGameOver() {
        Platform.runLater(() -> {
            System.out.println("🏁 Fin de partie détectée!");

            gameScheduler.schedule(() -> {
                Platform.runLater(this::handleQuit);
            }, 3, TimeUnit.SECONDS);
        });
    }

    /**
     * Redémarre une nouvelle partie.
     * Remet à zéro tous les éléments de jeu.
     */
    private void restartGame() {
        CompletableFuture.runAsync(() -> {
            System.out.println("🔄 Redémarrage de la partie...");
            gameEngine.initializeGame(2);
        }, gameThreadPool);
    }

    /**
     * Gestionnaire d'événement pour la pause du jeu.
     * Bascule l'état de pause de manière thread-safe.
     */
    @FXML
    private void handlePause() {
        CompletableFuture.runAsync(() -> {
            gameEngine.togglePause();
            Platform.runLater(() -> {
                System.out.println(gameEngine.getGameState().isPaused() ? "⏸ Jeu en pause" : "▶ Jeu repris");
            });
        }, gameThreadPool);
    }

    /**
     * Gestionnaire d'événement pour quitter le jeu.
     * Nettoie les ressources et retourne au menu.
     */
    @FXML
    private void handleQuit() {
        cleanup();
        Platform.runLater(() -> {
            ViewManager.getInstance(primaryStage).showMenuView();
        });
    }

    /**
     * Nettoie toutes les ressources utilisées par le contrôleur.
     * Arrête les threads et libère la mémoire.
     */
    public void cleanup() {
        System.out.println("Nettoyage des ressources...");

        gameRunning.set(false);

        if (renderTimer != null) {
            renderTimer.stop();
        }

        if (gameUpdateTask != null && !gameUpdateTask.isDone()) {
            gameUpdateTask.cancel(true);
        }

        shutdownThreadPools();

        System.out.println("Ressources nettoyées");
    }

    /**
     * Ferme les pools de threads de manière sécurisée.
     * Attend la fin des tâches en cours avant de terminer.
     */
    private void shutdownThreadPools() {
        if (gameScheduler != null && !gameScheduler.isShutdown()) {
            gameScheduler.shutdown();
            try {
                if (!gameScheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    gameScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                gameScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (gameThreadPool != null && !gameThreadPool.isShutdown()) {
            gameThreadPool.shutdown();
            try {
                if (!gameThreadPool.awaitTermination(2, TimeUnit.SECONDS)) {
                    gameThreadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                gameThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Définit la référence au stage principal.
     *
     * @param stage Le stage principal de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}