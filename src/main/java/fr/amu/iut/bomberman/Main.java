package fr.amu.iut.bomberman;

import fr.amu.iut.bomberman.controller.MenuController;
import fr.amu.iut.bomberman.controller.PlayController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Classe principale de l'application Bomberman MVP.
 * Gère le démarrage de l'application JavaFX et la navigation entre les vues.
 */
public class Main extends Application {

    /** Stage principal de l'application */
    private static Stage primaryStage;

    /** Scène principale partagée pour toutes les vues */
    private static Scene scene;

    /**
     * Point d'entrée de l'application JavaFX.
     * Initialise la fenêtre principale et charge la vue du menu.
     *
     * @param stage Le stage principal fourni par JavaFX
     * @throws Exception Si une erreur survient lors du chargement de la vue
     */
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
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);
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

    /**
     * Point d'entrée principal du programme.
     * Lance l'application JavaFX.
     *
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        System.out.println("Lancement Bomberman MVP...");
        launch(args);
    }

    /**
     * Change dynamiquement la vue affichée dans l'application.
     * Configure automatiquement le contrôleur selon le type de vue chargée.
     *
     * @param fxmlFile Nom du fichier FXML à charger (ex: "MenuView.fxml")
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

    /**
     * Retourne le stage principal de l'application.
     *
     * @return Le stage principal
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Retourne la scène principale de l'application.
     *
     * @return La scène principale
     */
    public static Scene getScene() {
        return scene;
    }
}