package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.components.ExitButton;
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
 * Contrôleur pour la vue du menu principal.
 * Gère les interactions utilisateur dans le menu principal.
 */
public class MenuController implements Initializable {

    @FXML
    public BorderPane container;
    /** Référence à la fenêtre principale de l'application */
    private Stage primaryStage;

    @FXML
    private Button quitButton;

    /**
     * Méthode appelée lors du clic sur le bouton "Jouer"
     */
    @FXML
    private void startGame() {
        ViewManager.getInstance(primaryStage).showPlayView();
    }

    /**
     * Définit la référence à la fenêtre principale.
     * @param primaryStage La fenêtre principale de l'application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (quitButton != null) {
            GridPane parent = (GridPane) quitButton.getParent();
            if (parent != null) {
                int index = parent.getChildren().indexOf(quitButton);
                parent.getChildren().set(index, quitButton);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger l'image de fond
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/wallpaper.jpg")));

        // Créer un BackgroundImage avec l'image chargée
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        // Appliquer l'image de fond au VBox racine
        container.setBackground(new Background(bgImage));
    }

    /**
     * Méthode appelée lorsque le bouton "Jouer" est cliqué.
     * Affiche la vue du mode jouer.
     */
    @FXML
    private void handlePlayButton() {
        ViewManager.getInstance(primaryStage).showPlayView();
    }

    /**
     * Méthode appelée lorsque le bouton "Paramètres" est cliqué.
     * Affiche la vue des paramètres.
     */
    @FXML
    private void handleSettingsButton() {
        ViewManager.getInstance(primaryStage).showSettingsView();
    }

    /**
     * Méthode appelée lorsque le bouton "Profile" est cliqué.
     * Affiche la vue du profile.
     */
    @FXML
    private void handleProfileButton() {
        ViewManager.getInstance(primaryStage).showProfileView();
    }

    /**
     * Méthode appelée lorsque le bouton "Quitter" est cliqué.
     * Ferme l'application.
     */
    @FXML
    private void handleQuitButton() {
        if (primaryStage != null) {
            primaryStage.close();
        } else {
            Platform.exit();
        }
    }
}