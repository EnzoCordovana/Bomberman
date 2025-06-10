package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.model.common.Position;
import fr.amu.iut.bomberman.view.MapView;
import fr.amu.iut.bomberman.view.ViewManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur principal pour la vue de jeu.
 * Gère l'interface utilisateur et les interactions pendant une partie.
 */
public class PlayController implements Initializable {
    // Constantes
    private static final int GAME_TIMER_INTERVAL = 1000; // 1 seconde
    private static final int DEFAULT_WIDTH = 15;
    private static final int DEFAULT_HEIGHT = 13;

    // Éléments FXML
    @FXML private AnchorPane gameArea;
    @FXML private VBox playersList;
    @FXML private Label timeLabel;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;

    // Composants du jeu
    private GameMap map;
    private MapController mapController;
    private MapView mapView;
    private Stage primaryStage;

    // État du jeu
    private int gameTime = 0;
    private int playerScore = 0;
    private int playerLives = 3;
    private AnimationTimer gameLoop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGameComponents();
        setupGameArea();
        initializePlayerUI();
        startGameLoop();
    }

    /**
     * Initialise les composants principaux du jeu
     */
    private void initializeGameComponents() {
        map = new GameMap(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        mapController = new MapController(map);
        mapView = new MapView(map);
    }

    /**
     * Configure la zone de jeu principale
     */
    private void setupGameArea() {
        gameArea.getChildren().clear();

        // Calcul pour centrer la carte
        double centerX = (gameArea.getWidth() - mapView.getViewWidth()) / 2;
        double centerY = (gameArea.getHeight() - mapView.getViewHeight()) / 2;

        mapView.setLayoutX(Math.max(0, centerX));
        mapView.setLayoutY(Math.max(0, centerY));

        gameArea.getChildren().add(mapView);
    }

    /**
     * Initialise l'interface utilisateur des joueurs
     */
    private void initializePlayerUI() {
        playersList.getChildren().clear();

        // Configuration des joueurs
        String[][] players = {
                {"Joueur 1", "#FF6B6B"},
                {"Joueur 2", "#4ECDC4"},
                {"Joueur 3", "#45B7D1"},
                {"Joueur 4", "#FFA07A"}
        };

        for (String[] player : players) {
            Label playerLabel = new Label(player[0] + ": 0 pts");
            playerLabel.setStyle("-fx-text-fill: " + player[1] + "; -fx-font-weight: bold;");
            playersList.getChildren().add(playerLabel);
        }

        updateGameInfo();
    }

    /**
     * Configure et démarre la boucle de jeu principale
     */
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000 / 60) { // 60 FPS
                    updateGameState();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Met à jour l'état du jeu à chaque frame
     */
    private void updateGameState() {
        mapController.update();
        mapView.update();

        // Mise à jour du timer toutes les secondes
        if (gameTime % GAME_TIMER_INTERVAL == 0) {
            updateGameTimer();
        }
        gameTime++;
    }

    /**
     * Met à jour le timer du jeu
     */
    private void updateGameTimer() {
        int minutes = gameTime / (60 * GAME_TIMER_INTERVAL);
        int seconds = (gameTime / GAME_TIMER_INTERVAL) % 60;
        timeLabel.setText(String.format("Temps: %d:%02d", minutes, seconds));
    }

    /**
     * Met à jour les informations du jeu affichées
     */
    private void updateGameInfo() {
        scoreLabel.setText("Score: " + playerScore);
        livesLabel.setText("Vies: " + playerLives);
    }

    /**
     * Définit la scène principale pour la navigation
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Gère l'action du bouton Pause
     */
    @FXML
    private void handlePause() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // Logique de pause supplémentaire ici
    }

    /**
     * Gère l'action du bouton Reprendre
     */
    @FXML
    private void handleResume() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    /**
     * Gère l'action du bouton Quitter
     */
    @FXML
    private void handleQuit() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Met à jour les informations du joueur
     * @param score Le nouveau score
     * @param lives Le nouveau nombre de vies
     */
    public void updatePlayerInfo(int score, int lives) {
        this.playerScore = score;
        this.playerLives = lives;
        updateGameInfo();
    }

    /**
     * Place une bombe à la position spécifiée
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return true si la bombe a été placée avec succès
     */
    public boolean placeBomb(int x, int y) {
        boolean success = mapController.placeBomb(new Position(x, y));
        if (success) {
            mapView.update();
        }
        return success;
    }

    /**
     * Nettoie les ressources lors de la fermeture du contrôleur
     */
    public void cleanup() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}