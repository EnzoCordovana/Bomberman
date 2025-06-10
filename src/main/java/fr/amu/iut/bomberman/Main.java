package fr.amu.iut.bomberman;

import fr.amu.iut.bomberman.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuView.fxml"));
        Parent root = loader.load();

        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        primaryStage.setTitle("Super Bomberman");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();

        MenuController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeView(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/view/" + fxmlFile)));
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}