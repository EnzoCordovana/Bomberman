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
 * Moteur de jeu principal du Bomberman gérant toute la logique de gameplay.
 * Coordonne les interactions entre les joueurs, bombes, explosions et la carte.
 * Utilise des structures thread-safe pour supporter le multithreading.
 */
public class GameEngine {
    /** Carte de jeu sur laquelle se déroule la partie */
    private GameMap gameMap;

    /** Liste thread-safe des joueurs participants */
    private List<Player> players;

    /** Liste thread-safe des bombes actives sur la carte */
    private List<Bomb> bombs;

    /** Liste thread-safe des explosions en cours */
    private List<Explosion> explosions;

    /** État global du jeu (pause, victoire, etc.) */
    private GameState gameState;

    /** Taille d'une cellule en pixels pour la conversion coordonnées */
    private static final int CELL_SIZE = 32;

    /** Positions de départ prédéfinies pour les joueurs */
    private static final Position[] START_POSITIONS = {
            new Position(1, 1),     // Joueur 1 - Rouge
            new Position(13, 1),    // Joueur 2 - Bleu
            new Position(1, 11),    // Joueur 3 - Vert
            new Position(13, 11)    // Joueur 4 - Orange
    };

    /** Couleurs assignées à chaque joueur */
    private static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };

    /**
     * Constructeur du moteur de jeu.
     *
     * @param gameMap La carte sur laquelle se déroule le jeu
     */
    public GameEngine(GameMap gameMap) {
        this.gameMap = gameMap;
        this.players = new CopyOnWriteArrayList<>();
        this.bombs = new CopyOnWriteArrayList<>();
        this.explosions = new CopyOnWriteArrayList<>();
        this.gameState = new GameState();

        System.out.println("🎮 GameEngine initialisé");
    }

    /**
     * Initialise une nouvelle partie avec le nombre de joueurs spécifié.
     * Remet à zéro tous les éléments du jeu et place les joueurs.
     *
     * @param playerCount Nombre de joueurs (entre 2 et 4)
     */
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

    /**
     * Met à jour la logique de jeu pour une frame.
     * Appelée à chaque cycle de jeu pour faire avancer l'état.
     *
     * @param deltaTime Temps écoulé depuis la dernière mise à jour en secondes
     */
    public void update(double deltaTime) {
        if (!gameState.isRunning() || gameState.isPaused()) return;

        updateBombs();
        updateExplosions();
        gameMap.updateExplosions();
        checkPlayerCollisions();
        checkEndGameConditions();
    }

    /**
     * Met à jour l'état de toutes les bombes et gère les explosions.
     * Méthode thread-safe pour éviter les problèmes de concurrence.
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
     * Crée les entités d'explosion autour d'une position donnée.
     *
     * @param center Position centrale de l'explosion
     * @param range Portée de l'explosion
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

    /**
     * Crée des explosions dans une direction spécifique.
     *
     * @param start Position de départ
     * @param dx Direction X (-1, 0, ou 1)
     * @param dy Direction Y (-1, 0, ou 1)
     * @param range Portée maximale
     */
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
     * Met à jour l'état de toutes les explosions actives.
     * Supprime celles qui ont expiré de manière thread-safe.
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

    /**
     * Vérifie les collisions entre joueurs et éléments dangereux.
     * Gère les dégâts causés par les explosions.
     */
    private void checkPlayerCollisions() {
        for (Player player : players) {
            if (!player.isAlive()) continue;

            Position playerPos = new Position(player.getGridX(), player.getGridY());

            // Vérifier collision avec les entités explosions
            boolean hitByExplosion = explosions.stream()
                    .anyMatch(explosion -> explosion.canDamage() &&
                            explosion.getX() == playerPos.getX() &&
                            explosion.getY() == playerPos.getY());

            if (hitByExplosion) {
                player.takeDamage();
                System.out.println("💀 " + player.getName() + " touché par explosion! Vies: " + player.getLives());
            }
        }
    }

    /**
     * Vérifie les conditions de fin de partie.
     * Détermine s'il y a un gagnant ou un match nul.
     */
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

    /**
     * Déplace un joueur dans une direction donnée.
     *
     * @param playerId Identifiant du joueur à déplacer
     * @param dx Déplacement en X (-1, 0, ou 1)
     * @param dy Déplacement en Y (-1, 0, ou 1)
     * @return true si le déplacement a été effectué
     */
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

    /**
     * Fait placer une bombe par un joueur à sa position actuelle.
     *
     * @param playerId Identifiant du joueur qui place la bombe
     * @return true si la bombe a été placée avec succès
     */
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

    /**
     * Vérifie si une position est accessible pour un déplacement.
     *
     * @param position Position à vérifier
     * @return true si la position est libre
     */
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

    /**
     * Récupère un joueur par son identifiant.
     *
     * @param playerId Identifiant du joueur recherché
     * @return Le joueur correspondant ou null si non trouvé
     */
    private Player getPlayer(int playerId) {
        return players.stream()
                .filter(p -> p.getId() == playerId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Bascule l'état de pause du jeu.
     */
    public void togglePause() {
        gameState.setPaused(!gameState.isPaused());
        System.out.println(gameState.isPaused() ? "⏸️ Jeu en pause" : "▶️ Jeu repris");
    }

    /**
     * Retourne la carte de jeu.
     *
     * @return La carte de jeu actuelle
     */
    public GameMap getGameMap() { return gameMap; }

    /**
     * Retourne une copie de la liste des joueurs pour éviter les modifications externes.
     *
     * @return Liste des joueurs
     */
    public List<Player> getPlayers() { return new ArrayList<>(players); }

    /**
     * Retourne une copie de la liste des bombes pour éviter les modifications externes.
     *
     * @return Liste des bombes actives
     */
    public List<Bomb> getBombs() { return new ArrayList<>(bombs); }

    /**
     * Retourne une copie de la liste des explosions pour éviter les modifications externes.
     *
     * @return Liste des explosions actives
     */
    public List<Explosion> getExplosions() { return new ArrayList<>(explosions); }

    /**
     * Retourne l'état actuel du jeu.
     *
     * @return L'état du jeu
     */
    public GameState getGameState() { return gameState; }

    /**
     * Indique si le jeu est actuellement en pause.
     *
     * @return true si le jeu est en pause
     */
    public boolean isGamePaused() { return gameState.isPaused(); }
}