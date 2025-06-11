package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import fr.amu.iut.bomberman.model.game.GameEngine;
import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.view.MapView;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
 * PlayController avec contrôles corrigés et sans invincibilité
 */
public class PlayController implements Initializable {

    @FXML
    private AnchorPane gameArea;

    // Composants de jeu
    private GameMap gameMap;
    private GameEngine gameEngine;
    private MapView mapView;
    private Stage primaryStage;

    // Multithreading
    private ExecutorService gameThreadPool;
    private ScheduledExecutorService gameScheduler;
    private AnimationTimer renderTimer;
    private CompletableFuture<Void> gameUpdateTask;

    // Gestion thread-safe des entrées
    private final Set<KeyCode> pressedKeys = ConcurrentHashMap.newKeySet();
    private final AtomicLong lastMoveTime = new AtomicLong(0);
    private final AtomicBoolean gameRunning = new AtomicBoolean(false);

    // Configuration temporelle
    private static final long MOVE_DELAY_NS = 100_000_000L; // 100ms
    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME_NS = 1_000_000_000L / TARGET_FPS;
    private static final int GAME_UPDATE_HZ = 120;

    // NOUVEAU: Configuration des contrôles par joueur
    private static class PlayerControls {
        public final KeyCode up, down, left, right, bomb;

        public PlayerControls(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.bomb = bomb;
        }
    }

    // NOUVEAU: Contrôles distincts pour chaque joueur
    private static final PlayerControls[] PLAYER_CONTROLS = {
            new PlayerControls(KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.E),           // Joueur 1: ZQSD + E
            new PlayerControls(KeyCode.O, KeyCode.L, KeyCode.K, KeyCode.L, KeyCode.P), // Joueur 2: OKLM + P
            new PlayerControls(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER),              // Joueur 3: fleches + enter
            new PlayerControls(KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0) // Joueur 4: Pavé numérique
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeThreadPools();
        initializeGameComponents();
        setupGameArea();
        startMultithreadedGame();
        displayControlsInfo();
    }

    /**
     * Affiche les contrôles dans la console
     */
    private void displayControlsInfo() {
        System.out.println("🎮 === CONTRÔLES DU JEU ===");
        System.out.println("👤 Joueur 1 (Rouge): Z Q S D + ESPACE");
        System.out.println("👤 Joueur 2 (Bleu): FLÈCHES + ENTRÉE");
        System.out.println("👤 Joueur 3 (Vert): I J K L + U");
        System.out.println("👤 Joueur 4 (Orange): PAVÉ NUM + 0");
        System.out.println("⚠️  INVINCIBILITÉ DÉSACTIVÉE");
        System.out.println("===============================");
    }

    /**
     * Initialise les pools de threads optimisés
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

        System.out.println("Threads initialisés - Processeurs: " + Runtime.getRuntime().availableProcessors());
    }

    /**
     * Initialise les composants de jeu
     */
    private void initializeGameComponents() {
        gameMap = new GameMap(15, 13);
        gameEngine = new GameEngine(gameMap);
        gameEngine.initializeGame(2);
        mapView = new MapView(gameEngine);

        System.out.println("Composants de jeu initialisés");
    }

    /**
     * Configure la zone de jeu avec optimisations
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
     * Configure la gestion des entrées de manière thread-safe
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
     * Démarre le système de jeu multithreadé
     */
    private void startMultithreadedGame() {
        gameRunning.set(true);

        startGameLogicThread();
        startRenderThread();
        startPlayerMovementThread();

        System.out.println("Système multithreadé démarré");
    }

    /**
     * Thread dédié à la logique de jeu
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
     * Thread dédié au rendu
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
     * Thread dédié aux mouvements des joueurs
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
     * NOUVEAU: Traite les mouvements avec contrôles distincts par joueur
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
     * NOUVEAU: Traite les actions instantanées avec bombes distinctes
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
     * Gère la fin de partie de manière asynchrone
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
     * Redémarre la partie
     */
    private void restartGame() {
        CompletableFuture.runAsync(() -> {
            System.out.println("🔄 Redémarrage de la partie...");
            gameEngine.initializeGame(2);
        }, gameThreadPool);
    }

    /**
     * Gère la pause de manière thread-safe
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
     * Quitte le jeu proprement
     */
    @FXML
    private void handleQuit() {
        cleanup();
        Platform.runLater(() -> {
            ViewManager.getInstance(primaryStage).showMenuView();
        });
    }

    /**
     * Nettoyage complet et thread-safe
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
     * Ferme les pools de threads de manière sécurisée
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

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}