package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue des paramètres (SettingsView).
 * Gère la configuration des paramètres de jeu, des contrôles et de l'audio.
 */
public class SettingsController implements Initializable {

    @FXML private ComboBox<String> difficultyCombo;
    @FXML private Spinner<Integer> playerCountSpinner;
    @FXML private Spinner<Integer> gameDurationSpinner;
    @FXML private CheckBox powerupsCheckbox;
    @FXML private CheckBox obstaclesCheckbox;

    @FXML private TextField moveUpField;
    @FXML private TextField moveDownField;
    @FXML private TextField moveLeftField;
    @FXML private TextField moveRightField;
    @FXML private TextField placeBombField;

    @FXML private Slider masterVolumeSlider;
    @FXML private Slider musicVolumeSlider;
    @FXML private Slider effectsVolumeSlider;
    @FXML private CheckBox muteCheckbox;

    @FXML private Label masterVolumeLabel;
    @FXML private Label musicVolumeLabel;
    @FXML private Label effectsVolumeLabel;

    private Stage primaryStage;
    //private GameSettings gameSettings;

    /**
     * Initialise le contrôleur et configure les éléments de l'interface.
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les spinners
        initializeSpinners();

        // Charger les paramètres actuels
        loadSettings();

        // Configurer les écouteurs pour les sliders
        setupVolumeSliders();
    }

    /**
     * Définit la scène principale pour la navigation.
     * @param stage La scène principale de l'application
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Initialise les Spinners avec leurs valeurs et limites.
     */
    private void initializeSpinners() {
        // Spinner pour le nombre de joueurs
        SpinnerValueFactory<Integer> playerCountFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 2);
        playerCountSpinner.setValueFactory(playerCountFactory);

        // Spinner pour la durée du jeu
        SpinnerValueFactory<Integer> durationFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10);
        gameDurationSpinner.setValueFactory(durationFactory);
    }

    /**
     * Charge les paramètres actuels depuis le modèle.
     */
    private void loadSettings() {
        // Exemple de chargement - à adapter avec votre modèle
        //gameSettings = GameSettings.getInstance();

        // Paramètres de jeu
        difficultyCombo.getSelectionModel().select(1); // Moyen par défaut
        playerCountSpinner.getValueFactory().setValue(2);
        gameDurationSpinner.getValueFactory().setValue(10);
        powerupsCheckbox.setSelected(true);
        obstaclesCheckbox.setSelected(true);

        // Contrôles
        moveUpField.setText("Z");
        moveDownField.setText("S");
        moveLeftField.setText("Q");
        moveRightField.setText("D");
        placeBombField.setText("Espace");

        // Audio
        masterVolumeSlider.setValue(70);
        musicVolumeSlider.setValue(50);
        effectsVolumeSlider.setValue(80);
        muteCheckbox.setSelected(false);
    }

    /**
     * Configure les écouteurs pour les sliders de volume.
     */
    private void setupVolumeSliders() {
        masterVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            masterVolumeLabel.setText(String.format("%.0f%%", newVal));
        });

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicVolumeLabel.setText(String.format("%.0f%%", newVal));
        });

        effectsVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            effectsVolumeLabel.setText(String.format("%.0f%%", newVal));
        });
    }

    /**
     * Gère l'action du bouton "Réinitialiser" pour les contrôles.
     */
    @FXML
    private void resetControls() {
        moveUpField.setText("Z");
        moveDownField.setText("S");
        moveLeftField.setText("Q");
        moveRightField.setText("D");
        placeBombField.setText("Espace");
    }

    /**
     * Gère l'action du bouton "Appliquer".
     * Sauvegarde les nouveaux paramètres.
     */
    @FXML
    private void handleApply() {
        // Sauvegarder les paramètres
        System.out.println("Paramètres sauvegardés");

        // Retour au menu
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Gère l'action du bouton "Annuler".
     * Retourne au menu sans sauvegarder les modifications.
     */
    @FXML
    private void handleCancel() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }
}