package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.model.map.Map;
import fr.amu.iut.bomberman.view.MapView;
import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue principale du jeu (PlayView).
 * Gère l'interface utilisateur pendant une partie de Bomberman.
 * Respecte le pattern MVC en séparant la logique de contrôle de la vue.
 */
public class PlayController implements Initializable {

    @FXML private AnchorPane gameArea;
    @FXML private VBox playersList;
    @FXML private Label timeLabel;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;

    private Map map;
    private MapView mapView;
    private Stage primaryStage;

    /**
     * Initialise le contrôleur et configure les éléments de l'interface.
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMap();
        setupGameArea();
        updatePlayerList();
        startGameTimer();
    }

    /**
     * Initialise la carte de jeu avec des dimensions standard de Bomberman.
     */
    private void initializeMap() {
        map = new Map();
        // Dimensions classiques d'une carte Bomberman (impaires pour le gameplay)
        map.initialize(15, 15);
        mapView = new MapView(map);
    }

    /**
     * Définit la scène principale pour la navigation.
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Configure la zone de jeu principale en y ajoutant la carte.
     */
    private void setupGameArea() {
        // Nettoyage de la zone de jeu
        gameArea.getChildren().clear();

        // Ajout de la vue de la carte
        if (mapView != null) {
            // Centrer la carte dans la zone de jeu
            double centerX = (gameArea.getPrefWidth() - (map.getWidth() * 32)) / 2;
            double centerY = (gameArea.getPrefHeight() - (map.getHeight() * 32)) / 2;

            mapView.setLayoutX(Math.max(0, centerX));
            mapView.setLayoutY(Math.max(0, centerY));

            gameArea.getChildren().add(mapView);
        }
    }

    /**
     * Met à jour la liste des joueurs affichée.
     */
    private void updatePlayerList() {
        playersList.getChildren().clear();

        // Configuration des couleurs pour chaque joueur comme dans Super Bomberman
        String[] playerColors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A"};
        String[] playerNames = {"Joueur 1", "Joueur 2", "Joueur 3", "Joueur 4"};

        for (int i = 0; i < 4; i++) {
            Label playerLabel = new Label(playerNames[i] + ": 0 pts");
            playerLabel.setStyle("-fx-text-fill: " + playerColors[i] + "; -fx-font-weight: bold;");
            playersList.getChildren().add(playerLabel);
        }
    }

    /**
     * Démarre le chronomètre du jeu.
     */
    private void startGameTimer() {
        // Pour le moment, affichage statique
        // Dans une version complète, vous utiliseriez Timeline
        timeLabel.setText("Temps: 0:00");
        scoreLabel.setText("Score: 0");
        livesLabel.setText("Vies: 3");
    }

    /**
     * Gère l'action du bouton Pause.
     */
    @FXML
    private void handlePause() {
        System.out.println("Jeu en pause");
        // Ici vous pauseriez les animations et les timers
    }

    /**
     * Gère l'action du bouton Quitter.
     * Retourne au menu principal.
     */
    @FXML
    private void handleQuit() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Met à jour les informations du jeu (score, temps, vies).
     * @param score Le score actuel
     * @param time Le temps écoulé
     * @param lives Le nombre de vies restantes
     */
    public void updateGameInfo(int score, String time, int lives) {
        if (scoreLabel != null) scoreLabel.setText("Score: " + score);
        if (timeLabel != null) timeLabel.setText("Temps: " + time);
        if (livesLabel != null) livesLabel.setText("Vies: " + lives);
    }

    /**
     * Rafraîchit l'affichage de la carte.
     */
    public void refreshMap() {
        if (mapView != null) {
            mapView.update();
        }
    }
}