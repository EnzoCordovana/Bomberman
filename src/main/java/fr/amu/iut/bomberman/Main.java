package fr.amu.iut.bomberman;

import fr.amu.iut.bomberman.controller.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Bomberman.
 * Point d'entrée de l'application JavaFX.
 */
public class Main extends Application {

    /**
     * Méthode appelée au démarrage de l'application JavaFX.
     * Initialise et affiche la vue du menu principal.
     * @param primaryStage La fenêtre principale de l'application
     * @throws Exception En cas d'erreur de chargement des ressources
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge la vue du menu principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuView.fxml"));
        Parent root = loader.load();

        // Passe la référence du stage au contrôleur
        MenuController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        // Configure et affiche la fenêtre principale
        primaryStage.setTitle("Super Bomberman");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Point d'entrée de l'application.
     * @param args Les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}