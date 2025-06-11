package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue du menu principal - MVC Controller
 */
public class MenuController implements Initializable {

    @FXML
    public BorderPane container;

    /** Référence à la fenêtre principale de l'application */
    private Stage primaryStage;

    @FXML
    private Button quitButton;

    /**
     * Définit la référence à la fenêtre principale.
     * @param primaryStage La fenêtre principale de l'application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger l'image de fond si disponible
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/wallpaper.jpg")));

            BackgroundImage bgImage = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );

            container.setBackground(new Background(bgImage));
        } catch (Exception e) {
            System.out.println("Image de fond non trouvée, utilisation du fond par défaut");
            // Utiliser un fond par défaut
            container.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        }
    }

    /**
     * Méthode appelée lorsque le bouton "Jouer" est cliqué.
     */
    @FXML
    private void handlePlayButton() {
        System.out.println("Lancement du jeu...");
        ViewManager.getInstance(primaryStage).showPlayView();
    }

    /**
     * Méthode appelée lorsque le bouton "Paramètres" est cliqué.
     */
    @FXML
    private void handleSettingsButton() {
        System.out.println("Ouverture des paramètres...");
        ViewManager.getInstance(primaryStage).showSettingsView();
    }

    /**
     * Méthode appelée lorsque le bouton "Profile" est cliqué.
     */
    @FXML
    private void handleProfileButton() {
        System.out.println("Ouverture du profil...");
        ViewManager.getInstance(primaryStage).showProfileView();
    }

    /**
     * Méthode appelée lorsque le bouton "Quitter" est cliqué.
     */
    @FXML
    private void handleQuitButton() {
        System.out.println("Fermeture de l'application...");
        if (primaryStage != null) {
            primaryStage.close();
        } else {
            Platform.exit();
        }
    }

    // Méthodes alternatives pour compatibilité
    @FXML
    private void startGame() {
        handlePlayButton();
    }
}