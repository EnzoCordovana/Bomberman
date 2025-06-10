package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.model.map.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Vue de la carte de jeu Bomberman.
 * Gère l'affichage graphique de la carte et des éléments du jeu.
 */
public class MapView extends Canvas {
    private static final int TILE_SIZE = 32;
    private final GameMap map;
    private final GraphicsContext gc;
    private final Map<Tile.TileType, Image> tileImages;

    /**
     * Constructeur de MapView.
     * @param map La carte à afficher
     */
    public MapView(GameMap map) {
        this.map = map;
        this.gc = getGraphicsContext2D();

        // Initialisation de la taille du canvas
        setWidth(map.getWidth() * TILE_SIZE);
        setHeight(map.getHeight() * TILE_SIZE);

        // Initialisation des images des tuiles
        this.tileImages = new HashMap<>();
        initializeTileImages();
    }

    /**
     * Initialise les images des différents types de tuiles.
     */
    private void initializeTileImages() {
        // Création d'images simples pour chaque type de tuile
        tileImages.put(Tile.TileType.FLOOR, createColoredTile(Color.WHITE));
        tileImages.put(Tile.TileType.WALL, createColoredTile(Color.GRAY));
        tileImages.put(Tile.TileType.DESTRUCTIBLE_WALL, createColoredTile(Color.LIGHTGRAY));
        tileImages.put(Tile.TileType.BOMB, createColoredTile(Color.BLACK));
        tileImages.put(Tile.TileType.EXPLOSION, createColoredTile(Color.RED));
        tileImages.put(Tile.TileType.POWERUP, createColoredTile(Color.GOLD));
    }

    /**
     * Crée une image de tuile colorée.
     * @param color La couleur de la tuile
     * @return Image de la tuile
     */
    private Image createColoredTile(Color color) {
        Canvas tempCanvas = new Canvas(TILE_SIZE, TILE_SIZE);
        GraphicsContext tempGc = tempCanvas.getGraphicsContext2D();
        tempGc.setFill(color);
        tempGc.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

        // Ajout d'une bordure pour mieux voir les tuiles
        tempGc.setStroke(Color.BLACK);
        tempGc.strokeRect(0, 0, TILE_SIZE, TILE_SIZE);

        return tempCanvas.snapshot(null, null);
    }

    /**
     * Met à jour l'affichage de la carte.
     */
    public void update() {
        gc.clearRect(0, 0, getWidth(), getHeight());

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTile(x, y);
                if (tile != null) {
                    Image image = tileImages.get(tile.getType());
                    if (image != null) {
                        gc.drawImage(image, x * TILE_SIZE, y * TILE_SIZE);
                    }
                }
            }
        }
    }

    /**
     * @return La largeur de la vue en pixels
     */
    public double getViewWidth() {
        return map.getWidth() * TILE_SIZE;
    }

    /**
     * @return La hauteur de la vue en pixels
     */
    public double getViewHeight() {
        return map.getHeight() * TILE_SIZE;
    }
}