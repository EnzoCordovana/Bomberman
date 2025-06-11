package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue de gestion du profil utilisateur.
 * Permet l'affichage et la modification des informations personnelles,
 * des statistiques de jeu et des récompenses du joueur.
 */
public class ProfileController implements Initializable {

    /** Champ de saisie du nom d'utilisateur */
    @FXML private TextField usernameField;

    /** Image d'avatar du joueur */
    @FXML private ImageView avatarImage;

    /** Label affichant le nombre de parties jouées */
    @FXML private Label gamesPlayedLabel;

    /** Label affichant le nombre de victoires */
    @FXML private Label winsLabel;

    /** Label affichant le temps de jeu total */
    @FXML private Label playTimeLabel;

    /** Référence à la fenêtre principale pour la navigation */
    private Stage primaryStage;

    /**
     * Initialise le contrôleur et charge les données du profil.
     * Configure l'interface utilisateur avec les informations existantes.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les données du profil
        loadProfileData();

        // Charger l'avatar par défaut
        avatarImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/default_avatar.jpg"))));
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
     * Charge les données du profil depuis le modèle de données.
     * Met à jour l'interface avec les statistiques actuelles.
     */
    private void loadProfileData() {
        // Exemple de chargement de données - à adapter avec votre modèle
        usernameField.setText("Joueur1");
        gamesPlayedLabel.setText("42");
        winsLabel.setText("23");
        playTimeLabel.setText("12h 34m");

        // Charger les récompenses
        loadAchievements();
    }

    /**
     * Charge et affiche les récompenses obtenues par le joueur.
     * Parcourt la liste des achievements et les affiche.
     */
    private void loadAchievements() {
        // Exemple de chargement de récompenses
        String[] achievements = {
                "Débutant", "Bombardier", "Survivant", "Champion"
        };
    }

    /**
     * Gestionnaire d'événement pour le bouton "Changer d'avatar".
     * Ouvre un sélecteur de fichier pour choisir une nouvelle image.
     */
    @FXML
    private void handleChangeAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un avatar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            avatarImage.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    /**
     * Gestionnaire d'événement pour le bouton "Enregistrer".
     * Sauvegarde les modifications du profil et retourne au menu.
     */
    @FXML
    private void handleSave() {
        // Sauvegarder les modifications
        String username = usernameField.getText();
        System.out.println("Profil sauvegardé pour: " + username);

        // Retour au menu
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Gestionnaire d'événement pour le bouton "Retour".
     * Retourne au menu principal sans sauvegarder les modifications.
     */
    @FXML
    private void handleBack() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }
}