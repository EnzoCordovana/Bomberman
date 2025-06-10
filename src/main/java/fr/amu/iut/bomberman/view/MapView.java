package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.model.map.IMap;
import fr.amu.iut.bomberman.model.map.Block;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

/**
 * Vue responsable de l'affichage de la carte de jeu.
 * Respecte le principe de séparation des responsabilités (MVC).
 */
public class MapView extends GridPane implements IMapView {
    private final IMap map;
    private static final int CELL_SIZE = 32;

    public MapView(IMap map) {
        this.map = map;
        this.setHgap(1);
        this.setVgap(1);
        this.setStyle("-fx-background-color: #2d5016;"); // Vert foncé comme fond
        drawMap();
    }

    @Override
    public void refreshBlock(int x, int y) {
        // Vérification que la position est valide
        if (map.isValidPosition(x, y)) {
            // Rechercher le rectangle existant à cette position
            Rectangle existingRect = findRectangleAt(x, y);

            if (existingRect != null) {
                // Mettre à jour la couleur du rectangle existant
                updateRectangleAppearance(existingRect, map.getBlock(x, y));
            } else {
                // Si pas de rectangle trouvé, en créer un nouveau
                Rectangle newRect = createRectangleForBlock(map.getBlock(x, y));
                this.add(newRect, x, y);
            }
        }
    }

    /**
     * Trouve le rectangle à une position spécifique dans la grille.
     * @param x Coordonnée X
     * @param y Coordonnée Y
     * @return Le rectangle à cette position, null si non trouvé
     */
    private Rectangle findRectangleAt(int x, int y) {
        return getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .map(node -> (Rectangle) node)
                .filter(rect -> {
                    Integer colIndex = GridPane.getColumnIndex(rect);
                    Integer rowIndex = GridPane.getRowIndex(rect);
                    return (colIndex != null ? colIndex : 0) == x &&
                            (rowIndex != null ? rowIndex : 0) == y;
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Met à jour l'apparence d'un rectangle selon le type de bloc.
     * @param rectangle Le rectangle à mettre à jour
     * @param block Le bloc correspondant
     */
    private void updateRectangleAppearance(Rectangle rectangle, Block block) {
        if (rectangle == null || block == null) return;

        // Mise à jour de la couleur selon le type de bloc
        switch (block.getType()) {
            case EMPTY:
                rectangle.setFill(Color.rgb(45, 80, 22)); // Vert foncé pour l'herbe
                rectangle.setStroke(Color.rgb(35, 70, 12));
                break;
            case DESTRUCTIBLE:
                rectangle.setFill(Color.rgb(139, 69, 19)); // Marron pour les caisses
                rectangle.setStroke(Color.rgb(101, 49, 9));
                break;
            case INDESTRUCTIBLE:
                rectangle.setFill(Color.rgb(105, 105, 105)); // Gris pour les murs
                rectangle.setStroke(Color.rgb(75, 75, 75));
                break;
            default:
                rectangle.setFill(Color.WHITE);
                rectangle.setStroke(Color.BLACK);
        }

        // Ajout d'un effet visuel pour différencier les types
        addVisualEffects(rectangle, block);
    }

    /**
     * Crée un nouveau rectangle pour un bloc donné.
     * @param block Le bloc pour lequel créer le rectangle
     * @return Le rectangle créé
     */
    private Rectangle createRectangleForBlock(Block block) {
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
        updateRectangleAppearance(rect, block);
        return rect;
    }

    /**
     * Ajoute des effets visuels selon le type de bloc.
     * @param rectangle Le rectangle à styliser
     * @param block Le bloc correspondant
     */
    private void addVisualEffects(Rectangle rectangle, Block block) {
        if (rectangle == null || block == null) return;

        switch (block.getType()) {
            case EMPTY:
                // Effet d'herbe avec un léger dégradé
                rectangle.setStyle("-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 2, 0.3, 0, 1);");
                break;
            case DESTRUCTIBLE:
                // Effet de texture pour les caisses
                rectangle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0.5, 2, 2);");
                break;
            case INDESTRUCTIBLE:
                // Effet métallique pour les murs
                rectangle.setStyle("-fx-effect: innershadow(gaussian, rgba(255,255,255,0.1), 1, 0.8, 0, -1);");
                break;
        }
    }

    /**
     * Dessine la carte complète en créant des rectangles colorés
     * pour chaque type de bloc.
     */
    private void drawMap() {
        this.getChildren().clear();

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Rectangle rect = createBlockRectangle(map.getBlock(x, y));
                this.add(rect, x, y);
            }
        }
    }

    /**
     * Crée un rectangle visuel pour un bloc donné.
     *
     * @param block Le bloc à représenter visuellement
     * @return Rectangle coloré selon le type de bloc
     */
    private Rectangle createBlockRectangle(fr.amu.iut.bomberman.model.map.Block block) {
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);

        switch (block.getType()) {
            case EMPTY:
                rect.setFill(Color.LIGHTGREEN);
                rect.setStroke(Color.GREEN);
                break;
            case DESTRUCTIBLE:
                rect.setFill(Color.SANDYBROWN);
                rect.setStroke(Color.SADDLEBROWN);
                addShadowEffect(rect);
                break;
            case INDESTRUCTIBLE:
                rect.setFill(Color.DARKGRAY);
                rect.setStroke(Color.GRAY);
                addShadowEffect(rect);
                break;
        }

        rect.setStrokeWidth(1);
        return rect;
    }

    /**
     * Ajoute un effet d'ombre aux blocs pour un meilleur rendu visuel.
     */
    private void addShadowEffect(Rectangle rect) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(3);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        rect.setEffect(shadow);
    }

    /**
     * Met à jour l'affichage de la carte.
     */
    public void update() {
        drawMap();
    }
}