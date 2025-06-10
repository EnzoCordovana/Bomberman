package fr.amu.iut.bomberman.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Classe ViewManager gérant la navigation entre les différentes vues de l'application.
 * Implémente le pattern Singleton pour assurer une seule instance de gestionnaire de vues.
 */
public class ViewManager implements IViewManager {
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

    private void loadView(String fxmlFile, String title) {
        try {
            // Charger le FXML depuis le classpath
            URL fxmlUrl = getClass().getResource("/view/" + fxmlFile);
            if (fxmlUrl == null) {
                System.err.println("Fichier FXML non trouvé: /view/" + fxmlFile);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de " + fxmlFile);
            e.printStackTrace();
        }
    }

    /**
     * Affiche la vue du menu principal.
     */
    public void showMenuView() {
        //showView("/view/MenuView.fxml");
        loadView("MenuView.fxml", "Bomberman - Menu");
    }

    /**
     * Affiche la vue du jeu.
     */
    public void showPlayView() {
        //showView("/view/PlayView.fxml");
        loadView("PlayView.fxml", "Bomberman - Play");
    }

    /**
     * Affiche la vue des paramètres.
     */
    public void showSettingsView() {
        //showView("/view/SettingsView.fxml");
        loadView("SettingsView.fxml", "Bomberman - Settings");
    }

    /**
     * Affiche la vue du profile.
     */
    public void showProfileView() {
        //showView("/view/ProfileView.fxml");
        loadView("ProfileView.fxml", "Bomberman - Profile");
    }
}