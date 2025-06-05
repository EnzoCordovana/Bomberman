package fr.amu.iut;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuPrincipal extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the title label
        Label titleLabel = new Label("Mon Application");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        // Create the "Jouer" button
        Button jouerButton = new Button("Jouer");
        jouerButton.setFont(Font.font("Arial", 18));
        jouerButton.setPrefSize(120, 40);

        // Add button action (optional)
        jouerButton.setOnAction(e -> {
            System.out.println("Bouton Jouer cliqué!");
        });

        // Create VBox layout
        VBox root = new VBox(50); // 50px spacing between elements
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, jouerButton);

        // Create scene and stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Simple Window");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}