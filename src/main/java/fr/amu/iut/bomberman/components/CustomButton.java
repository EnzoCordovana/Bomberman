package fr.amu.iut.bomberman.components;

import javafx.scene.control.Button;
import javafx.scene.Scene;

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
        setButtonAction();
    }

    /**
     * Méthode statique pour charger le fichier CSS dans la scène.
     *
     * @param scene La scène à laquelle ajouter le fichier CSS.
     */
    public static void loadCSS(Scene scene) {
        scene.getStylesheets().add(CustomButton.class.getResource("/styles/styles.css").toExternalForm());
    }

    /**
     * Méthode abstraite pour définir l'action du bouton.
     * Les classes dérivées doivent implémenter cette méthode pour définir l'action spécifique du bouton.
     */
    public abstract void setButtonAction();
}