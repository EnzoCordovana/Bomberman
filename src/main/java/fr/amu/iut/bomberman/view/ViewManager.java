package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.Main;
import javafx.stage.Stage;

/**
 * ViewManager pour gérer la navigation entre les vues - MVC View
 */
public class ViewManager implements IViewManager {
    private static ViewManager instance;
    private final Stage primaryStage;

    private ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static ViewManager getInstance(Stage stage) {
        if (instance == null) {
            instance = new ViewManager(stage);
        }
        return instance;
    }

    @Override
    public void showMenuView() {
        System.out.println("Navigation vers MenuView");
        Main.changeView("MenuView.fxml");
    }

    @Override
    public void showPlayView() {
        System.out.println("Navigation vers PlayView");
        Main.changeView("PlayView.fxml");
    }

    @Override
    public void showSettingsView() {
        System.out.println("Navigation vers SettingsView");
        Main.changeView("SettingsView.fxml");
    }

    @Override
    public void showProfileView() {
        System.out.println("Navigation vers ProfileView");
        Main.changeView("ProfileView.fxml");
    }

    /**
     * Ferme l'application
     */
    public void exitApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }

    // Getter pour accéder au stage principal
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}