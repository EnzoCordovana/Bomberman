package fr.amu.iut;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class MenuPrincipal extends Application {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = 21;
    private static final int BOARD_HEIGHT = 15;
    private static final int CANVAS_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final int CANVAS_HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    private Stage primaryStage;
    private List<Player> players;
    private int[][] board; // 0 = empty, 1 = wall, 2 = destructible wall, 3 = powerup
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Timeline gameLoop;
    private Timeline movementLoop;
    private boolean[] keysPressed = new boolean[256];
    private boolean gameEnded = false;
    private boolean showingFinalState = false; // Add this flag
    private Player winner = null; // Store the winner

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Bomberman");
        primaryStage.setResizable(false);

        showStartMenu();
        primaryStage.show();
    }

    private void showStartMenu() {
        VBox menuLayout = new VBox(30);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("BOMBERMAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setFill(Color.ORANGE);

        Button playButton = new Button("Jouer");
        playButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playButton.setPrefSize(150, 50);
        playButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10;");
        playButton.setOnAction(e -> showPlayerSelection());

        menuLayout.getChildren().addAll(title, playButton);

        Scene startScene = new Scene(menuLayout, 800, 600);
        primaryStage.setScene(startScene);
    }

    private void showPlayerSelection() {
        VBox selectionLayout = new VBox(20);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("Nombre de joueurs");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);

        Button twoPlayersBtn = new Button("2 Joueurs");
        Button threePlayersBtn = new Button("3 Joueurs");
        Button fourPlayersBtn = new Button("4 Joueurs");

        for (Button btn : new Button[]{twoPlayersBtn, threePlayersBtn, fourPlayersBtn}) {
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            btn.setPrefSize(200, 60);
            btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10;");
        }

        twoPlayersBtn.setOnAction(e -> startGame(2));
        threePlayersBtn.setOnAction(e -> startGame(3));
        fourPlayersBtn.setOnAction(e -> startGame(4));

        selectionLayout.getChildren().addAll(title, twoPlayersBtn, threePlayersBtn, fourPlayersBtn);

        Scene selectionScene = new Scene(selectionLayout, 800, 600);
        primaryStage.setScene(selectionScene);
    }

    private void startGame(int numPlayers) {
        initializeGame(numPlayers);

        StackPane gameLayout = new StackPane();
        gameLayout.setStyle("-fx-background-color: #34495e;");

        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        gameLayout.getChildren().add(gameCanvas);

        Scene gameScene = new Scene(gameLayout, CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
        setupKeyHandlers(gameScene);

        primaryStage.setScene(gameScene);

        // Start game loop
        startGameLoop();
        startMovementLoop();

        draw();
    }

    private void initializeGame(int numPlayers) {
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        gameEnded = false;
        showingFinalState = false;
        winner = null;

        // Initialize board with walls and destructible walls
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (x == 0 || x == BOARD_WIDTH - 1 || y == 0 || y == BOARD_HEIGHT - 1) {
                    board[y][x] = 1; // Border walls
                } else if (x % 2 == 0 && y % 2 == 0) {
                    board[y][x] = 1; // Fixed walls
                } else if (Math.random() < 0.3) {
                    board[y][x] = 2; // Destructible walls
                } else {
                    board[y][x] = 0; // Empty space
                }
            }
        }
        // Create players and position them in corners
        Color[] playerColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        KeyCode[][] playerKeys = {
                {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A}, // Player 1: ZQSD + A for bomb
                {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER}, // Player 2: Arrows + Enter
                {KeyCode.T, KeyCode.G, KeyCode.F, KeyCode.H, KeyCode.R}, // Player 3: TFGH + R
                {KeyCode.I, KeyCode.K, KeyCode.J, KeyCode.L, KeyCode.U}  // Player 4: IJKL + U
        };

        int[][] startPositions = {
                {1, 1}, // Top-left
                {BOARD_WIDTH - 2, 1}, // Top-right
                {1, BOARD_HEIGHT - 2}, // Bottom-left
                {BOARD_WIDTH - 2, BOARD_HEIGHT - 2} // Bottom-right
        };

        for (int i = 0; i < numPlayers; i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];

            // Clear starting area around player
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (x + dx >= 0 && x + dx < BOARD_WIDTH &&
                            y + dy >= 0 && y + dy < BOARD_HEIGHT) {
                        if (board[y + dy][x + dx] == 2) {
                            board[y + dy][x + dx] = 0;
                        }
                    }
                }
            }

            players.add(new Player(i + 1, x, y, playerColors[i], playerKeys[i]));
        }
    }

    private void checkWinCondition() {
        int alivePlayers = 0;
        Player currentWinner = null;

        for (Player player : players) {
            if (player.alive) {
                alivePlayers++;
                currentWinner = player;
            }
        }

        if (alivePlayers <= 1 && !gameEnded) {
            winner = currentWinner;
            showingFinalState = true; // Show final state first

            if (gameLoop != null) gameLoop.stop();
            if (movementLoop != null) movementLoop.stop();

            // Wait 3 seconds showing the final state, then show end screen
            Timeline endDelay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                gameEnded = true;
                showEndScreen(winner);
            }));
            endDelay.play();
        }
    }

    private void showEndScreen(Player winner) {
        VBox endLayout = new VBox(30);
        endLayout.setAlignment(Pos.CENTER);
        endLayout.setStyle("-fx-background-color: #2c3e50;");

        Text winText;
        if (winner != null) {
            winText = new Text("UN GAGNANT EST JOUEUR " + winner.id + " !");
            winText.setFill(winner.color);
        } else {
            winText = new Text("MATCH NUL !");
            winText.setFill(Color.WHITE);
        }
        winText.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        Button playAgainBtn = new Button("Rejouer");
        playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playAgainBtn.setPrefSize(150, 50);
        playAgainBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10;");
        playAgainBtn.setOnAction(e -> {
            gameEnded = false;
            showStartMenu();
        });

        Button quitBtn = new Button("Quitter");
        quitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        quitBtn.setPrefSize(150, 50);
        quitBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        quitBtn.setOnAction(e -> primaryStage.close());

        endLayout.getChildren().addAll(winText, playAgainBtn, quitBtn);

        Scene endScene = new Scene(endLayout, 800, 600);
        primaryStage.setScene(endScene);
    }

    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode().ordinal() < keysPressed.length) {
                keysPressed[e.getCode().ordinal()] = true;
            }

            // Handle bomb placement immediately (not in movement loop)
            for (Player player : players) {
                if (!player.alive || gameEnded || showingFinalState) continue;

                if (e.getCode() == player.bombKey) {
                    placeBomb(player);
                    break;
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode().ordinal() < keysPressed.length) {
                keysPressed[e.getCode().ordinal()] = false;
            }
        });

        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
    }

    // Replace the existing movePlayer method with this updated version:
    private void movePlayer(Player player, int dx, int dy) {
        // Only move if player is not currently moving
        if (!player.canMove()) {
            return;
        }

        int newX = player.x + dx;
        int newY = player.y + dy;

        if (newX >= 0 && newX < BOARD_WIDTH && newY >= 0 && newY < BOARD_HEIGHT) {
            // Check if there's a bomb at the target position
            boolean bombBlocking = false;
            for (Bomb bomb : bombs) {
                if (bomb.x == newX && bomb.y == newY) {
                    bombBlocking = true;
                    break;
                }
            }

            // Only allow movement if no bomb is blocking the path
            if (!bombBlocking) {
                if (board[newY][newX] == 0) { // Empty space
                    player.setTargetPosition(newX, newY);
                } else if (board[newY][newX] == 3) { // Range powerup
                    player.setTargetPosition(newX, newY);
                    player.bombRange++;
                    board[newY][newX] = 0; // Remove powerup
                } else if (board[newY][newX] == 4) { // Speed powerup (new!)
                    player.setTargetPosition(newX, newY);
                    player.speedPowerUps++;
                    board[newY][newX] = 0; // Remove powerup
                }
            }
        }
    }

    private void placeBomb(Player player) {
        // Check cooldown using the player's calculated cooldown
        long currentTime = System.currentTimeMillis();
        if (currentTime - player.lastBombTime < player.getBombCooldown()) {
            return;
        }

        // Check if there's already a bomb at this position
        for (Bomb bomb : bombs) {
            if (bomb.x == player.x && bomb.y == player.y) {
                return;
            }
        }

        bombs.add(new Bomb(player.x, player.y, player.bombRange, player.id));
        player.lastBombTime = currentTime;
    }


    private void startMovementLoop() {
        movementLoop = new Timeline(new KeyFrame(Duration.millis(60), e -> {
            if (gameEnded || showingFinalState) return;

            for (Player player : players) {
                if (!player.alive) continue;

                // Handle movement input (only one direction at a time for smooth movement)
                if (keysPressed[player.upKey.ordinal()]) {
                    movePlayer(player, 0, -1);
                } else if (keysPressed[player.downKey.ordinal()]) {
                    movePlayer(player, 0, 1);
                } else if (keysPressed[player.leftKey.ordinal()]) {
                    movePlayer(player, -1, 0);
                } else if (keysPressed[player.rightKey.ordinal()]) {
                    movePlayer(player, 1, 0);
                }
            }
        }));
        movementLoop.setCycleCount(Timeline.INDEFINITE);
        movementLoop.play();
    }

    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            if (!gameEnded && !showingFinalState) {
                updateGame();
                checkWinCondition();
            }

            // Update player smooth movement
            for (Player player : players) {
                player.updateMovement();
            }

            draw(); // Always draw, even when showing final state
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void updateGame() {
        // Update bombs
        Iterator<Bomb> bombIter = bombs.iterator();
        while (bombIter.hasNext()) {
            Bomb bomb = bombIter.next();
            bomb.timer--;
            if (bomb.timer <= 0) {
                explodeBomb(bomb);
                bombIter.remove();
            }
        }

        // Update explosions
        Iterator<Explosion> explIter = explosions.iterator();
        while (explIter.hasNext()) {
            Explosion explosion = explIter.next();
            explosion.timer--;
            if (explosion.timer <= 0) {
                explIter.remove();
            }
        }

        // Check for player deaths
        for (Player player : players) {
            if (player.alive) {
                for (Explosion explosion : explosions) {
                    // Use grid position (x, y) for collision detection, not render position
                    if (explosion.x == player.x && explosion.y == player.y) {
                        player.alive = false;
                        break;
                    }
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb) {
        // Center explosion
        explosions.add(new Explosion(bomb.x, bomb.y, 10));

        // Explosion in four directions
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            for (int i = 1; i <= bomb.range; i++) {
                int newX = bomb.x + dir[0] * i;
                int newY = bomb.y + dir[1] * i;

                if (newX < 0 || newX >= BOARD_WIDTH || newY < 0 || newY >= BOARD_HEIGHT) {
                    break; // Out of bounds
                }

                if (board[newY][newX] == 1) {
                    break; // Hit fixed wall
                }

                explosions.add(new Explosion(newX, newY, 10));

                if (board[newY][newX] == 2) {
                    // Destructible wall - destroy it and maybe create powerup
                    board[newY][newX] = 0;
                    if (Math.random() < 0.3) { // 30% chance for powerup
                        if (Math.random() < 0.5) {
                            board[newY][newX] = 3; // Range powerup
                        } else {
                            board[newY][newX] = 4; // Speed powerup
                        }
                    }
                    break; // Stop explosion here
                }
            }
        }
    }

    private void draw() {
        // Clear canvas
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw board
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                switch (board[y][x]) {
                    case 1: // Fixed wall
                        gc.setFill(Color.GRAY);
                        gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        break;
                    case 2: // Destructible wall
                        gc.setFill(Color.BROWN);
                        gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        break;
                    case 3: // Powerup
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        gc.setFill(Color.PURPLE);
                        gc.fillOval(cellX + 6, cellY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                        break;
                    case 4: // Speed powerup (add this case after case 3)
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        gc.setFill(Color.CYAN);
                        gc.fillOval(cellX + 6, cellY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                        // Draw a small "S" in the center
                        gc.setFill(Color.WHITE);
                        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                        gc.fillText("S", cellX + 12, cellY + 18);
                        break;
                    default: // Empty space
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        break;
                }

                // Draw grid lines
                gc.setStroke(Color.BLACK);
                gc.strokeRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw explosions
        for (Explosion explosion : explosions) {
            gc.setFill(Color.ORANGE);

            double scale = explosion.getScale(); // Get current scale (1.0 to 0.0)
            int baseSize = CELL_SIZE - 4;
            int scaledSize = (int)(baseSize * scale);
            int offset = (baseSize - scaledSize) / 2; // Center the shrinking square

            int explX = explosion.x * CELL_SIZE + 2 + offset;
            int explY = explosion.y * CELL_SIZE + 2 + offset;

            gc.fillRect(explX, explY, scaledSize, scaledSize);
        }

        // Draw bombs
        for (Bomb bomb : bombs) {
            gc.setFill(Color.BLACK);
            int bombX = bomb.x * CELL_SIZE + 5;
            int bombY = bomb.y * CELL_SIZE + 5;
            gc.fillOval(bombX, bombY, CELL_SIZE - 10, CELL_SIZE - 10);

            // Draw fuse
            gc.setFill(Color.RED);
            gc.fillOval(bombX + 8, bombY - 3, 4, 4);
        }

        // Draw players
        for (Player player : players) {
            if (player.alive) {
                gc.setFill(player.color);
                // Use renderX and renderY for smooth positioning
                int playerX = (int)(player.renderX * CELL_SIZE) + 3;
                int playerY = (int)(player.renderY * CELL_SIZE) + 3;
                gc.fillOval(playerX, playerY, CELL_SIZE - 6, CELL_SIZE - 6);

                // Draw player number
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                gc.fillText(String.valueOf(player.id), playerX + 8, playerY + 16);
            }
        }

        // Draw bomb range indicator for each player
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            gc.setFill(player.color);
            String info = "P" + player.id + " Range: " + player.bombRange +
                    " Speed: " + player.speedPowerUps +
                    " Cooldown: " + player.getBombCooldown() + "ms" +
                    (player.alive ? "" : " (DEAD)");
            gc.fillText(info, 10, CANVAS_HEIGHT + 30 + i * 20);
        }

        // Draw victory message when showing final state
        if (showingFinalState) {
            // Semi-transparent overlay
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

            // Victory message
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 32));
            if (winner != null) {
                gc.setFill(winner.color);
                String winMessage = "JOUEUR " + winner.id + " GAGNE !";
                gc.fillText(winMessage, CANVAS_WIDTH/2 - 120, CANVAS_HEIGHT/2);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillText("MATCH NUL !", CANVAS_WIDTH/2 - 80, CANVAS_HEIGHT/2);
            }
        }
    }

    private static class Player {
        int id;
        int x, y; // Grid position
        double renderX, renderY; // Smooth rendering position
        double targetX, targetY; // Target position for smooth movement
        Color color;
        KeyCode upKey, downKey, leftKey, rightKey, bombKey;
        int bombRange;
        boolean alive;
        long lastBombTime;
        int speedPowerUps;
        boolean isMoving;
        private static final double MOVE_SPEED = 0.8; // Movement interpolation speed

        public Player(int id, int x, int y, Color color, KeyCode[] keys) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.renderX = x;
            this.renderY = y;
            this.targetX = x;
            this.targetY = y;
            this.color = color;
            this.upKey = keys[0];
            this.downKey = keys[1];
            this.leftKey = keys[2];
            this.rightKey = keys[3];
            this.bombKey = keys[4];
            this.bombRange = 1;
            this.alive = true;
            this.lastBombTime = 0;
            this.speedPowerUps = 0;
            this.isMoving = false;
        }

        public void updateMovement() {
            if (isMoving) {
                // Interpolate towards target position
                double dx = targetX - renderX;
                double dy = targetY - renderY;

                if (Math.abs(dx) < 0.01 && Math.abs(dy) < 0.01) {
                    // Close enough, snap to target
                    renderX = targetX;
                    renderY = targetY;
                    isMoving = false;
                } else {
                    // Move towards target
                    renderX += dx * MOVE_SPEED;
                    renderY += dy * MOVE_SPEED;
                }
            }
        }

        public void setTargetPosition(int newX, int newY) {
            this.x = newX;
            this.y = newY;
            this.targetX = newX;
            this.targetY = newY;
            this.isMoving = true;
        }

        public boolean canMove() {
            return !isMoving; // Only allow movement when not currently moving
        }

        public long getBombCooldown() {
            // Base cooldown is 800ms, reduced by 100ms per speed power-up (minimum 200ms)
            return Math.max(200, 800 - (speedPowerUps * 100));
        }
    }

    private static class Bomb {
        int x, y;
        int range;
        int timer;
        int playerId;

        public Bomb(int x, int y, int range, int playerId) {
            this.x = x;
            this.y = y;
            this.range = range;
            this.playerId = playerId;
            this.timer = 60; // 3 seconds at 20 FPS (50ms intervals)
        }
    }

    private static class Explosion {
        int x, y;
        int timer;
        int maxTimer; // Store the initial timer value
        private static final int WAIT_TIME = 3; // Wait 3 frames before starting to shrink

        public Explosion(int x, int y, int timer) {
            this.x = x;
            this.y = y;
            this.timer = timer;
            this.maxTimer = timer; // Remember the starting timer
        }

        // Get the current scale (1.0 = full size, 0.0 = disappeared)
        public double getScale() {
            if (timer > maxTimer - WAIT_TIME) {
                // Still in waiting period - stay at full size
                return 1.0;
            } else {
                // Start shrinking after wait period
                double remainingTime = timer;
                double shrinkTime = maxTimer - WAIT_TIME;
                return Math.max(0.0, remainingTime / shrinkTime);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}