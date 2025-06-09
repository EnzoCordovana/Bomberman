package fr.amu.iut.bomberman.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe ViewManager gérant la navigation entre les différentes vues de l'application.
 * Implémente le pattern Singleton pour assurer une seule instance de gestionnaire de vues.
 */
public class ViewManager {
    /** Instance unique du ViewManager (pattern Singleton) */
    private static ViewManager instance;

    /** Référence à la fenêtre principale de l'application */
    private final Stage primaryStage;

    /**
     * Constructeur privé pour le pattern Singleton.
     * @param primaryStage La fenêtre principale de l'application
     */
    private ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Méthode pour obtenir l'instance unique du ViewManager.
     * @param stage La fenêtre principale de l'application
     * @return L'instance unique du ViewManager
     */
    public static ViewManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new ViewManager(stage);
        }
        return instance;
    }

    /**
     * Méthode générique pour afficher une vue à partir de son chemin FXML.
     * @param fxmlPath Le chemin vers le fichier FXML de la vue à afficher
     */
    public void showView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la vue du menu principal.
     */
    public void showMenuView() {
        showView("/view/MenuView.fxml");
    }

    /**
     * Affiche la vue du jeu.
     */
    public void showGameView() {
        showView("/view/GameView.fxml");
    }

    /**
     * Affiche la vue des paramètres.
     */
    public void showOptionsView() {
        showView("/view/OptionsView.fxml");
    }

    /**
     * Affiche la vue du profile.
     */
    public void showProfile() {
        showView("/view/ProfileView.fxml");
    }
}