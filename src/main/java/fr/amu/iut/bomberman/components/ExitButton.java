package fr.amu.iut.bomberman.components;

import javafx.stage.Stage;

/**
 * Bouton personnalisé pour quitter le Stage.
 */
public class ExitButton extends CustomButton {

    private final Stage stage;

    /**
     * Constructeur de ExitButton.
     *
     * @param stage Le Stage à fermer.
     */
    public ExitButton(Stage stage) {
        super("Quitter");
        this.stage = stage;
        setButtonAction();
    }

    /**
     * Définit l'action du bouton pour fermer le Stage.
     */
    @Override
    public void setButtonAction() {
        this.setOnAction(event -> stage.close());
    }
}