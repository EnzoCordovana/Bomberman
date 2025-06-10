package fr.amu.iut.bomberman.model;

import fr.amu.iut.bomberman.model.entities.Player;
import fr.amu.iut.bomberman.model.entities.Bomb;
import fr.amu.iut.bomberman.model.entities.Explosion;
import fr.amu.iut.bomberman.model.map.IMap;
import fr.amu.iut.bomberman.model.map.Map;
import fr.amu.iut.bomberman.model.map.EmptyBlock;
import fr.amu.iut.bomberman.model.map.DestructibleBlock;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * État principal du jeu Bomberman.
 * Gère tous les éléments du jeu : joueurs, bombes, explosions, carte.
 */
public class GameState {
    private IMap map;
    private List<Player> players;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private boolean gameRunning;
    private boolean gamePaused;
    private long gameStartTime;
    private int gameTimeLimit; // En secondes

    // Configuration des joueurs
    private static final Color[] PLAYER_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE
    };

    private static final String[] PLAYER_NAMES = {
            "Joueur 1", "Joueur 2", "Joueur 3", "Joueur 4"
    };

    // Positions de départ des joueurs
    private static final int[][] START_POSITIONS = {
            {1, 1},     // Joueur 1 - coin supérieur gauche
            {13, 1},    // Joueur 2 - coin supérieur droit
            {1, 13},    // Joueur 3 - coin inférieur gauche
            {13, 13}    // Joueur 4 - coin inférieur droit
    };

    public GameState() {
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.gameRunning = false;
        this.gamePaused = false;
        this.gameTimeLimit = 180; // 3 minutes par défaut
    }

    /**
     * Initialise une nouvelle partie.
     * @param playerCount Nombre de joueurs (1-4)
     * @param mapWidth Largeur de la carte
     * @param mapHeight Hauteur de la carte
     */
    public void initializeGame(int playerCount, int mapWidth, int mapHeight) {
        // Initialiser la carte
        map = new Map();
        map.initialize(mapWidth, mapHeight);

        // Créer les joueurs
        players.clear();
        for (int i = 0; i < Math.min(playerCount, 4); i++) {
            Player player = new Player(
                    i,
                    START_POSITIONS[i][0],
                    START_POSITIONS[i][1],
                    PLAYER_COLORS[i],
                    PLAYER_NAMES[i]
            );
            players.add(player);
        }

        // Vider les listes
        bombs.clear();
        explosions.clear();

        // Démarrer le jeu
        gameRunning = true;
        gamePaused = false;
        gameStartTime = System.currentTimeMillis();
    }

    /**
     * Met à jour l'état du jeu.
     * Appelé à chaque frame.
     */
    public void update() {
        if (!gameRunning || gamePaused) return;

        // Mettre à jour les bombes
        updateBombs();

        // Mettre à jour les explosions
        updateExplosions();

        // Vérifier les conditions de fin de partie
        checkEndGameConditions();
    }

    /**
     * Met à jour les bombes et gère les explosions.
     */
    private void updateBombs() {
        Iterator<Bomb> bombIterator = bombs.iterator();

        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();

            if (bomb.shouldExplode()) {
                // Créer l'explosion
                createExplosion(bomb);

                // Informer le joueur que sa bombe a explosé
                getPlayer(bomb.getOwnerId()).bombExploded();

                // Supprimer la bombe
                bombIterator.remove();
            }
        }
    }

    /**
     * Crée une explosion à partir d'une bombe.
     * @param bomb La bombe qui explose
     */
    private void createExplosion(Bomb bomb) {
        int centerX = bomb.getX();
        int centerY = bomb.getY();
        int range = bomb.getExplosionRange();

        // Explosion au centre
        explosions.add(new Explosion(centerX, centerY));

        // Explosions dans les 4 directions
        createExplosionInDirection(centerX, centerY, -1, 0, range); // Gauche
        createExplosionInDirection(centerX, centerY, 1, 0, range);  // Droite
        createExplosionInDirection(centerX, centerY, 0, -1, range); // Haut
        createExplosionInDirection(centerX, centerY, 0, 1, range);  // Bas
    }

    /**
     * Crée des explosions dans une direction donnée.
     */
    private void createExplosionInDirection(int startX, int startY, int dx, int dy, int range) {
        for (int i = 1; i <= range; i++) {
            int x = startX + i * dx;
            int y = startY + i * dy;

            // Vérifier les limites de la carte
            if (!map.isValidPosition(x, y)) break;

            // Vérifier le type de bloc
            if (map.getBlock(x, y).getType() == fr.amu.iut.bomberman.model.map.BlockType.INDESTRUCTIBLE) {
                break; // Les murs indestructibles arrêtent l'explosion
            }

            // Créer l'explosion
            explosions.add(new Explosion(x, y));

            // Si c'est un bloc destructible, le détruire et arrêter l'explosion
            if (map.getBlock(x, y).getType() == fr.amu.iut.bomberman.model.map.BlockType.DESTRUCTIBLE) {
                map.setBlock(x, y, new EmptyBlock(x, y));
                break;
            }
        }
    }

    /**
     * Met à jour les explosions et supprime celles qui sont terminées.
     */
    private void updateExplosions() {
        Iterator<Explosion> explosionIterator = explosions.iterator();

        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();

            if (!explosion.update()) {
                explosionIterator.remove();
            } else {
                // Vérifier si des joueurs sont touchés
                checkPlayerExplosionCollision(explosion);
            }
        }
    }

    /**
     * Vérifie si un joueur est touché par une explosion.
     */
    private void checkPlayerExplosionCollision(Explosion explosion) {
        for (Player player : players) {
            if (player.isAlive() &&
                    player.getGridX() == explosion.getX() &&
                    player.getGridY() == explosion.getY()) {
                player.takeDamage();
            }
        }
    }

    /**
     * Vérifie les conditions de fin de partie.
     */
    private void checkEndGameConditions() {
        // Compter les joueurs vivants
        long alivePlayers = players.stream().filter(Player::isAlive).count();

        // Vérifier le temps limite
        long elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000;

        if (alivePlayers <= 1 || elapsedTime >= gameTimeLimit) {
            endGame();
        }
    }

    /**
     * Termine la partie.
     */
    private void endGame() {
        gameRunning = false;
        // Ici vous pourriez déclencher un événement ou appeler une méthode du contrôleur
    }

    /**
     * Déplace un joueur.
     * @param playerId ID du joueur
     * @param dx Déplacement horizontal
     * @param dy Déplacement vertical
     */
    public void movePlayer(int playerId, int dx, int dy) {
        Player player = getPlayer(playerId);
        if (player == null || !player.isAlive()) return;

        // Calculer la nouvelle position
        int newGridX = player.getGridX() + dx;
        int newGridY = player.getGridY() + dy;

        // Vérifier si le mouvement est valide
        if (canMoveTo(newGridX, newGridY)) {
            player.move(dx, dy);
        }
    }

    /**
     * Vérifie si on peut se déplacer vers une position.
     */
    private boolean canMoveTo(int x, int y) {
        // Vérifier les limites
        if (!map.isValidPosition(x, y)) return false;

        // Vérifier si le bloc est traversable
        if (!map.getBlock(x, y).isTraversable()) return false;

        // Vérifier s'il y a une bombe
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) return false;
        }

        return true;
    }

    /**
     * Place une bombe pour un joueur.
     * @param playerId ID du joueur
     */
    public void placeBomb(int playerId) {
        Player player = getPlayer(playerId);
        if (player == null || !player.isAlive()) return;

        int x = player.getGridX();
        int y = player.getGridY();

        // Vérifier s'il y a déjà une bombe à cette position
        for (Bomb bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) return;
        }

        // Placer la bombe si le joueur peut
        if (player.placeBomb()) {
            bombs.add(new Bomb(x, y, playerId, player.getExplosionRange()));
        }
    }

    /**
     * Trouve un joueur par son ID.
     */
    private Player getPlayer(int playerId) {
        return players.stream()
                .filter(p -> p.getId() == playerId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Met en pause ou reprend le jeu.
     */
    public void togglePause() {
        gamePaused = !gamePaused;
    }

    /**
     * Calcule le temps écoulé depuis le début de la partie.
     * @return Temps écoulé en secondes
     */
    public long getElapsedTime() {
        if (!gameRunning) return 0;
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }

    /**
     * Calcule le temps restant.
     * @return Temps restant en secondes
     */
    public long getRemainingTime() {
        long elapsed = getElapsedTime();
        return Math.max(0, gameTimeLimit - elapsed);
    }

    // Getters
    public IMap getMap() { return map; }
    public List<Player> getPlayers() { return players; }
    public List<Bomb> getBombs() { return bombs; }
    public List<Explosion> getExplosions() { return explosions; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGamePaused() { return gamePaused; }
    public int getGameTimeLimit() { return gameTimeLimit; }

    // Setters
    public void setGameTimeLimit(int gameTimeLimit) { this.gameTimeLimit = gameTimeLimit; }
}