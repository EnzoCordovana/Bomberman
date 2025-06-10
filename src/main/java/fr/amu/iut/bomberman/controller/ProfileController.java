package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue du profil utilisateur (ProfileView).
 * Gère l'affichage et la modification des informations du profil du joueur.
 */
public class ProfileController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private ImageView avatarImage;
    @FXML private Label gamesPlayedLabel;
    @FXML private Label winsLabel;
    @FXML private Label bestScoreLabel;
    @FXML private Label playTimeLabel;
    @FXML private HBox achievementsContainer;

    private Stage primaryStage;
    //private PlayerModel playerModel;

    /**
     * Initialise le contrôleur et charge les données du profil.
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les données du profil
        loadProfileData();

        // Charger l'avatar par défaut
        avatarImage.setImage(new Image(getClass().getResourceAsStream("/assets/default_avatar.jpg")));
    }

    /**
     * Définit la scène principale pour la navigation.
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Charge les données du profil depuis le modèle.
     */
    private void loadProfileData() {
        // Exemple de chargement de données - à adapter avec votre modèle
        usernameField.setText("Joueur1");
        gamesPlayedLabel.setText("42");
        winsLabel.setText("23");
        bestScoreLabel.setText("1250");
        playTimeLabel.setText("12h 34m");

        // Charger les récompenses
        loadAchievements();
    }

    /**
     * Charge et affiche les récompenses du joueur.
     */
    private void loadAchievements() {
        // Exemple de chargement de récompenses
        String[] achievements = {
                "Débutant", "Bombardier", "Survivant", "Champion"
        };

        for (String achievement : achievements) {
            Label achievementLabel = new Label(achievement);
            achievementLabel.setStyle("-fx-text-fill: white; -fx-background-color: #444444; -fx-padding: 5;");
            achievementsContainer.getChildren().add(achievementLabel);
        }
    }

    /**
     * Gère l'action du bouton "Changer d'avatar".
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
     * Gère l'action du bouton "Enregistrer".
     * Sauvegarde les modifications du profil.
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
     * Gère l'action du bouton "Retour".
     * Retourne au menu principal sans sauvegarder.
     */
    @FXML
    private void handleBack() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }
}