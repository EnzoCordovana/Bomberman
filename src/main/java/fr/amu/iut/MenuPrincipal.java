package fr.amu.iut;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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

import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class MenuPrincipal extends Application {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_WIDTH = 21;
    private static final int BOARD_HEIGHT = 15;
    private static final int CANVAS_WIDTH = BOARD_WIDTH * CELL_SIZE;
    private static final int CANVAS_HEIGHT = BOARD_HEIGHT * CELL_SIZE;

    private Stage primaryStage;
    private List<Player> players;
    private int[][] board; // 0 = empty, 1 = wall, 2 = destructible wall, 3 = range powerup, 4 = bomb speed powerup, 5 = movement speed powerup
    private List<Bomb> bombs;
    private static List<Explosion> explosions;
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Timeline gameLoop;
    private Timeline movementLoop;
    private Timeline aiLoop;
    private boolean[] keysPressed = new boolean[256];
    private boolean gameEnded = false;
    private boolean showingFinalState = false;
    private Player winner = null;
    private int selectedPlayerCount = 2;
    private AIPlayer.Difficulty selectedAIDifficulty = AIPlayer.Difficulty.EASY;
    private boolean isVsAI = false;
    private boolean isCaptureTheFlagMode = false;
    protected List<Flag> flags;

    private Map<String, Image> spriteMap = new HashMap<>();
    private Image[] playerSprites = new Image[4];
    private Image[] bombFrames;
    private Image[] explosionFrames;
    private Image groundTile, indestructibleWall, destructibleWall;
    private Image rangePowerup, bombSpeedPowerup, moveSpeedPowerup;
    private Image[] flagSprites = new Image[4];

    private void debugResources() {
        System.out.println("=== DEBUGGING RESOURCES ===");

        // Check if images directory exists
        URL imagesDir = getClass().getResource("/images");
        if (imagesDir == null) {
            System.err.println("ERROR: /images directory not found on classpath!");

            // Try without leading slash
            imagesDir = getClass().getResource("images");
            if (imagesDir != null) {
                System.out.println("Found images directory at: " + imagesDir);
                System.out.println("Try using paths without leading slash: 'images/player_red.png'");
            }
        } else {
            System.out.println("Images directory found at: " + imagesDir);
        }

        // Check class location
        URL classLocation = getClass().getResource("");
        System.out.println("Class is located at: " + classLocation);

        // List what's in the root
        URL root = getClass().getResource("/");
        System.out.println("Root classpath: " + root);

        System.out.println("=== END DEBUG ===");
    }

    private void loadSprites() {
        try {
            // Player sprites
            for (int i = 0; i < 4; i++) {
                String color = switch(i) {
                    case 0 -> "red";
                    case 1 -> "blue";
                    case 2 -> "green";
                    case 3 -> "yellow";
                    default -> "red";
                };
                String playerPath = "/images/player_" + color + ".png";
                System.out.println("Loading player sprite: " + playerPath);
                InputStream stream = getClass().getResourceAsStream(playerPath);
                if (stream == null) {
                    System.err.println("ERROR: Could not find resource: " + playerPath);
                    continue;
                }
                playerSprites[i] = new Image(stream);
            }

            // Environment sprites
            String[] spritePaths = {
                    "/images/ground.png",
                    "/images/wall_indestructible.png",
                    "/images/wall_destructible.png",
                    "/images/powerup_range.png",
                    "/images/powerup_bomb_speed.png",
                    "/images/powerup_move_speed.png",
                    "/images/bomb1.png",              // Index 6
                    "/images/explosion1.png",         // Index 7
                    "/images/explosion2.png",         // Index 8
                    "/images/explosion3.png",         // Index 9
                    "/images/explosion4.png",         // Index 10
                    "/images/explosion5.png",         // Index 11
                    "/images/explosion6.png",         // Index 12
                    "/images/explosion7.png",         // Index 13
                    "/images/bomb1.png",              // Index 14
                    "/images/bomb2.png",              // Index 15
                    "/images/bomb3.png",              // Index 16
                    "/images/bomb4.png",              // Index 17
                    "/images/bomb5.png",              // Index 18
                    "/images/bomb6.png"               // Index 19
            };

            // Load basic environment sprites
            System.out.println("Loading ground tile...");
            InputStream groundStream = getClass().getResourceAsStream(spritePaths[0]);
            if (groundStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[0]);
            } else {
                groundTile = new Image(groundStream);
            }

            System.out.println("Loading indestructible wall...");
            InputStream wallStream = getClass().getResourceAsStream(spritePaths[1]);
            if (wallStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[1]);
            } else {
                indestructibleWall = new Image(wallStream);
            }

            System.out.println("Loading destructible wall...");
            InputStream destWallStream = getClass().getResourceAsStream(spritePaths[2]);
            if (destWallStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[2]);
            } else {
                destructibleWall = new Image(destWallStream);
            }

            // Power-ups
            System.out.println("Loading range powerup...");
            InputStream rangePowerupStream = getClass().getResourceAsStream(spritePaths[3]);
            if (rangePowerupStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[3]);
            } else {
                rangePowerup = new Image(rangePowerupStream);
            }

            System.out.println("Loading bomb speed powerup...");
            InputStream bombSpeedPowerupStream = getClass().getResourceAsStream(spritePaths[4]);
            if (bombSpeedPowerupStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[4]);
            } else {
                bombSpeedPowerup = new Image(bombSpeedPowerupStream);
            }

            System.out.println("Loading move speed powerup...");
            InputStream moveSpeedPowerupStream = getClass().getResourceAsStream(spritePaths[5]);
            if (moveSpeedPowerupStream == null) {
                System.err.println("ERROR: Could not find resource: " + spritePaths[5]);
            } else {
                moveSpeedPowerup = new Image(moveSpeedPowerupStream);
            }

            // Explosion frames
            explosionFrames = new Image[7];
            for (int i = 0; i < 7; i++) {
                int spriteIndex = i + 7; // spritePaths indices 7-13 for explosion1-7
                System.out.println("Loading explosion frame " + (i + 1) + ": " + spritePaths[spriteIndex]);
                InputStream explosionStream = getClass().getResourceAsStream(spritePaths[spriteIndex]);
                if (explosionStream == null) {
                    System.err.println("ERROR: Could not find resource: " + spritePaths[spriteIndex]);
                } else {
                    explosionFrames[i] = new Image(explosionStream);
                }
            }

            // Bomb animation frames
            bombFrames = new Image[7];
            for (int i = 0; i < 6; i++) {
                int spriteIndex = i + 14; // spritePaths indices 14-19 for bomb1-6
                System.out.println("Loading bomb frame " + (i+1) + ": " + spritePaths[spriteIndex]);
                InputStream bombFrameStream = getClass().getResourceAsStream(spritePaths[spriteIndex]);
                if (bombFrameStream == null) {
                    System.err.println("ERROR: Could not find resource: " + spritePaths[spriteIndex]);
                } else {
                    bombFrames[i] = new Image(bombFrameStream);
                }
            }

            // Flags
            for (int i = 0; i < 4; i++) {
                String color = switch(i) {
                    case 0 -> "red";
                    case 1 -> "blue";
                    case 2 -> "green";
                    case 3 -> "yellow";
                    default -> "red";
                };
                String flagPath = "/images/flag_" + color + ".png";
                System.out.println("Loading flag sprite: " + flagPath);
                InputStream flagStream = getClass().getResourceAsStream(flagPath);
                if (flagStream == null) {
                    System.err.println("ERROR: Could not find resource: " + flagPath);
                } else {
                    flagSprites[i] = new Image(flagStream);
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        debugResources();
        loadSprites();
        primaryStage.setTitle("Bomberman");
        primaryStage.setResizable(false);

        showStartMenu();
        primaryStage.show();
    }

    private static class Flag {
        int x, y;
        int ownerId; // The player who owns this flag
        Color color;
        boolean captured;

        public Flag(int x, int y, int ownerId, Color color) {
            this.x = x;
            this.y = y;
            this.ownerId = ownerId;
            this.color = color;
            this.captured = false;
        }
    }

    private static class AIPlayer extends Player {
        enum Difficulty { EASY, MEDIUM, HARD }
        enum AIState { EXPLORING, ESCAPING, ATTACKING, SEEKING_POWERUP, COMMITTED_PATH }

        protected Difficulty difficulty; // Changed from private to protected
        protected long lastDecisionTime = 0; // Changed from private to protected
        protected int currentDirection = -1;
        protected long directionChangeTime = 0;
        protected AIState currentState = AIState.EXPLORING;
        protected long stateChangeTime = 0;

        // Anti-oscillation system
        protected List<int[]> recentPositions = new ArrayList<>(); // Changed to protected
        protected long lastMoveTime = 0;
        protected int stuckCounter = 0;
        protected boolean isCommittedToPath = false;
        protected List<int[]> committedPath = new ArrayList<>();
        protected int pathIndex = 0;
        protected long commitmentStartTime = 0;
        protected final int MAX_COMMITMENT_TIME = 3000;

        // Movement tracking
        protected int consecutiveBlocked = 0;
        protected int[] lastAttemptedMove = {0, 0};
        protected long lastBlockedTime = 0;

        public AIPlayer(int id, int x, int y, Color color, Difficulty difficulty) {
            super(id, x, y, color, new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER});
            this.difficulty = difficulty;
            recentPositions.add(new int[]{x, y});
            lastMoveTime = System.currentTimeMillis();
        }

        protected void updatePositionHistory() { // Changed from private to protected
            long currentTime = System.currentTimeMillis();
            int[] currentPos = {this.x, this.y};

            if (currentTime - lastMoveTime > 300) {
                recentPositions.add(currentPos);
                lastMoveTime = currentTime;

                if (recentPositions.size() > 8) {
                    recentPositions.remove(0);
                }
            }
        }

        // Add this method for CTF AI to use
        protected Flag getNearestEnemyFlag(List<Flag> flags) {
            Flag nearestFlag = null;
            int minDistance = Integer.MAX_VALUE;

            for (Flag flag : flags) {
                if (flag.ownerId != this.id && !flag.captured) {
                    int distance = Math.abs(flag.x - this.x) + Math.abs(flag.y - this.y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestFlag = flag;
                    }
                }
            }

            return nearestFlag;
        }

        protected boolean isOscillating() { // Changed from private to protected
            if (recentPositions.size() < 4) return false;

            Set<String> uniquePositions = new HashSet<>();
            for (int[] pos : recentPositions) {
                uniquePositions.add(pos[0] + "," + pos[1]);
            }

            return uniquePositions.size() <= 3 && recentPositions.size() >= 6;
        }

        protected void generateCommittedPath(int[][] board, List<Bomb> bombs, List<Explosion> explosions) { // Changed to protected
            committedPath.clear();
            pathIndex = 0;

            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            int chosenDirection = -1;
            int maxOpenSteps = 0;

            for (int i = 0; i < directions.length; i++) {
                int[] dir = directions[i];
                int steps = 0;

                for (int step = 1; step <= 5; step++) {
                    int checkX = this.x + dir[0] * step;
                    int checkY = this.y + dir[1] * step;

                    if (isValidMove(checkX, checkY, board, bombs) &&
                            isSafePosition(checkX, checkY, board, bombs, explosions)) {
                        steps++;
                    } else {
                        break;
                    }
                }

                if (steps > maxOpenSteps) {
                    maxOpenSteps = steps;
                    chosenDirection = i;
                }
            }

            if (chosenDirection >= 0 && maxOpenSteps > 0) {
                int[] dir = directions[chosenDirection];
                for (int step = 1; step <= Math.min(maxOpenSteps, 4); step++) {
                    int nextX = this.x + dir[0] * step;
                    int nextY = this.y + dir[1] * step;
                    committedPath.add(new int[]{nextX, nextY});
                }
            }

            if (committedPath.isEmpty()) {
                for (int attempt = 0; attempt < 10; attempt++) {
                    int randomDir = (int)(Math.random() * 4);
                    int[] dir = directions[randomDir];

                    boolean canCreatePath = true;
                    List<int[]> tempPath = new ArrayList<>();

                    for (int step = 1; step <= 2; step++) {
                        int nextX = this.x + dir[0] * step;
                        int nextY = this.y + dir[1] * step;

                        if (isValidMove(nextX, nextY, board, bombs)) {
                            tempPath.add(new int[]{nextX, nextY});
                        } else {
                            canCreatePath = false;
                            break;
                        }
                    }

                    if (canCreatePath && !tempPath.isEmpty()) {
                        committedPath.addAll(tempPath);
                        break;
                    }
                }
            }
        }

        protected boolean followCommittedPath(int[][] board, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) { // Changed to protected
            if (committedPath.isEmpty() || pathIndex >= committedPath.size()) {
                return false;
            }

            long currentTime = System.currentTimeMillis();

            if (currentTime - commitmentStartTime > MAX_COMMITMENT_TIME) {
                return false;
            }

            int[] targetPos = committedPath.get(pathIndex);
            int targetX = targetPos[0];
            int targetY = targetPos[1];

            int dx = Integer.compare(targetX, this.x);
            int dy = Integer.compare(targetY, this.y);

            if (!isValidMove(targetX, targetY, board, bombs)) {
                return false;
            }

            if (currentState != AIState.ESCAPING && !isSafePosition(targetX, targetY, board, bombs, explosions)) {
                return false;
            }

            if (dx != 0 || dy != 0) {
                game.movePlayer(this, dx, dy);

                if (this.x == targetX && this.y == targetY) {
                    pathIndex++;
                }

                return true;
            }

            return false;
        }

        public void makeDecision(int[][] board, List<Player> players, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            long currentTime = System.currentTimeMillis();

            long decisionDelay = switch (difficulty) {
                case EASY -> 400;
                case MEDIUM -> 250;
                case HARD -> 150;
            };

            if (currentTime - lastDecisionTime < decisionDelay) {
                return;
            }

            lastDecisionTime = currentTime;
            updatePositionHistory();

            if (isInDanger(board, bombs, explosions)) {
                currentState = AIState.ESCAPING;
                isCommittedToPath = false;
                moveToSafety(board, bombs, explosions, game);
                return;
            }

            if (isCommittedToPath && currentState == AIState.COMMITTED_PATH) {
                if (followCommittedPath(board, bombs, explosions, game)) {
                    return;
                } else {
                    isCommittedToPath = false;
                    currentState = AIState.EXPLORING;
                }
            }

            if (isOscillating() && !isCommittedToPath) {
                generateCommittedPath(board, bombs, explosions);
                if (!committedPath.isEmpty()) {
                    isCommittedToPath = true;
                    currentState = AIState.COMMITTED_PATH;
                    commitmentStartTime = currentTime;
                    pathIndex = 0;

                    if (followCommittedPath(board, bombs, explosions, game)) {
                        return;
                    }
                }
            }

            if (hasAttackOpportunity(board, players, bombs) &&
                    canEscapeAfterBomb(this.x, this.y, this.bombRange, board, bombs)) {
                game.placeBomb(this);
                currentState = AIState.ESCAPING;
                return;
            }

            if (shouldDestroyWalls(board, bombs)) {
                game.placeBomb(this);
                currentState = AIState.ESCAPING;
                return;
            }

            makeStrategicMove(board, players, bombs, explosions, game);
        }

        private void makeStrategicMove(int[][] board, List<Player> players, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            int[] powerupMove = findMoveTowardsPowerup(board, bombs, explosions, 3);
            if (powerupMove != null) {
                game.movePlayer(this, powerupMove[0], powerupMove[1]);
                return;
            }

            int[] playerMove = findMoveTowardsNearestPlayer(board, players, bombs, explosions);
            if (playerMove != null) {
                game.movePlayer(this, playerMove[0], playerMove[1]);
                return;
            }

            powerupMove = findMoveTowardsPowerup(board, bombs, explosions, Integer.MAX_VALUE);
            if (powerupMove != null) {
                game.movePlayer(this, powerupMove[0], powerupMove[1]);
                return;
            }

            makeMovementDecision(board, players, bombs, game);
        }

        private int[] findMoveTowardsNearestPlayer(int[][] board, List<Player> players, List<Bomb> bombs, List<Explosion> explosions) {
            Player nearestPlayer = null;
            int minDistance = Integer.MAX_VALUE;

            for (Player player : players) {
                if (player.id != this.id && player.alive && !(player instanceof AIPlayer)) {
                    int distance = Math.abs(player.x - this.x) + Math.abs(player.y - this.y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestPlayer = player;
                    }
                }
            }

            if (nearestPlayer == null) return null;

            int safeDistance = switch (difficulty) {
                case EASY -> 3;
                case MEDIUM -> 2;
                case HARD -> 2;
            };

            if (minDistance <= safeDistance) {
                if (hasAttackOpportunity(board, players, bombs)) {
                    return null;
                }
                return findParallelMove(board, nearestPlayer, bombs, explosions);
            }

            int targetX = nearestPlayer.x;
            int targetY = nearestPlayer.y;

            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            int bestDir = -1;
            int bestDistance = Integer.MAX_VALUE;

            for (int i = 0; i < directions.length; i++) {
                int[] dir = directions[i];
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (isValidMove(newX, newY, board, bombs) &&
                        isSafePosition(newX, newY, board, bombs, explosions)) {

                    int distance = Math.abs(newX - targetX) + Math.abs(newY - targetY);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestDir = i;
                    }
                }
            }

            return bestDir >= 0 ? directions[bestDir] : null;
        }

        private int[] findParallelMove(int[][] board, Player targetPlayer, List<Bomb> bombs, List<Explosion> explosions) {
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            for (int[] dir : directions) {
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (isValidMove(newX, newY, board, bombs) &&
                        isSafePosition(newX, newY, board, bombs, explosions)) {

                    int currentDistance = Math.abs(this.x - targetPlayer.x) + Math.abs(this.y - targetPlayer.y);
                    int newDistance = Math.abs(newX - targetPlayer.x) + Math.abs(newY - targetPlayer.y);

                    if (Math.abs(newDistance - currentDistance) <= 1) {
                        return dir;
                    }
                }
            }

            return null;
        }

        private int[] findMoveTowardsPowerup(int[][] board, List<Bomb> bombs, List<Explosion> explosions, int maxDistance) {
            int nearestX = -1, nearestY = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[0].length; x++) {
                    if (board[y][x] >= 3 && board[y][x] <= 5) {
                        int distance = Math.abs(x - this.x) + Math.abs(y - this.y);
                        if (distance < minDistance && distance <= maxDistance) {
                            minDistance = distance;
                            nearestX = x;
                            nearestY = y;
                        }
                    }
                }
            }

            if (nearestX == -1) return null;

            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            int bestDir = -1;
            int bestDistance = Integer.MAX_VALUE;

            for (int i = 0; i < directions.length; i++) {
                int[] dir = directions[i];
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (isValidMove(newX, newY, board, bombs) &&
                        isSafePosition(newX, newY, board, bombs, explosions)) {

                    int distance = Math.abs(newX - nearestX) + Math.abs(newY - nearestY);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestDir = i;
                    }
                }
            }

            return bestDir >= 0 ? directions[bestDir] : null;
        }

        private void makeMovementDecision(int[][] board, List<Player> players, List<Bomb> bombs, MenuPrincipal game) {
            long currentTime = System.currentTimeMillis();
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            if (consecutiveBlocked > 2) {
                List<Integer> availableDirections = new ArrayList<>();
                for (int i = 0; i < directions.length; i++) {
                    int[] dir = directions[i];
                    int newX = this.x + dir[0];
                    int newY = this.y + dir[1];
                    if (isValidMove(newX, newY, board, bombs)) {
                        availableDirections.add(i);
                    }
                }

                if (!availableDirections.isEmpty()) {
                    int randomIndex = (int)(Math.random() * availableDirections.size());
                    currentDirection = availableDirections.get(randomIndex);
                    consecutiveBlocked = 0;
                    directionChangeTime = currentTime;
                }
            }

            long directionHoldTime = switch (difficulty) {
                case EASY -> 2000;
                case MEDIUM -> 1500;
                case HARD -> 1000;
            };

            if (currentTime - directionChangeTime > directionHoldTime || currentDirection == -1) {
                int bestDirection = -1;
                int maxOpenSpace = 0;

                for (int i = 0; i < directions.length; i++) {
                    int openSpace = countOpenSpaceInDirection(this.x, this.y, directions[i], board, bombs);
                    if (openSpace > maxOpenSpace) {
                        maxOpenSpace = openSpace;
                        bestDirection = i;
                    }
                }

                if (bestDirection >= 0) {
                    currentDirection = bestDirection;
                } else {
                    currentDirection = (int)(Math.random() * 4);
                }

                directionChangeTime = currentTime;
            }

            if (currentDirection >= 0) {
                int[] preferredDir = directions[currentDirection];
                int newX = this.x + preferredDir[0];
                int newY = this.y + preferredDir[1];

                if (isValidMove(newX, newY, board, bombs)) {
                    game.movePlayer(this, preferredDir[0], preferredDir[1]);
                    consecutiveBlocked = 0;
                    lastAttemptedMove[0] = preferredDir[0];
                    lastAttemptedMove[1] = preferredDir[1];
                    return;
                } else {
                    consecutiveBlocked++;
                    lastBlockedTime = currentTime;
                }
            }

            for (int i = 0; i < directions.length; i++) {
                if (i == currentDirection) continue;

                int[] dir = directions[i];
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (isValidMove(newX, newY, board, bombs)) {
                    game.movePlayer(this, dir[0], dir[1]);
                    currentDirection = i;
                    directionChangeTime = currentTime;
                    consecutiveBlocked = 0;
                    lastAttemptedMove[0] = dir[0];
                    lastAttemptedMove[1] = dir[1];
                    return;
                }
            }

            consecutiveBlocked++;
        }

        private int countOpenSpaceInDirection(int startX, int startY, int[] direction, int[][] board, List<Bomb> bombs) {
            int count = 0;
            for (int step = 1; step <= 4; step++) {
                int checkX = startX + direction[0] * step;
                int checkY = startY + direction[1] * step;

                if (isValidMove(checkX, checkY, board, bombs)) {
                    count++;
                } else {
                    break;
                }
            }
            return count;
        }

        protected boolean hasAttackOpportunity(int[][] board, List<Player> players, List<Bomb> bombs) { // Changed to protected
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastBombTime < this.getBombCooldown()) {
                return false;
            }

            for (Bomb bomb : bombs) {
                if (bomb.x == this.x && bomb.y == this.y) {
                    return false;
                }
            }

            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] dir : directions) {
                for (int i = 1; i <= this.bombRange; i++) {
                    int checkX = this.x + dir[0] * i;
                    int checkY = this.y + dir[1] * i;

                    if (checkX < 0 || checkX >= board[0].length || checkY < 0 || checkY >= board.length) {
                        break;
                    }

                    if (board[checkY][checkX] == 1) {
                        break;
                    }

                    for (Player player : players) {
                        if (player.id != this.id && player.alive && player.x == checkX && player.y == checkY) {
                            return true;
                        }
                    }

                    if (board[checkY][checkX] == 2) {
                        break;
                    }
                }
            }

            return false;
        }

        protected boolean shouldDestroyWalls(int[][] board, List<Bomb> bombs) { // Changed to protected
            if (difficulty == Difficulty.EASY && Math.random() > 0.3) {
                return false;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastBombTime < this.getBombCooldown()) {
                return false;
            }

            for (Bomb bomb : bombs) {
                if (bomb.x == this.x && bomb.y == this.y) {
                    return false;
                }
            }

            int wallsHit = 0;
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] dir : directions) {
                for (int i = 1; i <= this.bombRange; i++) {
                    int checkX = this.x + dir[0] * i;
                    int checkY = this.y + dir[1] * i;

                    if (checkX < 0 || checkX >= board[0].length || checkY < 0 || checkY >= board.length) {
                        break;
                    }

                    if (board[checkY][checkX] == 1) {
                        break;
                    }

                    if (board[checkY][checkX] == 2) {
                        wallsHit++;
                        break;
                    }
                }
            }

            return wallsHit >= 1;
        }

        protected boolean isInDanger(int[][] board, List<Bomb> bombs, List<Explosion> explosions) { // Changed to protected
            for (Explosion explosion : explosions) {
                if (explosion.x == this.x && explosion.y == this.y) {
                    return true;
                }
            }

            for (Bomb bomb : bombs) {
                if (bomb.timer <= 20) {
                    if (isInExplosionRange(bomb.x, bomb.y, bomb.range, this.x, this.y, board)) {
                        return true;
                    }
                }
            }

            return false;
        }

        protected boolean isInExplosionRange(int bombX, int bombY, int range, int targetX, int targetY, int[][] board) { // Changed to protected
            if (bombX == targetX && bombY == targetY) return true;

            if (bombY == targetY) {
                int distance = Math.abs(bombX - targetX);
                if (distance <= range) {
                    int minX = Math.min(bombX, targetX);
                    int maxX = Math.max(bombX, targetX);
                    for (int x = minX + 1; x < maxX; x++) {
                        if (board[bombY][x] == 1 || board[bombY][x] == 2) {
                            return false;
                        }
                    }
                    return true;
                }
            }

            if (bombX == targetX) {
                int distance = Math.abs(bombY - targetY);
                if (distance <= range) {
                    int minY = Math.min(bombY, targetY);
                    int maxY = Math.max(bombY, targetY);
                    for (int y = minY + 1; y < maxY; y++) {
                        if (board[y][bombX] == 1 || board[y][bombX] == 2) {
                            return false;
                        }
                    }
                    return true;
                }
            }

            return false;
        }

        protected void moveToSafety(int[][] board, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) { // Changed to protected
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            for (int[] dir : directions) {
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (isValidMove(newX, newY, board, bombs) &&
                        isSafePosition(newX, newY, board, bombs, explosions) &&
                        hasEscapeRoute(newX, newY, board, bombs)) {

                    game.movePlayer(this, dir[0], dir[1]);
                    return;
                }
            }

            for (int[] dir : directions) {
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];
                if (isValidMove(newX, newY, board, bombs)) {
                    game.movePlayer(this, dir[0], dir[1]);
                    return;
                }
            }
        }

        protected boolean canEscapeAfterBomb(int bombX, int bombY, int bombRange, int[][] board, List<Bomb> bombs) { // Changed to protected
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            for (int[] dir : directions) {
                for (int steps = 1; steps <= 3; steps++) {
                    int escapeX = bombX + dir[0] * steps;
                    int escapeY = bombY + dir[1] * steps;

                    if (isValidMove(escapeX, escapeY, board, bombs) &&
                            !isInExplosionRange(bombX, bombY, bombRange, escapeX, escapeY, board)) {
                        return true;
                    }

                    if (!isValidMove(escapeX, escapeY, board, bombs)) {
                        break;
                    }
                }
            }

            return false;
        }

        protected boolean hasEscapeRoute(int x, int y, int[][] board, List<Bomb> bombs) { // Changed to protected
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            for (int[] dir : directions) {
                int escapeX = x + dir[0];
                int escapeY = y + dir[1];

                if (isValidMove(escapeX, escapeY, board, bombs)) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isValidMove(int x, int y, int[][] board, List<Bomb> bombs) { // Changed to protected
            if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
                return false;
            }

            if (board[y][x] == 1 || board[y][x] == 2) {
                return false;
            }

            for (Bomb bomb : bombs) {
                if (bomb.x == x && bomb.y == y) {
                    return false;
                }
            }

            return true;
        }

        protected boolean isSafePosition(int x, int y, int[][] board, List<Bomb> bombs, List<Explosion> explosions) { // Changed to protected
            for (Bomb bomb : bombs) {
                if (isInExplosionRange(bomb.x, bomb.y, bomb.range, x, y, board)) {
                    return false;
                }
            }

            for (Explosion explosion : explosions) {
                if (explosion.x == x && explosion.y == y) {
                    return false;
                }
            }

            return true;
        }
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
        playButton.setOnAction(e -> showGameModeMenu());

        menuLayout.getChildren().addAll(title, playButton);

        Scene startScene = new Scene(menuLayout, 800, 600);
        primaryStage.setScene(startScene);
    }

    private void showGameModeMenu() {
        VBox gameModeLayout = new VBox(30);
        gameModeLayout.setAlignment(Pos.CENTER);
        gameModeLayout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("Mode de Jeu");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setFill(Color.WHITE);

        Button playerVsPlayerBtn = new Button("Contre Joueur");
        playerVsPlayerBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        playerVsPlayerBtn.setPrefSize(200, 60);
        playerVsPlayerBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 10;");
        playerVsPlayerBtn.setOnAction(e -> showPlayerSelection());

        Button playerVsAIBtn = new Button("Contre Machine");
        playerVsAIBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        playerVsAIBtn.setPrefSize(200, 60);
        playerVsAIBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 10;");
        // ✅ CORRECT - should call showAIPlayerSelection()
        playerVsAIBtn.setOnAction(e -> showAIPlayerSelection());

        Button backBtn = new Button("Retour");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        backBtn.setPrefSize(200, 60);
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> showStartMenu());

        gameModeLayout.getChildren().addAll(title, playerVsPlayerBtn, playerVsAIBtn, backBtn);

        Scene gameModeScene = new Scene(gameModeLayout, 800, 600);
        primaryStage.setScene(gameModeScene);
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

        // Updated to show game mode selection instead of starting game directly
        twoPlayersBtn.setOnAction(e -> {
            selectedPlayerCount = 2;
            isVsAI = false;
            showGameModeSelection();
        });
        threePlayersBtn.setOnAction(e -> {
            selectedPlayerCount = 3;
            isVsAI = false;
            showGameModeSelection();
        });
        fourPlayersBtn.setOnAction(e -> {
            selectedPlayerCount = 4;
            isVsAI = false;
            showGameModeSelection();
        });

        Button backBtn = new Button("Retour");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        backBtn.setPrefSize(200, 60);
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> showGameModeMenu());

        selectionLayout.getChildren().addAll(title, twoPlayersBtn, threePlayersBtn, fourPlayersBtn, backBtn);

        Scene selectionScene = new Scene(selectionLayout, 800, 600);
        primaryStage.setScene(selectionScene);
    }

    private void showAIPlayerSelection() {
        VBox selectionLayout = new VBox(20);
        selectionLayout.setAlignment(Pos.CENTER);
        selectionLayout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("Difficulté de l'IA");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);

        Button easyBtn = new Button("Facile");
        Button mediumBtn = new Button("Moyen");
        Button hardBtn = new Button("Difficile");

        for (Button btn : new Button[]{easyBtn, mediumBtn, hardBtn}) {
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            btn.setPrefSize(200, 60);
            btn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 10;");
        }

        easyBtn.setOnAction(e -> {
            selectedAIDifficulty = AIPlayer.Difficulty.EASY;
            isVsAI = true;
            showGameModeSelection();
        });
        mediumBtn.setOnAction(e -> {
            selectedAIDifficulty = AIPlayer.Difficulty.MEDIUM;
            isVsAI = true;
            showGameModeSelection();
        });
        hardBtn.setOnAction(e -> {
            selectedAIDifficulty = AIPlayer.Difficulty.HARD;
            isVsAI = true;
            showGameModeSelection();
        });

        Button backBtn = new Button("Retour");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        backBtn.setPrefSize(200, 60);
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> showGameModeMenu());

        selectionLayout.getChildren().addAll(title, easyBtn, mediumBtn, hardBtn, backBtn);

        Scene selectionScene = new Scene(selectionLayout, 800, 600);
        primaryStage.setScene(selectionScene);
    }

    private void showGameModeSelection() {
        VBox gameModeLayout = new VBox(30);
        gameModeLayout.setAlignment(Pos.CENTER);
        gameModeLayout.setStyle("-fx-background-color: #2c3e50;");

        Text title = new Text("Mode de jeu");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setFill(Color.WHITE);

        Button classiqueBtn = new Button("Classique");
        classiqueBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        classiqueBtn.setPrefSize(200, 60);
        classiqueBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10;");
        classiqueBtn.setOnAction(e -> startSelectedGame("classique"));

        Button ctfBtn = new Button("Capture the Flag");
        ctfBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ctfBtn.setPrefSize(200, 60);
        ctfBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 10;");
        ctfBtn.setOnAction(e -> startSelectedGame("ctf"));

        Button backBtn = new Button("Retour");
        backBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        backBtn.setPrefSize(200, 60);
        backBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> {
            // Go back to the appropriate previous menu
            if (isVsAI) {
                showAIPlayerSelection();
            } else {
                showPlayerSelection();
            }
        });

        gameModeLayout.getChildren().addAll(title, classiqueBtn, ctfBtn, backBtn);

        Scene gameModeScene = new Scene(gameModeLayout, 800, 600);
        primaryStage.setScene(gameModeScene);
    }

    private void startSelectedGame(String gameMode) {
        if (gameMode.equals("classique")) {
            // Start classic mode
            if (isVsAI) {
                startGameVsAI(selectedAIDifficulty);
            } else {
                startGame(selectedPlayerCount);
            }
        } else if (gameMode.equals("ctf")) {
            // Start Capture the Flag mode
            if (isVsAI) {
                startCaptureTheFlagVsAI(selectedAIDifficulty);
            } else {
                startCaptureTheFlag(selectedPlayerCount);
            }
        }
    }

    private void startCaptureTheFlag(int numPlayers) {
        isCaptureTheFlagMode = true;
        initializeCTFGame(numPlayers);

        StackPane gameLayout = new StackPane();
        gameLayout.setStyle("-fx-background-color: #34495e;");

        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        gameLayout.getChildren().add(gameCanvas);

        Scene gameScene = new Scene(gameLayout, CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
        setupKeyHandlers(gameScene);

        primaryStage.setScene(gameScene);

        startGameLoop();
        startMovementLoop();

        draw();
    }

    private void startCaptureTheFlagVsAI(AIPlayer.Difficulty difficulty) {
        isCaptureTheFlagMode = true;
        initializeCTFGameVsAI(difficulty);

        StackPane gameLayout = new StackPane();
        gameLayout.setStyle("-fx-background-color: #34495e;");

        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        gameLayout.getChildren().add(gameCanvas);

        Scene gameScene = new Scene(gameLayout, CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
        setupKeyHandlers(gameScene);

        primaryStage.setScene(gameScene);

        startGameLoop();
        startMovementLoop();
        startAILoop();

        draw();
    }

    private void initializeCTFGame(int numPlayers) {
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        flags = new ArrayList<>();
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
                } else if (Math.random() < 0.25) { // Reduced destructible walls for CTF
                    board[y][x] = 2; // Destructible walls
                } else {
                    board[y][x] = 0; // Empty space
                }
            }
        }

        // Create players and flags
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

        // Flag positions (one tile away from player start for visibility)
        int[][] flagPositions = {
                {2, 1}, // Top-left flag
                {BOARD_WIDTH - 3, 1}, // Top-right flag
                {2, BOARD_HEIGHT - 2}, // Bottom-left flag
                {BOARD_WIDTH - 3, BOARD_HEIGHT - 2} // Bottom-right flag
        };

        for (int i = 0; i < numPlayers; i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];

            // Clear starting area around player (larger area for CTF)
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -2; dx <= 2; dx++) {
                    if (x + dx >= 0 && x + dx < BOARD_WIDTH &&
                            y + dy >= 0 && y + dy < BOARD_HEIGHT) {
                        if (board[y + dy][x + dx] == 2) {
                            board[y + dy][x + dx] = 0;
                        }
                    }
                }
            }

            // Create player
            players.add(new Player(i + 1, x, y, playerColors[i], playerKeys[i]));

            // Create flag for this player
            int flagX = flagPositions[i][0];
            int flagY = flagPositions[i][1];
            board[flagY][flagX] = 0; // Ensure flag position is empty
            flags.add(new Flag(flagX, flagY, i + 1, playerColors[i]));
        }
    }

    private void initializeCTFGameVsAI(AIPlayer.Difficulty difficulty) {
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        flags = new ArrayList<>();
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        gameEnded = false;
        showingFinalState = false;
        winner = null;

        // Initialize board
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (x == 0 || x == BOARD_WIDTH - 1 || y == 0 || y == BOARD_HEIGHT - 1) {
                    board[y][x] = 1; // Border walls
                } else if (x % 2 == 0 && y % 2 == 0) {
                    board[y][x] = 1; // Fixed walls
                } else if (Math.random() < 0.25) {
                    board[y][x] = 2; // Destructible walls
                } else {
                    board[y][x] = 0; // Empty space
                }
            }
        }

        Color[] playerColors = {Color.RED, Color.BLUE};
        KeyCode[] humanKeys = {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A};
        int[][] startPositions = {{1, 1}, {BOARD_WIDTH - 2, BOARD_HEIGHT - 2}};
        int[][] flagPositions = {{2, 1}, {BOARD_WIDTH - 3, BOARD_HEIGHT - 2}};

        // Clear starting areas
        for (int i = 0; i < 2; i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -2; dx <= 2; dx++) {
                    if (x + dx >= 0 && x + dx < BOARD_WIDTH &&
                            y + dy >= 0 && y + dy < BOARD_HEIGHT) {
                        if (board[y + dy][x + dx] == 2) {
                            board[y + dy][x + dx] = 0;
                        }
                    }
                }
            }
        }

        // Add human player
        players.add(new Player(1, startPositions[0][0], startPositions[0][1], playerColors[0], humanKeys));
        flags.add(new Flag(flagPositions[0][0], flagPositions[0][1], 1, playerColors[0]));

        // Add AI player with CTF behavior
        players.add(new CTFAIPlayer(2, startPositions[1][0], startPositions[1][1], playerColors[1], difficulty));
        flags.add(new Flag(flagPositions[1][0], flagPositions[1][1], 2, playerColors[1]));
    }

    private static class CTFAIPlayer extends Player {
        private AIPlayer.Difficulty difficulty;
        private long lastDecisionTime = 0;
        private int currentDirection = -1; // 0=up, 1=down, 2=left, 3=right
        private long directionStartTime = 0;
        private int movesSinceDirectionChange = 0;
        private long lastBombTime = 0;

        public CTFAIPlayer(int id, int x, int y, Color color, AIPlayer.Difficulty difficulty) {
            super(id, x, y, color, new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER});
            this.difficulty = difficulty;
            this.directionStartTime = System.currentTimeMillis();
        }

        public void makeDecision(int[][] board, List<Player> players, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            long currentTime = System.currentTimeMillis();

            // Decision frequency
            long decisionDelay = switch (difficulty) {
                case EASY -> 250;
                case MEDIUM -> 150;
                case HARD -> 100;
            };

            if (currentTime - lastDecisionTime < decisionDelay) {
                return;
            }
            lastDecisionTime = currentTime;

            // PRIORITY 1: Escape immediate danger
            if (isInDanger(board, bombs, explosions)) {
                escapeNow(board, bombs, explosions, game);
                return;
            }

            // PRIORITY 2: Place bomb if beneficial (be aggressive!)
            if (shouldPlaceBombNow(board, players, bombs, game.flags, currentTime)) {
                game.placeBomb(this);
                this.lastBombTime = currentTime; // Update our bomb time
                return;
            }

            // PRIORITY 3: Move toward enemy flag
            moveTowardFlag(board, bombs, explosions, game);
        }

        private boolean isInDanger(int[][] board, List<Bomb> bombs, List<Explosion> explosions) {
            // Check explosions
            for (Explosion explosion : explosions) {
                if (explosion.x == this.x && explosion.y == this.y) {
                    return true;
                }
            }

            // Check bombs about to explode
            for (Bomb bomb : bombs) {
                if (bomb.timer <= 30) { // 1.5 seconds warning
                    if (wouldHitMe(bomb.x, bomb.y, bomb.range, board)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean wouldHitMe(int bombX, int bombY, int range, int[][] board) {
            // Same position
            if (bombX == this.x && bombY == this.y) return true;

            // Same row
            if (bombY == this.y && Math.abs(bombX - this.x) <= range) {
                // Check if wall blocks
                int startX = Math.min(bombX, this.x);
                int endX = Math.max(bombX, this.x);
                for (int x = startX + 1; x < endX; x++) {
                    if (board[bombY][x] == 1 || board[bombY][x] == 2) {
                        return false; // Wall blocks
                    }
                }
                return true;
            }

            // Same column
            if (bombX == this.x && Math.abs(bombY - this.y) <= range) {
                // Check if wall blocks
                int startY = Math.min(bombY, this.y);
                int endY = Math.max(bombY, this.y);
                for (int y = startY + 1; y < endY; y++) {
                    if (board[y][bombX] == 1 || board[y][bombX] == 2) {
                        return false; // Wall blocks
                    }
                }
                return true;
            }

            return false;
        }

        private void escapeNow(int[][] board, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // up, down, left, right

            // Find safest direction
            int bestDir = -1;
            int bestSafety = -1;

            for (int i = 0; i < dirs.length; i++) {
                int newX = this.x + dirs[i][0];
                int newY = this.y + dirs[i][1];

                if (canMoveTo(newX, newY, board, bombs)) {
                    int safety = calculateSafety(newX, newY, board, bombs, explosions);
                    if (safety > bestSafety) {
                        bestSafety = safety;
                        bestDir = i;
                    }
                }
            }

            if (bestDir >= 0) {
                game.movePlayer(this, dirs[bestDir][0], dirs[bestDir][1]);
                currentDirection = bestDir;
                directionStartTime = System.currentTimeMillis();
                movesSinceDirectionChange = 0;
            }
        }

        private boolean shouldPlaceBombNow(int[][] board, List<Player> players, List<Bomb> bombs, List<Flag> flags, long currentTime) {
            // Check cooldown
            if (currentTime - this.lastBombTime < this.getBombCooldown()) {
                return false;
            }

            // Check if bomb already here
            for (Bomb bomb : bombs) {
                if (bomb.x == this.x && bomb.y == this.y) {
                    return false;
                }
            }

            // Don't bomb if we can't escape
            if (!canEscapeFromHere(board, bombs)) {
                return false;
            }

            // REASON 1: Enemy in range - ALWAYS bomb
            for (Player player : players) {
                if (player.id != this.id && player.alive) {
                    if (wouldHitTarget(this.x, this.y, this.bombRange, player.x, player.y, board)) {
                        return true; // Attack enemy!
                    }
                }
            }

            // REASON 2: Destructible walls blocking path to flag
            Flag enemyFlag = findEnemyFlag(flags);
            if (enemyFlag != null) {
                int distanceToFlag = Math.abs(enemyFlag.x - this.x) + Math.abs(enemyFlag.y - this.y);

                // Only if reasonably close to flag
                if (distanceToFlag <= 6) {
                    // Check if bomb would destroy walls between us and flag
                    int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
                    for (int[] dir : dirs) {
                        for (int r = 1; r <= this.bombRange; r++) {
                            int checkX = this.x + dir[0] * r;
                            int checkY = this.y + dir[1] * r;

                            if (checkX < 0 || checkX >= board[0].length || checkY < 0 || checkY >= board.length) {
                                break;
                            }
                            if (board[checkY][checkX] == 1) {
                                break; // Fixed wall
                            }
                            if (board[checkY][checkX] == 2) {
                                // Destructible wall - check if it's between us and flag
                                int wallToFlag = Math.abs(checkX - enemyFlag.x) + Math.abs(checkY - enemyFlag.y);
                                if (wallToFlag < distanceToFlag) {
                                    return true; // This wall is blocking our path!
                                }
                                break;
                            }
                        }
                    }
                }
            }

            // REASON 3: Random wall destruction when close to enemy (aggressive play)
            if (difficulty == AIPlayer.Difficulty.HARD) {
                // Hard AI is very aggressive - bombs walls frequently
                if (Math.random() < 0.4) { // 40% chance
                    return canHitWalls(board);
                }
            } else if (difficulty == AIPlayer.Difficulty.MEDIUM) {
                if (Math.random() < 0.2) { // 20% chance
                    return canHitWalls(board);
                }
            }

            return false;
        }

        private boolean wouldHitTarget(int bombX, int bombY, int range, int targetX, int targetY, int[][] board) {
            if (bombX == targetX && bombY == targetY) return true;

            if (bombY == targetY && Math.abs(bombX - targetX) <= range) {
                int startX = Math.min(bombX, targetX);
                int endX = Math.max(bombX, targetX);
                for (int x = startX + 1; x < endX; x++) {
                    if (board[bombY][x] == 1 || board[bombY][x] == 2) {
                        return false;
                    }
                }
                return true;
            }

            if (bombX == targetX && Math.abs(bombY - targetY) <= range) {
                int startY = Math.min(bombY, targetY);
                int endY = Math.max(bombY, targetY);
                for (int y = startY + 1; y < endY; y++) {
                    if (board[y][bombX] == 1 || board[y][bombX] == 2) {
                        return false;
                    }
                }
                return true;
            }

            return false;
        }

        private boolean canHitWalls(int[][] board) {
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] dir : dirs) {
                for (int r = 1; r <= this.bombRange; r++) {
                    int checkX = this.x + dir[0] * r;
                    int checkY = this.y + dir[1] * r;

                    if (checkX < 0 || checkX >= board[0].length || checkY < 0 || checkY >= board.length) {
                        break;
                    }
                    if (board[checkY][checkX] == 1) {
                        break;
                    }
                    if (board[checkY][checkX] == 2) {
                        return true; // Found destructible wall
                    }
                }
            }
            return false;
        }

        private boolean canEscapeFromHere(int[][] board, List<Bomb> bombs) {
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

            // Check if we have at least one escape route
            for (int[] dir : dirs) {
                for (int steps = 1; steps <= this.bombRange + 1; steps++) {
                    int escapeX = this.x + dir[0] * steps;
                    int escapeY = this.y + dir[1] * steps;

                    if (canMoveTo(escapeX, escapeY, board, bombs)) {
                        // Check if this position is outside our bomb range
                        if (!wouldHitTarget(this.x, this.y, this.bombRange, escapeX, escapeY, board)) {
                            return true; // Found escape route
                        }
                    } else {
                        break; // Can't go further in this direction
                    }
                }
            }

            return false;
        }

        private void moveTowardFlag(int[][] board, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            Flag enemyFlag = findEnemyFlag(game.flags);
            if (enemyFlag == null) {
                makeRandomMove(board, bombs, explosions, game);
                return;
            }

            long currentTime = System.currentTimeMillis();

            // Change direction if we've been going the same way for too long or hit obstacles
            boolean shouldChangeDirection =
                    (currentTime - directionStartTime > 2000) || // 2 seconds max
                            (movesSinceDirectionChange > 5) || // 5 moves max
                            (currentDirection == -1); // No direction set

            if (shouldChangeDirection) {
                currentDirection = chooseDirectionToFlag(enemyFlag, board, bombs, explosions);
                directionStartTime = currentTime;
                movesSinceDirectionChange = 0;
            }

            // Try to move in current direction
            if (currentDirection >= 0) {
                int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
                int[] dir = dirs[currentDirection];
                int newX = this.x + dir[0];
                int newY = this.y + dir[1];

                if (canMoveTo(newX, newY, board, bombs) && isSafeMove(newX, newY, board, bombs, explosions)) {
                    game.movePlayer(this, dir[0], dir[1]);
                    movesSinceDirectionChange++;
                    return;
                }
            }

            // Current direction blocked, choose new one
            currentDirection = chooseDirectionToFlag(enemyFlag, board, bombs, explosions);
            if (currentDirection >= 0) {
                int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
                int[] dir = dirs[currentDirection];
                game.movePlayer(this, dir[0], dir[1]);
                directionStartTime = currentTime;
                movesSinceDirectionChange = 1;
            }
        }

        private int chooseDirectionToFlag(Flag flag, int[][] board, List<Bomb> bombs, List<Explosion> explosions) {
            int deltaX = flag.x - this.x;
            int deltaY = flag.y - this.y;

            // Create priority list based on distance
            List<Integer> priorities = new ArrayList<>();

            if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                // X-axis priority
                if (deltaX > 0) priorities.add(3); // right
                if (deltaX < 0) priorities.add(2); // left
                if (deltaY > 0) priorities.add(1); // down
                if (deltaY < 0) priorities.add(0); // up
            } else {
                // Y-axis priority
                if (deltaY > 0) priorities.add(1); // down
                if (deltaY < 0) priorities.add(0); // up
                if (deltaX > 0) priorities.add(3); // right
                if (deltaX < 0) priorities.add(2); // left
            }

            // Try each direction in priority order
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int dirIndex : priorities) {
                int newX = this.x + dirs[dirIndex][0];
                int newY = this.y + dirs[dirIndex][1];

                if (canMoveTo(newX, newY, board, bombs) && isSafeMove(newX, newY, board, bombs, explosions)) {
                    return dirIndex;
                }
            }

            return -1; // No valid direction
        }

        private Flag findEnemyFlag(List<Flag> flags) {
            for (Flag flag : flags) {
                if (flag.ownerId != this.id && !flag.captured) {
                    return flag; // Return first enemy flag found
                }
            }
            return null;
        }

        private boolean canMoveTo(int x, int y, int[][] board, List<Bomb> bombs) {
            if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
                return false;
            }

            if (board[y][x] == 1 || board[y][x] == 2) {
                return false; // Wall
            }

            for (Bomb bomb : bombs) {
                if (bomb.x == x && bomb.y == y) {
                    return false; // Bomb here
                }
            }

            return true;
        }

        private boolean isSafeMove(int x, int y, int[][] board, List<Bomb> bombs, List<Explosion> explosions) {
            // Check explosions
            for (Explosion explosion : explosions) {
                if (explosion.x == x && explosion.y == y) {
                    return false;
                }
            }

            // Check bomb blast zones
            for (Bomb bomb : bombs) {
                if (wouldHitTarget(bomb.x, bomb.y, bomb.range, x, y, board)) {
                    return false;
                }
            }

            return true;
        }

        private int calculateSafety(int x, int y, int[][] board, List<Bomb> bombs, List<Explosion> explosions) {
            if (!canMoveTo(x, y, board, bombs)) return -1;
            if (!isSafeMove(x, y, board, bombs, explosions)) return 0;

            // Count open spaces around this position (mobility)
            int openSpaces = 0;
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] dir : dirs) {
                if (canMoveTo(x + dir[0], y + dir[1], board, bombs)) {
                    openSpaces++;
                }
            }

            return openSpaces + 1; // Base safety + mobility bonus
        }

        private void makeRandomMove(int[][] board, List<Bomb> bombs, List<Explosion> explosions, MenuPrincipal game) {
            int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            List<Integer> validDirs = new ArrayList<>();

            for (int i = 0; i < dirs.length; i++) {
                int newX = this.x + dirs[i][0];
                int newY = this.y + dirs[i][1];
                if (canMoveTo(newX, newY, board, bombs) && isSafeMove(newX, newY, board, bombs, explosions)) {
                    validDirs.add(i);
                }
            }

            if (!validDirs.isEmpty()) {
                int randomDir = validDirs.get((int)(Math.random() * validDirs.size()));
                game.movePlayer(this, dirs[randomDir][0], dirs[randomDir][1]);
                currentDirection = randomDir;
                directionStartTime = System.currentTimeMillis();
                movesSinceDirectionChange = 1;
            }
        }
    }


    private void startGameVsAI(AIPlayer.Difficulty difficulty) {
        initializeGameVsAI(difficulty);

        StackPane gameLayout = new StackPane();
        gameLayout.setStyle("-fx-background-color: #34495e;");

        gameCanvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        gameLayout.getChildren().add(gameCanvas);

        Scene gameScene = new Scene(gameLayout, CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
        setupKeyHandlers(gameScene);

        primaryStage.setScene(gameScene);

        startGameLoop();
        startMovementLoop();
        startAILoop();

        draw();
    }

    private void initializeGameVsAI(AIPlayer.Difficulty difficulty) {
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        gameEnded = false;
        showingFinalState = false;
        winner = null;

        // Initialize board
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

        Color[] playerColors = {Color.RED, Color.BLUE};
        KeyCode[] humanKeys = {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A};
        int[][] startPositions = {{1, 1}, {BOARD_WIDTH - 2, BOARD_HEIGHT - 2}};

        // Clear starting areas
        for (int i = 0; i < 2; i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];
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
        }

        // Add human player
        players.add(new Player(1, startPositions[0][0], startPositions[0][1], playerColors[0], humanKeys));

        // Add AI player
        players.add(new AIPlayer(2, startPositions[1][0], startPositions[1][1], playerColors[1], difficulty));
    }

    private void startAILoop() {
        aiLoop = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            if (gameEnded || showingFinalState) return;

            for (Player player : players) {
                if (player.alive) {
                    // Handle both AIPlayer and CTFAIPlayer
                    if (player instanceof AIPlayer) {
                        AIPlayer aiPlayer = (AIPlayer) player;
                        aiPlayer.makeDecision(board, players, bombs, explosions, this);
                    } else if (player instanceof CTFAIPlayer) {
                        CTFAIPlayer ctfAI = (CTFAIPlayer) player;
                        ctfAI.makeDecision(board, players, bombs, explosions, this);
                    }
                }
            }
        }));
        aiLoop.setCycleCount(Timeline.INDEFINITE);
        aiLoop.play();
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
        if (isCaptureTheFlagMode) {
            checkCTFWinCondition();
        } else {
            checkClassicWinCondition();
        }
    }

    private void checkCTFWinCondition() {
        // Check for flag captures first
        for (Player player : players) {
            if (!player.alive) continue;

            for (Flag flag : flags) {
                if (flag.ownerId != player.id && !flag.captured &&
                        flag.x == player.x && flag.y == player.y) {

                    // Player captured enemy flag - enemy dies
                    flag.captured = true;

                    // Kill the flag owner
                    for (Player flagOwner : players) {
                        if (flagOwner.id == flag.ownerId) {
                            flagOwner.alive = false;
                            break;
                        }
                    }
                }
            }
        }

        // Check win condition
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
            showingFinalState = true;

            if (gameLoop != null) gameLoop.stop();
            if (movementLoop != null) movementLoop.stop();
            if (aiLoop != null) aiLoop.stop();

            Timeline endDelay = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                gameEnded = true;
                showEndScreen(winner);
            }));
            endDelay.play();
        }
    }

    private void checkClassicWinCondition() {
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
            showingFinalState = true;

            if (gameLoop != null) gameLoop.stop();
            if (movementLoop != null) movementLoop.stop();
            if (aiLoop != null) aiLoop.stop();

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
            // Check if the winner is an AI player
            if (winner instanceof AIPlayer) {
                winText = new Text("UN GAGNANT EST L'IA !");
            } else {
                winText = new Text("UN GAGNANT EST JOUEUR " + winner.id + " !");
            }
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

    private void movePlayer(Player player, int dx, int dy) {
        // Only move if player is not currently moving
        if (!player.canMove()) {
            return;
        }

        player.setDirection(dx, dy);
        player.setMoving(true);

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
                } else if (board[newY][newX] == 4) { // Bomb speed powerup
                    player.setTargetPosition(newX, newY);
                    player.bombSpeedPowerUps++;
                    board[newY][newX] = 0; // Remove powerup
                } else if (board[newY][newX] == 5) { // Movement speed powerup
                    player.setTargetPosition(newX, newY);
                    player.movementSpeedPowerUps++;
                    board[newY][newX] = 0; // Remove powerup
                }
            }
        }
    }

    public void placeBomb(Player player) { // Made public for AI access
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

                // Skip AI players - they handle their own movement
                if (player instanceof AIPlayer || player instanceof CTFAIPlayer) continue;

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
                    if (Math.random() < 0.4) { // 40% chance for powerup
                        double rand = Math.random();
                        if (rand < 0.33) {
                            board[newY][newX] = 3; // Range powerup
                        } else if (rand < 0.66) {
                            board[newY][newX] = 4; // Bomb speed powerup
                        } else {
                            board[newY][newX] = 5; // Movement speed powerup
                        }
                    }
                    break; // Stop explosion here
                }
            }
        }
    }

    private void draw() {
        // Clear the entire canvas first
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw ground tiles - this should cover the entire canvas
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                int cellX = x * CELL_SIZE;
                int cellY = y * CELL_SIZE;

                // Always draw ground first
                if (groundTile != null) {
                    gc.drawImage(groundTile, cellX, cellY, CELL_SIZE, CELL_SIZE);
                } else {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                }

                // Then draw walls/powerups on top
                switch (board[y][x]) {
                    case 1: // Fixed wall
                        if (indestructibleWall != null) {
                            gc.drawImage(indestructibleWall, cellX, cellY, CELL_SIZE, CELL_SIZE);
                        } else {
                            gc.setFill(Color.GRAY);
                            gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        }
                        break;
                    case 2: // Destructible wall
                        if (destructibleWall != null) {
                            gc.drawImage(destructibleWall, cellX, cellY, CELL_SIZE, CELL_SIZE);
                        } else {
                            gc.setFill(Color.BROWN);
                            gc.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                        }
                        break;
                    case 3: // Range powerup
                        if (rangePowerup != null) {
                            gc.drawImage(rangePowerup, cellX, cellY, CELL_SIZE, CELL_SIZE);
                        } else {
                            gc.setFill(Color.PURPLE);
                            gc.fillOval(cellX + 6, cellY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                        }
                        break;
                    case 4: // Bomb speed powerup
                        if (bombSpeedPowerup != null) {
                            gc.drawImage(bombSpeedPowerup, cellX, cellY, CELL_SIZE, CELL_SIZE);
                        } else {
                            gc.setFill(Color.CYAN);
                            gc.fillOval(cellX + 6, cellY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                        }
                        break;
                    case 5: // Movement speed powerup
                        if (moveSpeedPowerup != null) {
                            gc.drawImage(moveSpeedPowerup, cellX, cellY, CELL_SIZE, CELL_SIZE);
                        } else {
                            gc.setFill(Color.YELLOW);
                            gc.fillOval(cellX + 6, cellY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                        }
                        break;
                }
            }
        }

        // Then draw bombs with animation
        for (Bomb bomb : bombs) {
            int bombX = bomb.x * CELL_SIZE;
            int bombY = bomb.y * CELL_SIZE;

            if (bombFrames != null && bombFrames.length > 0) {
                // Calculate animation frame based on bomb timer
                // Assuming bomb has a timer field that counts down from maxTimer to 0
                int frameIndex2;
                if (bomb.timer > 49) {
                    frameIndex2 = 1;
                }
                    else if (bomb.timer > 39) {
                    frameIndex2 = 2;
                }
                else if (bomb.timer > 29) {
                    frameIndex2 = 3;
                }
                else if (bomb.timer > 19) {
                    frameIndex2 = 4;
                }
                else if (bomb.timer > 9) {
                    frameIndex2 = 5;
                }
                else {
                    frameIndex2 = 6;
                }
                gc.drawImage(bombFrames[frameIndex2], bombX, bombY, CELL_SIZE, CELL_SIZE);
            } else {
                // Fallback to colored circle
                gc.setFill(Color.BLACK);
                gc.fillOval(bombX + 5, bombY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
            }
        }

        // Then draw explosions
        for (Explosion explosion : explosions) {
            int explX = explosion.x * CELL_SIZE;
            int explY = explosion.y * CELL_SIZE;

            if (explosionFrames != null && explosionFrames.length > 0) {
                int frameIndex = (int)((1 - (explosion.timer / (double)explosion.maxTimer)) * explosionFrames.length);
                frameIndex = Math.min(frameIndex, explosionFrames.length - 1);
                gc.drawImage(explosionFrames[frameIndex], explX, explY, CELL_SIZE, CELL_SIZE);
            } else {
                gc.setFill(Color.ORANGE);
                gc.fillRect(explX + 2, explY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            }
        }

        // Then draw players
        for (Player player : players) {
            if (player.alive) {
                int playerX = (int)(player.renderX * CELL_SIZE);
                int playerY = (int)(player.renderY * CELL_SIZE);

                if (playerSprites != null && playerSprites.length > player.id - 1 && playerSprites[player.id - 1] != null) {
                    gc.drawImage(playerSprites[player.id - 1], playerX, playerY, CELL_SIZE, CELL_SIZE);
                } else {
                    gc.setFill(player.color);
                    gc.fillOval(playerX + 3, playerY + 3, CELL_SIZE - 6, CELL_SIZE - 6);
                }
            }
        }

        // Draw flags in CTF mode
        if (isCaptureTheFlagMode) {
            for (Flag flag : flags) {
                if (!flag.captured && flagSprites != null && flagSprites.length > flag.ownerId - 1 &&
                        flagSprites[flag.ownerId - 1] != null) {
                    int flagX = flag.x * CELL_SIZE;
                    int flagY = flag.y * CELL_SIZE;
                    gc.drawImage(flagSprites[flag.ownerId - 1], flagX, flagY, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Draw powerup info for each player
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            gc.setFill(player.color);
            String info = "P" + player.id + " Range:" + player.bombRange +
                    " BombSpd:" + player.bombSpeedPowerUps +
                    " MoveSpd:" + player.movementSpeedPowerUps +
                    " Cooldown:" + player.getBombCooldown() + "ms" +
                    (player.alive ? "" : " (DEAD)");
            gc.fillText(info, 10, CANVAS_HEIGHT + 30 + i * 18);
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
                String winMessage;
                if (winner instanceof AIPlayer) {
                    winMessage = "L'IA GAGNE !";
                } else {
                    winMessage = "JOUEUR " + winner.id + " GAGNE !";
                }
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
        int bombSpeedPowerUps; // Renamed for clarity
        int movementSpeedPowerUps; // New field for movement speed
        boolean isMoving;
        private static final double BASE_MOVE_SPEED = 0.35; // Base movement interpolation speed
        private static int currentFrame = 0;
        private static long lastFrameTime = 0;
        private static boolean moving = false;
        private int direction = 0; // 0=up, 1=right, 2=down, 3=left

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
            this.bombSpeedPowerUps = 0;
            this.movementSpeedPowerUps = 0;
            this.isMoving = false;
        }

        public void updateMovement() {
            if (isMoving) {
                // Calculate movement speed based on powerups
                double moveSpeed = getMovementSpeed();

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
                    renderX += dx * moveSpeed;
                    renderY += dy * moveSpeed;
                }
                Player.updateAnimation();
            } else {
                Player.setMoving(false);
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
            // Base cooldown is 800ms, reduced by 100ms per bomb speed power-up (minimum 200ms)
            return Math.max(200, 800 - (bombSpeedPowerUps * 100));
        }

        public double getMovementSpeed() {
            // Base speed is 0.35, increased by 0.15 per movement speed power-up (max 0.8)
            return Math.min(0.8, BASE_MOVE_SPEED + (movementSpeedPowerUps * 0.15));
        }

        public void setDirection(int dx, int dy) {
            if (dx > 0) direction = 1; // right
            else if (dx < 0) direction = 3; // left
            else if (dy > 0) direction = 2; // down
            else if (dy < 0) direction = 0; // up
        }

        public static void updateAnimation() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > 100) { // 100ms per frame
                lastFrameTime = currentTime;
                if (moving) {
                    currentFrame = (currentFrame + 1) % 4; // Assuming 4-frame walk cycle
                } else {
                    currentFrame = 0; // Idle frame
                }
            }
        }

        // Call this from your movement code
        public static void setMoving(boolean moving) {
            Player.moving = moving;
            if (!moving) {
                currentFrame = 0; // Reset to idle when not moving
            }
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

