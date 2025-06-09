package fr.amu.iut.bomberman.controller;

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
 */
public class PlayController implements Initializable {

    @FXML private AnchorPane gameArea;
    @FXML private VBox playersList;
    @FXML private Label timeLabel;
    @FXML private Label scoreLabel;
    @FXML private Label livesLabel;

    private Stage primaryStage;
    //private GameModel gameModel;

    /**
     * Initialise le contrôleur et configure les éléments de l'interface.
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des composants
        setupGameArea();
        updatePlayerList();
        startGameTimer();
    }

    /**
     * Définit la scène principale pour la navigation.
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Configure la zone de jeu principale.
     */
    private void setupGameArea() {
        // Configuration initiale de la zone de jeu
        gameArea.setPrefSize(800, 600);
        // Ici vous ajouteriez les éléments du jeu (carte, joueurs, etc.)
    }

    /**
     * Met à jour la liste des joueurs affichée.
     */
    private void updatePlayerList() {
        // Nettoyer la liste actuelle
        playersList.getChildren().clear();

        // Ajouter les informations des joueurs (exemple)
        for (int i = 0; i < 4; i++) {
            Label playerLabel = new Label("Joueur " + (i+1) + ": 0 pts");
            playerLabel.setStyle("-fx-text-fill: white;");
            playersList.getChildren().add(playerLabel);
        }
    }

    /**
     * Démarre le chronomètre du jeu.
     */
    private void startGameTimer() {
        // Implémentation du chronomètre
        // Exemple simple avec un Timeline
        // Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ...));
    }

    /**
     * Gère l'action du bouton Pause.
     */
    @FXML
    private void handlePause() {
        // Logique pour mettre le jeu en pause
        System.out.println("Jeu en pause");
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
        scoreLabel.setText("Score: " + score);
        timeLabel.setText("Temps: " + time);
        livesLabel.setText("Vies: " + lives);
    }
}