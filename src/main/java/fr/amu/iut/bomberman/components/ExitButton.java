package fr.amu.iut.bomberman.components;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Bouton personnalisé pour quitter le Stage.
 */
public class ExitButton extends Button {

    private final Stage stage;

    /**
     * Constructeur de ExitButton.
     *
     * @param stage Le Stage à fermer.
     */
    public ExitButton(Stage stage) {
        super("Quitter");
        this.stage = stage;
        initializeButton();
        setButtonAction();
    }

    private void initializeButton() {
        this.setStyle("-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-pref-width: 150px; " +
                "-fx-pref-height: 40px;");
    }

    /**
     * Définit l'action du bouton pour fermer le Stage.
     */
    public void setButtonAction() {
        this.setOnAction(event -> handleQuitButton());
    }

    private void handleQuitButton() {
        if (stage != null) {
            stage.close();
        } else {
            Platform.exit();
        }
    }
}