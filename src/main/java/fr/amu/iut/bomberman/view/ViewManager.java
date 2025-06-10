package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.Main;
import javafx.stage.Stage;

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

    public void showMenuView() {
        Main.changeView("MenuView.fxml");
    }

    public void showPlayView() {
        Main.changeView("PlayView.fxml");
    }

    public void showSettingsView() {
        Main.changeView("SettingsView.fxml");
    }

    public void showProfileView() {
        Main.changeView("ProfileView.fxml");
    }
}