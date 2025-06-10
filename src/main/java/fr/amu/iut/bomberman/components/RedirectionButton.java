package fr.amu.iut.bomberman.components;

import javafx.scene.Scene;
import fr.amu.iut.bomberman.view.ViewManager;
import javafx.stage.Stage;

/**
 * Bouton personnalisé pour la redirection vers une autre vue.
 */
public class RedirectionButton extends CustomButton {

    private final Stage stage;
    private final String viewName;

    /**
     * Constructeur de RedirectionButton.
     *
     * @param stage Le Stage principal de l'application.
     * @param viewName Le nom de la vue vers laquelle rediriger.
     * @param text Le texte à afficher sur le bouton.
     */
    public RedirectionButton(Stage stage, String viewName, String text) {
        super(text);
        this.stage = stage;
        this.viewName = viewName;
        setButtonAction();
    }

    /**
     * Définit l'action du bouton pour rediriger vers une autre vue.
     */
    @Override
    public void setButtonAction() {
        this.setOnAction(event -> {
            // Utilisez ViewManager pour changer de vue
            ViewManager viewManager = ViewManager.getInstance(stage);
            switch (viewName) {
                case "PlayView":
                    viewManager.showPlayView();
                    break;
                case "SettingsView":
                    viewManager.showSettingsView();
                    break;
                case "ProfileView":
                    viewManager.showProfileView();
                    break;
                default:
                    System.err.println("Vue inconnue: " + viewName);
            }
        });
    }
}