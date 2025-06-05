package fr.amu.iut.bomberman.gui;

import fr.amu.iut.bomberman.gui.view.components.CustomButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameApplication extends Application {

    private BorderPane root;
    private CustomButton button;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        root.setCenter(new CustomButton());

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Palette");
        primaryStage.show();
    }
}
