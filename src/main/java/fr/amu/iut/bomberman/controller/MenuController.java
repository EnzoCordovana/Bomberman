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
 * Contrôleur pour la vue du menu principal de l'application Bomberman.
 * Gère les interactions utilisateur et la navigation vers les autres vues.
 * Implémente le pattern MVC en tant que Controller.
 */
public class MenuController implements Initializable {

    /** Conteneur principal de la vue du menu */
    @FXML
    public BorderPane container;

    /** Référence à la fenêtre principale de l'application */
    private Stage primaryStage;

    /** Bouton de sortie de l'application */
    @FXML
    private Button quitButton;

    /**
     * Définit la référence à la fenêtre principale.
     * Nécessaire pour la navigation et la fermeture de l'application.
     *
     * @param primaryStage La fenêtre principale de l'application
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * Configure l'interface utilisateur et charge les ressources.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
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
     * Gestionnaire d'événement pour le bouton "Jouer".
     * Lance une nouvelle partie en naviguant vers la vue de jeu.
     */
    @FXML
    private void handlePlayButton() {
        System.out.println("Lancement du jeu...");
        ViewManager.getInstance(primaryStage).showPlayView();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Paramètres".
     * Ouvre le menu de configuration de l'application.
     */
    @FXML
    private void handleSettingsButton() {
        System.out.println("Ouverture des paramètres...");
        ViewManager.getInstance(primaryStage).showSettingsView();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Profil".
     * Ouvre la gestion du profil utilisateur.
     */
    @FXML
    private void handleProfileButton() {
        System.out.println("Ouverture du profil...");
        ViewManager.getInstance(primaryStage).showProfileView();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Quitter".
     * Ferme proprement l'application.
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

    /**
     * Méthode alternative pour démarrer le jeu.
     * Fournie pour compatibilité avec différentes liaisons FXML.
     */
    @FXML
    private void startGame() {
        handlePlayButton();
    }
}