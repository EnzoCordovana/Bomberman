package fr.amu.iut.bomberman.components;

import javafx.scene.control.Button;

/**
 * CustomButton est une classe abstraite qui étend la classe Button de JavaFX.
 * Elle fournit une base commune pour tous les boutons personnalisés.
 */
public abstract class CustomButton extends Button {

    /**
     * Constructeur par défaut de CustomButton.
     */
    public CustomButton() {
        super();
        initialize();
    }

    /**
     * Constructeur de CustomButton avec un texte.
     *
     * @param text Le texte à afficher sur le bouton.
     */
    public CustomButton(String text) {
        super(text);
        initialize();
    }

    /**
     * Initialise les propriétés du bouton.
     */
    private void initialize() {
        // Ajouter une classe CSS pour le style personnalisé
        this.getStyleClass().add("custom-button");

        // Définir des styles par défaut
        this.setStyle("-fx-background-color: #ff7f7f; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-pref-width: 150px; " +
                "-fx-pref-height: 40px;");

        // Ajouter des effets de survol
        this.setOnMouseEntered(event -> {
            this.setStyle("-fx-background-color: #ff5a5a; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-pref-width: 150px; " +
                    "-fx-pref-height: 40px;");
        });

        this.setOnMouseExited(event -> {
            this.setStyle("-fx-background-color: #ff7f7f; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-pref-width: 150px; " +
                    "-fx-pref-height: 40px;");
        });

        // Méthode abstraite pour définir l'action du bouton
        setButtonAction();
    }

    /**
     * Méthode abstraite pour définir l'action du bouton.
     * Les classes dérivées doivent implémenter cette méthode pour définir l'action spécifique du bouton.
     */
    public abstract void setButtonAction();
}