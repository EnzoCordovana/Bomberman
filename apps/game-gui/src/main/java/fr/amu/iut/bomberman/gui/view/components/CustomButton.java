package fr.amu.iut.bomberman.gui.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class CustomButton extends StackPane {
    public CustomButton() {
        System.out.println("Class location: " + CustomButton.class.getProtectionDomain().getCodeSource().getLocation());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CustomButton.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ajoute une classe CSS (définie dans le fxml par exemple)
        this.getStyleClass().add("custom-button");
    }
}