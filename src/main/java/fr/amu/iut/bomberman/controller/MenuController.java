package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Contrôleur pour la vue du menu principal.
 * Gère les interactions utilisateur dans le menu principal.
 */
public class MenuController {

    /** Référence à la fenêtre principale de l'application */
    private Stage primaryStage;

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
        primaryStage.close();
    }
}