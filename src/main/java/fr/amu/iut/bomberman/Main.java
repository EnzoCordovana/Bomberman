package fr.amu.iut.bomberman;

import fr.amu.iut.bomberman.controller.MenuController;
import fr.amu.iut.bomberman.controller.PlayController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main corrigé pour Bomberman MVP
 */
public class Main extends Application {

    private static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Charger le MENU principal (pas directement le jeu)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuView.fxml"));
        Parent root = loader.load();

        // Créer la scène
        scene = new Scene(root, 1000, 700);

        // Charger les styles CSS (corrigé le chemin)
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Styles CSS non trouvés, utilisation des styles par défaut");
        }

        // Configuration de la fenêtre
        primaryStage.setTitle("Bomberman MVP");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        // Configurer le contrôleur MENU (pas Play!)
        MenuController controller = loader.getController();
        if (controller != null) {
            controller.setPrimaryStage(primaryStage);
        }

        System.out.println("=== BOMBERMAN MVP ===");
        System.out.println("Menu principal chargé");
        System.out.println("Cliquez sur 'Jouer' pour commencer");
        System.out.println("====================");
    }

    public static void main(String[] args) {
        System.out.println("Lancement Bomberman MVP...");
        launch(args);
    }

    /**
     * Méthode pour changer de vue dynamiquement
     */
    public static void changeView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            scene.setRoot(root);

            // Configurer le contrôleur selon la vue chargée
            Object controller = loader.getController();

            if (controller instanceof PlayController) {
                ((PlayController) controller).setPrimaryStage(primaryStage);
                System.out.println("PlayController configuré");
            } else if (controller instanceof MenuController) {
                ((MenuController) controller).setPrimaryStage(primaryStage);
                System.out.println("MenuController configuré");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du changement de vue: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Scene getScene() {
        return scene;
    }
}