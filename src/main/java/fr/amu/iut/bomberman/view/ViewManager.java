package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.Main;
import javafx.stage.Stage;

/**
 * Gestionnaire de navigation entre les vues de l'application Bomberman.
 * Implémente le pattern Singleton pour garantir une instance unique.
 * Responsable de la coordination entre les différentes vues de l'application.
 */
public class ViewManager implements IViewManager {

    /** Instance unique du ViewManager (Singleton) */
    private static ViewManager instance;

    /** Référence au stage principal de l'application */
    private final Stage primaryStage;

    /**
     * Constructeur privé pour le pattern Singleton.
     *
     * @param primaryStage Le stage principal de l'application
     */
    private ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Retourne l'instance unique du ViewManager.
     * Crée l'instance si elle n'existe pas encore.
     *
     * @param stage Le stage principal de l'application
     * @return L'instance unique du ViewManager
     */
    public static ViewManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new ViewManager(stage);
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * Navigue vers la vue du menu principal.
     */
    @Override
    public void showMenuView() {
        System.out.println("Navigation vers MenuView");
        Main.changeView("MenuView.fxml");
    }

    /**
     * {@inheritDoc}
     * Navigue vers la vue de jeu principale.
     */
    @Override
    public void showPlayView() {
        System.out.println("Navigation vers PlayView");
        Main.changeView("PlayView.fxml");
    }

    /**
     * {@inheritDoc}
     * Navigue vers la vue des paramètres.
     */
    @Override
    public void showSettingsView() {
        System.out.println("Navigation vers SettingsView");
        Main.changeView("SettingsView.fxml");
    }

    /**
     * {@inheritDoc}
     * Navigue vers la vue du profil utilisateur.
     */
    @Override
    public void showProfileView() {
        System.out.println("Navigation vers ProfileView");
        Main.changeView("ProfileView.fxml");
    }

    /**
     * Ferme proprement l'application.
     * Libère les ressources et termine le processus.
     */
    public void exitApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }

    /**
     * Retourne le stage principal de l'application.
     *
     * @return Le stage principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}