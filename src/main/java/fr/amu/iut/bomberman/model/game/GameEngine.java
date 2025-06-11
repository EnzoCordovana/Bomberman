package fr.amu.iut.bomberman.model.game;

import fr.amu.iut.bomberman.model.entities.Player;
import fr.amu.iut.bomberman.model.entities.Bomb;
import fr.amu.iut.bomberman.model.entities.Explosion;
import fr.amu.iut.bomberman.model.map.GameMap;
import fr.amu.iut.bomberman.model.common.Position;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * GameEngine avec correction du problème CopyOnWriteArrayList
 */
public class GameEngine {
    private GameMap gameMap;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private GameState gameState;

    private static final int CELL_SIZE = 32;

    private static final Position[] START_POSITIONS = {
            new Position(1, 1),     // Joueur 1 - Rouge
            new Position(13, 1),    // Joueur 2 - Bleu
            new Position(1, 11),    // Joueur 3 - Vert
            new Position(13, 11)    // Joueur 4 - Orange
    };

    private static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };

    public GameEngine(GameMap gameMap) {
        this.gameMap = gameMap;
        this.players = new CopyOnWriteArrayList<>();
        this.bombs = new CopyOnWriteArrayList<>();
        this.explosions = new CopyOnWriteArrayList<>();
        this.gameState = new GameState();

        System.out.println("🎮 GameEngine initialisé");
    }

    public void initializeGame(int playerCount) {
        gameMap.reset();
        players.clear();
        bombs.clear();
        explosions.clear();
        gameState.reset();

        playerCount = Math.max(2, Math.min(4, playerCount));

        for (int i = 0; i < playerCount; i++) {
            Position startPos = START_POSITIONS[i];
            Player player = new Player(
                    i,
                    startPos.getX(),
                    startPos.getY(),
                    PLAYER_COLORS[i],
                    "Joueur " + (i + 1)
            );
            players.add(player);
        }

        gameState.setRunning(true);
        System.out.println("🚀 Partie initialisée avec " + playerCount + " joueurs");
    }

    public void update(double deltaTime) {
        if (!gameState.isRunning() || gameState.isPaused()) return;

        updateBombs();
        updateExplosions();
        gameMap.updateExplosions();
        checkPlayerCollisions();
        checkEndGameConditions();
    }

    /**
     * CORRECTION: Méthode thread-safe pour supprimer les bombes
     */
    private void updateBombs() {
        if (bombs.isEmpty()) return;

        // SOLUTION: Créer une liste des bombes à supprimer
        List<Bomb> bombsToRemove = new ArrayList<>();

        for (Bomb bomb : bombs) {
            if (bomb.shouldExplode()) {
                System.out.println("💥 EXPLOSION DÉTECTÉE pour bombe " + bomb);

                Position bombPos = new Position(bomb.getX(), bomb.getY());

                // 1. Créer les entités explosions
                createExplosionEntities(bombPos, bomb.getExplosionRange());

                // 2. Modifier la carte
                gameMap.explodeBomb(bombPos);

                // 3. Gérer le joueur
                Player owner = getPlayer(bomb.getOwnerId());
                if (owner != null) {
                    owner.bombExploded();
                    owner.addScore(10);
                    System.out.println("✅ " + owner.getName() + " - bombe libérée, score: " + owner.getScore());
                }

                // 4. Marquer pour suppression
                bombsToRemove.add(bomb);
            }
        }

        // SOLUTION: Supprimer toutes les bombes explosées en une fois
        if (!bombsToRemove.isEmpty()) {
            bombs.removeAll(bombsToRemove);
            System.out.println("🗑️ " + bombsToRemove.size() + " bombe(s) supprimée(s). Bombes restantes: " + bombs.size());
        }
    }

    /**
     * Crée les entités explosion
     */
    private void createExplosionEntities(Position center, int range) {
        System.out.println("🌟 Création des explosions autour de " + center + " avec range " + range);

        // Explosion au centre
        explosions.add(new Explosion(center.getX(), center.getY()));

        // Explosions dans les 4 directions
        createExplosionInDirection(center, 1, 0, range);   // Droite
        createExplosionInDirection(center, -1, 0, range);  // Gauche
        createExplosionInDirection(center, 0, 1, range);   // Bas
        createExplosionInDirection(center, 0, -1, range);  // Haut

        System.out.println("🎆 Total explosions créées: " + explosions.size());
    }

    private void createExplosionInDirection(Position start, int dx, int dy, int range) {
        for (int i = 1; i <= range; i++) {
            int x = start.getX() + dx * i;
            int y = start.getY() + dy * i;

            if (!gameMap.isValidPosition(x, y)) break;

            var tile = gameMap.getTile(x, y);
            if (tile == null) break;

            if (tile.getType() == fr.amu.iut.bomberman.model.map.Tile.TileType.WALL) {
                break;
            }

            explosions.add(new Explosion(x, y));

            if (tile.getType() == fr.amu.iut.bomberman.model.map.Tile.TileType.DESTRUCTIBLE_WALL) {
                break;
            }
        }
    }

    /**
     * CORRECTION: Méthode thread-safe pour supprimer les explosions
     */
    private void updateExplosions() {
        if (explosions.isEmpty()) return;

        // SOLUTION: Créer une liste des explosions à supprimer
        List<Explosion> explosionsToRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            if (!explosion.update()) {
                explosionsToRemove.add(explosion);
            }
        }

        // SOLUTION: Supprimer toutes les explosions inactives en une fois
        if (!explosionsToRemove.isEmpty()) {
            explosions.removeAll(explosionsToRemove);
            System.out.println("🧹 " + explosionsToRemove.size() + " explosion(s) nettoyée(s). Restantes: " + explosions.size());
        }
    }

    private void checkPlayerCollisions() {
        for (Player player : players) {
            if (!player.isAlive()) continue;

            Position playerPos = new Position(player.getGridX(), player.getGridY());

            // Vérifier collision avec les entités explosions
            boolean hitByExplosion = explosions.stream()
                    .anyMatch(explosion -> explosion.isActive() &&
                            explosion.getX() == playerPos.getX() &&
                            explosion.getY() == playerPos.getY());

            if (hitByExplosion) {
                player.takeDamage();
                System.out.println("💀 " + player.getName() + " touché par explosion! Vies: " + player.getLives());
            }
        }
    }

    private void checkEndGameConditions() {
        long alivePlayers = players.stream().filter(Player::isAlive).count();

        if (alivePlayers <= 1) {
            gameState.setRunning(false);
            Player winner = players.stream()
                    .filter(Player::isAlive)
                    .findFirst()
                    .orElse(null);
            gameState.setWinner(winner);

            System.out.println(winner != null ?
                    "🏆 Gagnant: " + winner.getName() : "⚰️ Match nul!");
        }
    }

    public boolean movePlayer(int playerId, int dx, int dy) {
        Player player = getPlayer(playerId);
        if (player == null || !player.isAlive()) return false;

        int newX = player.getGridX() + dx;
        int newY = player.getGridY() + dy;
        Position newPos = new Position(newX, newY);

        if (canMoveTo(newPos)) {
            player.setPosition(
                    newX * CELL_SIZE + CELL_SIZE / 2.0,
                    newY * CELL_SIZE + CELL_SIZE / 2.0
            );
            return true;
        }
        return false;
    }

    public boolean placeBomb(int playerId) {
        Player player = getPlayer(playerId);
        if (player == null || !player.isAlive()) return false;

        Position bombPos = new Position(player.getGridX(), player.getGridY());

        // Vérifier s'il y a déjà une bombe
        for (Bomb bomb : bombs) {
            if (bomb.getX() == bombPos.getX() && bomb.getY() == bombPos.getY()) {
                System.out.println("❌ Bombe déjà présente en " + bombPos);
                return false;
            }
        }

        if (player.placeBomb()) {
            if (gameMap.placeBomb(bombPos)) {
                Bomb newBomb = new Bomb(
                        bombPos.getX(),
                        bombPos.getY(),
                        playerId,
                        player.getExplosionRange()
                );
                bombs.add(newBomb);
                System.out.println("✅ " + player.getName() + " a placé une bombe en " + bombPos +
                        ". Total bombes: " + bombs.size());
                return true;
            } else {
                player.bombExploded();
                System.out.println("❌ La carte refuse la bombe en " + bombPos);
            }
        } else {
            System.out.println("❌ " + player.getName() + " ne peut pas placer de bombe (limite atteinte)");
        }
        return false;
    }

    private boolean canMoveTo(Position position) {
        if (!gameMap.isValidPosition(position)) return false;
        if (!gameMap.isWalkable(position)) return false;

        for (Bomb bomb : bombs) {
            if (bomb.getX() == position.getX() && bomb.getY() == position.getY()) {
                return false;
            }
        }
        return true;
    }

    private Player getPlayer(int playerId) {
        return players.stream()
                .filter(p -> p.getId() == playerId)
                .findFirst()
                .orElse(null);
    }

    public void togglePause() {
        gameState.setPaused(!gameState.isPaused());
        System.out.println(gameState.isPaused() ? "⏸️ Jeu en pause" : "▶️ Jeu repris");
    }

    // Getters
    public GameMap getGameMap() { return gameMap; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public List<Bomb> getBombs() { return new ArrayList<>(bombs); }
    public List<Explosion> getExplosions() { return new ArrayList<>(explosions); }
    public GameState getGameState() { return gameState; }
    public boolean isGamePaused() { return gameState.isPaused(); }
}