package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue des paramètres de l'application.
 * Gère la configuration des options de jeu, des contrôles personnalisés
 * et des paramètres audio/vidéo de l'application Bomberman.
 */
public class SettingsController implements Initializable {

    /** Sélecteur de difficulté du jeu */
    @FXML private ComboBox<String> difficultyCombo;

    /** Case à cocher pour activer/désactiver les power-ups */
    @FXML private CheckBox powerupsCheckbox;

    /** Case à cocher pour activer/désactiver les obstacles */
    @FXML private CheckBox obstaclesCheckbox;

    // Contrôles Joueur 1
    @FXML private TextField moveUpField1;
    @FXML private TextField moveDownField1;
    @FXML private TextField moveLeftField1;
    @FXML private TextField moveRightField1;
    @FXML private TextField placeBombField1;

    // Contrôles Joueur 2
    @FXML private TextField moveUpField2;
    @FXML private TextField moveDownField2;
    @FXML private TextField moveLeftField2;
    @FXML private TextField moveRightField2;
    @FXML private TextField placeBombField2;

    // Contrôles Joueur 3
    @FXML private TextField moveUpField3;
    @FXML private TextField moveDownField3;
    @FXML private TextField moveLeftField3;
    @FXML private TextField moveRightField3;
    @FXML private TextField placeBombField3;

    // Contrôles Joueur 4
    @FXML private TextField moveUpField4;
    @FXML private TextField moveDownField4;
    @FXML private TextField moveLeftField4;
    @FXML private TextField moveRightField4;
    @FXML private TextField placeBombField4;

    /** Référence à la fenêtre principale */
    private Stage primaryStage;

    /**
     * Initialise le contrôleur et configure les éléments de l'interface.
     * Charge les paramètres actuels et configure les composants.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les paramètres actuels
        loadSettings();
    }

    /**
     * Définit la scène principale pour la navigation.
     *
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Charge les paramètres actuels depuis le modèle de configuration.
     * Met à jour l'interface avec les valeurs sauvegardées.
     */
    private void loadSettings() {

        // Joueur 1 (ZQSD)
        moveUpField1.setText("Z");
        moveDownField1.setText("S");
        moveLeftField1.setText("Q");
        moveRightField1.setText("D");
        placeBombField1.setText("ESPACE");

        // Joueur 2 (OKLM)
        moveUpField2.setText("O");
        moveDownField2.setText("L");
        moveLeftField2.setText("K");
        moveRightField2.setText("M");
        placeBombField2.setText("P");

        // Joueur 3 (Flèches)
        moveUpField3.setText("HAUT");
        moveDownField3.setText("BAS");
        moveLeftField3.setText("GAUCHE");
        moveRightField3.setText("DROITE");
        placeBombField3.setText("ENTREE");

        // Joueur 4 (IJKL)
        moveUpField4.setText("I");
        moveDownField4.setText("K");
        moveLeftField4.setText("J");
        moveRightField4.setText("L");
        placeBombField4.setText("U");
    }

    /**
     * Gestionnaire d'événement pour le bouton "Réinitialiser".
     * Remet tous les contrôles à leurs valeurs par défaut.
     */
    @FXML
    private void resetControls() {
        loadSettings();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Appliquer".
     * Sauvegarde les nouveaux paramètres et retourne au menu.
     */
    @FXML
    private void handleApply() {
        // Sauvegarder les paramètres
        System.out.println("Paramètres sauvegardés");

        // Retour au menu
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Annuler".
     * Retourne au menu sans sauvegarder les modifications.
     */
    @FXML
    private void handleCancel() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }
}