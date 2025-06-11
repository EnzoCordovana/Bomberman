package fr.amu.iut.bomberman.view;

import fr.amu.iut.bomberman.model.entities.Bomb;
import fr.amu.iut.bomberman.model.entities.Player;
import fr.amu.iut.bomberman.model.game.GameEngine;
import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.model.map.Tile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * MapView corrigé pour ressembler à l'image Bomberman
 */
public class MapView extends Canvas {
    private static final int TILE_SIZE = 32;
    private static final int PLAYER_SIZE = 24;
    private static final int BOMB_SIZE = 20;

    private final GameEngine gameEngine;
    private final GameMap gameMap;
    private final GraphicsContext gc;

    public MapView(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.gameMap = gameEngine.getGameMap();
        this.gc = getGraphicsContext2D();

        setWidth(gameMap.getWidth() * TILE_SIZE);
        setHeight(gameMap.getHeight() * TILE_SIZE);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }

    public void update() {
        gc.clearRect(0, 0, getWidth(), getHeight());

        drawMap();
        drawBombs();
        drawPlayers();
    }

    private void drawMap() {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile != null) {
                    drawTile(x, y, tile.getType());
                }
            }
        }
    }

    private void drawTile(int x, int y, Tile.TileType type) {
        double pixelX = x * TILE_SIZE;
        double pixelY = y * TILE_SIZE;

        switch (type) {
            case FLOOR:
                // Sol vert comme dans l'image
                gc.setFill(Color.rgb(34, 139, 34)); // Forest Green
                gc.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
                break;

            case WALL:
                // Murs gris foncé indestructibles
                gc.setFill(Color.rgb(105, 105, 105)); // Dim Gray
                gc.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);

                // Effet de relief
                gc.setFill(Color.rgb(169, 169, 169)); // Dark Gray highlight
                gc.fillRect(pixelX + 2, pixelY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                gc.setFill(Color.rgb(105, 105, 105));
                gc.fillRect(pixelX + 4, pixelY + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                break;

            case DESTRUCTIBLE_WALL:
                // Murs marron destructibles
                gc.setFill(Color.rgb(139, 69, 19)); // Saddle Brown
                gc.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);

                // Motif de briques
                gc.setStroke(Color.rgb(160, 82, 45));
                gc.setLineWidth(1);
                gc.strokeRect(pixelX + 2, pixelY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                gc.strokeLine(pixelX + TILE_SIZE/2, pixelY + 4, pixelX + TILE_SIZE/2, pixelY + TILE_SIZE - 4);
                break;

            case EXPLOSION:
                // Explosion orange/rouge
                gc.setFill(Color.rgb(255, 69, 0)); // Orange Red
                gc.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);

                // Effet de flamme
                gc.setFill(Color.rgb(255, 215, 0)); // Gold
                gc.fillOval(pixelX + 4, pixelY + 4, TILE_SIZE - 8, TILE_SIZE - 8);

                gc.setFill(Color.rgb(255, 255, 0)); // Yellow
                gc.fillOval(pixelX + 8, pixelY + 8, TILE_SIZE - 16, TILE_SIZE - 16);
                break;

            default:
                gc.setFill(Color.WHITE);
                gc.fillRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
        }

        // Bordure subtile pour toutes les tuiles
        gc.setStroke(Color.rgb(0, 0, 0, 0.2));
        gc.setLineWidth(0.5);
        gc.strokeRect(pixelX, pixelY, TILE_SIZE, TILE_SIZE);
    }

    private void drawBombs() {
        for (Bomb bomb : gameEngine.getBombs()) {
            double pixelX = bomb.getX() * TILE_SIZE;
            double pixelY = bomb.getY() * TILE_SIZE;

            double bombX = pixelX + (TILE_SIZE - BOMB_SIZE) / 2;
            double bombY = pixelY + (TILE_SIZE - BOMB_SIZE) / 2;

            // Animation de clignotement
            double timeProgress = bomb.getTimeProgress();
            boolean isBlinking = timeProgress > 0.6 && ((System.currentTimeMillis() / 200) % 2 == 0);

            if (!isBlinking) {
                // Corps de la bombe noir
                gc.setFill(Color.BLACK);
                gc.fillOval(bombX, bombY, BOMB_SIZE, BOMB_SIZE);

                // Reflet blanc
                gc.setFill(Color.WHITE);
                gc.fillOval(bombX + 3, bombY + 3, BOMB_SIZE * 0.3, BOMB_SIZE * 0.3);

                // Mèche marron
                gc.setStroke(Color.rgb(139, 69, 19));
                gc.setLineWidth(3);
                double fuseX = bombX + BOMB_SIZE / 2;
                double fuseY = bombY;
                gc.strokeLine(fuseX, fuseY, fuseX - 3, fuseY - 8);

                // Étincelle orange si proche de l'explosion
                if (timeProgress > 0.7) {
                    gc.setFill(Color.ORANGE);
                    gc.fillOval(fuseX - 5, fuseY - 10, 6, 6);
                }
            }
        }
    }

    private void drawPlayers() {
        for (Player player : gameEngine.getPlayers()) {
            if (player.isAlive()) {
                drawPlayer(player);
            }
        }
    }

    private void drawPlayer(Player player) {
        double pixelX = player.getX() - PLAYER_SIZE / 2.0;
        double pixelY = player.getY() - PLAYER_SIZE / 2.0;

        Color playerColor = player.getColor();

        // Ombre du joueur
        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillOval(pixelX + 2, pixelY + 2, PLAYER_SIZE, PLAYER_SIZE);

        // Corps du joueur
        gc.setFill(playerColor);
        gc.fillOval(pixelX, pixelY, PLAYER_SIZE, PLAYER_SIZE);

        // Contour plus foncé
        gc.setStroke(playerColor.darker());
        gc.setLineWidth(2);
        gc.strokeOval(pixelX, pixelY, PLAYER_SIZE, PLAYER_SIZE);

        // Yeux blancs
        gc.setFill(Color.WHITE);
        gc.fillOval(pixelX + 5, pixelY + 6, 5, 5);
        gc.fillOval(pixelX + 14, pixelY + 6, 5, 5);

        // Pupilles noires
        gc.setFill(Color.BLACK);
        gc.fillOval(pixelX + 6, pixelY + 7, 3, 3);
        gc.fillOval(pixelX + 15, pixelY + 7, 3, 3);

        // Numéro du joueur
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        String playerNumber = String.valueOf(player.getId() + 1);
        gc.fillText(playerNumber, pixelX + PLAYER_SIZE/2 - 3, pixelY + PLAYER_SIZE - 3);

        // Indicateur de vie si blessé
        if (player.getLives() < 3) {
            drawHealthIndicator(player, pixelX, pixelY - 6);
        }
    }

    private void drawHealthIndicator(Player player, double x, double y) {
        double barWidth = PLAYER_SIZE;
        double barHeight = 3;

        // Fond rouge
        gc.setFill(Color.RED);
        gc.fillRect(x, y, barWidth, barHeight);

        // Barre de vie verte
        double healthRatio = (double) player.getLives() / 3.0;
        gc.setFill(Color.LIME);
        gc.fillRect(x, y, barWidth * healthRatio, barHeight);

        // Contour
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, barWidth, barHeight);
    }

    public double getViewWidth() {
        return gameMap.getWidth() * TILE_SIZE;
    }

    public double getViewHeight() {
        return gameMap.getHeight() * TILE_SIZE;
    }
}