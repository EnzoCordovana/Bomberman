package fr.amu.iut.bomberman.controller;

import fr.amu.iut.bomberman.model.game.GameSettings;
import fr.amu.iut.bomberman.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue des paramètres de l'application.
 * Gère la configuration des options de jeu, des contrôles personnalisés
 * et des paramètres audio/vidéo de l'application Bomberman.
 * Intégré avec le système de sauvegarde des paramètres.
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

    /** Instance des paramètres du jeu */
    private GameSettings gameSettings;

    /** Mapping des champs de texte pour faciliter la gestion */
    private Map<Integer, Map<String, TextField>> playerFields;

    /** TextField actuellement en attente d'une nouvelle touche */
    private TextField waitingForKeyField;

    /**
     * Initialise le contrôleur et configure les éléments de l'interface.
     * Charge les paramètres actuels et configure les composants.
     *
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs
     * @param resources Les ressources utilisées pour localiser l'objet racine
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameSettings = GameSettings.getInstance();
        initializeFieldMappings();
        loadSettings();
        setupKeyDetection();
    }

    /**
     * Initialise le mapping des champs de texte pour faciliter l'accès.
     */
    private void initializeFieldMappings() {
        playerFields = new HashMap<>();

        // Joueur 1
        Map<String, TextField> player1Fields = new HashMap<>();
        player1Fields.put("up", moveUpField1);
        player1Fields.put("down", moveDownField1);
        player1Fields.put("left", moveLeftField1);
        player1Fields.put("right", moveRightField1);
        player1Fields.put("bomb", placeBombField1);
        playerFields.put(0, player1Fields);

        // Joueur 2
        Map<String, TextField> player2Fields = new HashMap<>();
        player2Fields.put("up", moveUpField2);
        player2Fields.put("down", moveDownField2);
        player2Fields.put("left", moveLeftField2);
        player2Fields.put("right", moveRightField2);
        player2Fields.put("bomb", placeBombField2);
        playerFields.put(1, player2Fields);

        // Joueur 3
        Map<String, TextField> player3Fields = new HashMap<>();
        player3Fields.put("up", moveUpField3);
        player3Fields.put("down", moveDownField3);
        player3Fields.put("left", moveLeftField3);
        player3Fields.put("right", moveRightField3);
        player3Fields.put("bomb", placeBombField3);
        playerFields.put(2, player3Fields);

        // Joueur 4
        Map<String, TextField> player4Fields = new HashMap<>();
        player4Fields.put("up", moveUpField4);
        player4Fields.put("down", moveDownField4);
        player4Fields.put("left", moveLeftField4);
        player4Fields.put("right", moveRightField4);
        player4Fields.put("bomb", placeBombField4);
        playerFields.put(3, player4Fields);
    }

    /**
     * Configure la détection des touches pour les champs de contrôles.
     */
    private void setupKeyDetection() {
        for (Map<String, TextField> playerFieldsMap : playerFields.values()) {
            for (TextField field : playerFieldsMap.values()) {
                setupTextFieldForKeyDetection(field);
            }
        }
    }

    /**
     * Configure un TextField pour la détection de touches.
     */
    private void setupTextFieldForKeyDetection(TextField field) {
        // Rendre le champ en lecture seule pour éviter la saisie manuelle
        field.setEditable(false);

        // Gérer le focus pour la détection
        field.setOnMouseClicked(event -> {
            waitingForKeyField = field;
            field.setStyle("-fx-background-color: #666666; -fx-text-fill: yellow;");
            field.setText("Appuyez sur une touche...");
            field.requestFocus();
        });

        // Détecter les touches pressées
        field.setOnKeyPressed(this::handleKeyPressed);

        // Gérer la perte de focus
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && field == waitingForKeyField) {
                resetFieldAppearance(field);
                waitingForKeyField = null;
            }
        });
    }

    /**
     * Gère la détection d'une nouvelle touche.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (waitingForKeyField != null) {
            KeyCode keyCode = event.getCode();

            // Ignorer certaines touches système
            if (isSystemKey(keyCode)) {
                return;
            }

            // Vérifier si la touche n'est pas déjà utilisée
            if (isKeyAlreadyUsed(keyCode, waitingForKeyField)) {
                showKeyConflictAlert(keyCode);
                return;
            }

            // Assigner la nouvelle touche
            String displayName = GameSettings.keyCodeToDisplayName(keyCode);
            waitingForKeyField.setText(displayName);
            waitingForKeyField.setUserData(keyCode);

            resetFieldAppearance(waitingForKeyField);
            waitingForKeyField = null;

            event.consume();
        }
    }

    /**
     * Vérifie si une touche est une touche système à ignorer.
     */
    private boolean isSystemKey(KeyCode keyCode) {
        return keyCode == KeyCode.TAB || keyCode == KeyCode.ESCAPE ||
                keyCode == KeyCode.SHIFT || keyCode == KeyCode.CONTROL ||
                keyCode == KeyCode.ALT || keyCode == KeyCode.WINDOWS;
    }

    /**
     * Vérifie si une touche est déjà utilisée par un autre contrôle.
     */
    private boolean isKeyAlreadyUsed(KeyCode keyCode, TextField currentField) {
        for (Map<String, TextField> playerFieldsMap : playerFields.values()) {
            for (TextField field : playerFieldsMap.values()) {
                if (field != currentField && field.getUserData() == keyCode) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Affiche une alerte de conflit de touches.
     */
    private void showKeyConflictAlert(KeyCode keyCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Conflit de touches");
        alert.setHeaderText("Touche déjà utilisée");
        alert.setContentText("La touche " + GameSettings.keyCodeToDisplayName(keyCode) +
                " est déjà assignée à un autre contrôle.");
        alert.showAndWait();
    }

    /**
     * Remet l'apparence normale d'un TextField.
     */
    private void resetFieldAppearance(TextField field) {
        field.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
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
        // Charger les paramètres généraux
        if (difficultyCombo != null) {
            difficultyCombo.setValue(gameSettings.getDifficulty());
        }
        if (powerupsCheckbox != null) {
            powerupsCheckbox.setSelected(gameSettings.isPowerupsEnabled());
        }
        if (obstaclesCheckbox != null) {
            obstaclesCheckbox.setSelected(gameSettings.isObstaclesEnabled());
        }

        // Charger les contrôles pour chaque joueur
        for (int playerId = 0; playerId < 4; playerId++) {
            loadPlayerControls(playerId);
        }

        System.out.println("🔄 Paramètres chargés dans l'interface");
    }

    /**
     * Charge les contrôles d'un joueur spécifique dans l'interface.
     */
    private void loadPlayerControls(int playerId) {
        GameSettings.PlayerControlSettings controls = gameSettings.getPlayerControls(playerId);
        if (controls == null) return;

        Map<String, TextField> fields = playerFields.get(playerId);
        if (fields == null) return;

        // Mettre à jour chaque champ avec la touche correspondante
        updateTextField(fields.get("up"), controls.up);
        updateTextField(fields.get("down"), controls.down);
        updateTextField(fields.get("left"), controls.left);
        updateTextField(fields.get("right"), controls.right);
        updateTextField(fields.get("bomb"), controls.bomb);
    }

    /**
     * Met à jour un TextField avec une KeyCode.
     */
    private void updateTextField(TextField field, KeyCode keyCode) {
        if (field != null && keyCode != null) {
            field.setText(GameSettings.keyCodeToDisplayName(keyCode));
            field.setUserData(keyCode);
        }
    }

    /**
     * Gestionnaire d'événement pour le bouton "Réinitialiser".
     * Remet tous les contrôles à leurs valeurs par défaut.
     */
    @FXML
    private void resetControls() {
        // Remettre les paramètres par défaut
        gameSettings.resetAllSettings();

        // Recharger l'interface
        loadSettings();

        System.out.println("🔄 Contrôles remis par défaut");
    }

    /**
     * Gestionnaire d'événement pour le bouton "Appliquer".
     * Sauvegarde les nouveaux paramètres et retourne au menu.
     */
    @FXML
    private void handleApply() {
        try {
            // Sauvegarder les paramètres généraux
            if (difficultyCombo != null) {
                gameSettings.setDifficulty(difficultyCombo.getValue());
            }
            if (powerupsCheckbox != null) {
                gameSettings.setPowerupsEnabled(powerupsCheckbox.isSelected());
            }
            if (obstaclesCheckbox != null) {
                gameSettings.setObstaclesEnabled(obstaclesCheckbox.isSelected());
            }

            // Sauvegarder les contrôles pour chaque joueur
            for (int playerId = 0; playerId < 4; playerId++) {
                savePlayerControls(playerId);
            }

            // Persister les paramètres
            gameSettings.saveSettings();

            // Retour au menu
            ViewManager.getInstance(primaryStage).showMenuView();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde des paramètres: " + e.getMessage());
            showErrorAlert("Erreur de sauvegarde", "Impossible de sauvegarder les paramètres.");
        }
    }

    /**
     * Sauvegarde les contrôles d'un joueur spécifique.
     */
    private void savePlayerControls(int playerId) {
        Map<String, TextField> fields = playerFields.get(playerId);
        if (fields == null) return;

        KeyCode up = (KeyCode) fields.get("up").getUserData();
        KeyCode down = (KeyCode) fields.get("down").getUserData();
        KeyCode left = (KeyCode) fields.get("left").getUserData();
        KeyCode right = (KeyCode) fields.get("right").getUserData();
        KeyCode bomb = (KeyCode) fields.get("bomb").getUserData();

        // Vérifier que toutes les touches sont définies
        if (up != null && down != null && left != null && right != null && bomb != null) {
            gameSettings.setPlayerControls(playerId, up, down, left, right, bomb);
        } else {
            System.err.println("⚠️ Contrôles incomplets pour le joueur " + (playerId + 1));
        }
    }

    /**
     * Gestionnaire d'événement pour le bouton "Annuler".
     * Retourne au menu sans sauvegarder les modifications.
     */
    @FXML
    private void handleCancel() {
        ViewManager.getInstance(primaryStage).showMenuView();
    }

    /**
     * Affiche une alerte d'erreur.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}