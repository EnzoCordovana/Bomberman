package fr.amu.iut.bomberman.model.game;

import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Classe singleton pour gérer les paramètres du jeu, notamment les contrôles personnalisés.
 * Utilise les préférences système pour persister les paramètres entre les sessions.
 * Thread-safe pour l'utilisation dans un environnement multithreadé.
 */
public class GameSettings {
    private static volatile GameSettings instance;
    private static final Object lock = new Object();

    /** Préférences système pour la persistance */
    private final Preferences prefs;

    /** Mapping des contrôles par joueur - Thread-safe */
    private final Map<Integer, PlayerControlSettings> playerControls;

    /** Paramètres généraux de jeu */
    private String difficulty = "Normal";
    private int playerCount = 2;
    private int gameDuration = 180; // secondes
    private boolean powerupsEnabled = true;
    private boolean obstaclesEnabled = true;

    /**
     * Structure pour stocker les contrôles d'un joueur.
     */
    public static class PlayerControlSettings {
        public KeyCode up, down, left, right, bomb;

        public PlayerControlSettings(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.bomb = bomb;
        }

        /**
         * Créé une copie défensive des contrôles.
         */
        public PlayerControlSettings copy() {
            return new PlayerControlSettings(up, down, left, right, bomb);
        }
    }

    /**
     * Constructeur privé pour le pattern Singleton.
     */
    private GameSettings() {
        prefs = Preferences.userNodeForPackage(GameSettings.class);
        playerControls = new HashMap<>();
        loadDefaultControls();
        loadSettings();
    }

    /**
     * Obtient l'instance unique (thread-safe).
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameSettings();
                }
            }
        }
        return instance;
    }

    /**
     * Charge les contrôles par défaut pour tous les joueurs.
     */
    private void loadDefaultControls() {
        // Joueur 1 - ZQSD + E
        playerControls.put(0, new PlayerControlSettings(
                KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.E
        ));

        // Joueur 2 - OKLM + P
        playerControls.put(1, new PlayerControlSettings(
                KeyCode.O, KeyCode.L, KeyCode.K, KeyCode.M, KeyCode.P
        ));

        // Joueur 3 - Flèches + Entrée
        playerControls.put(2, new PlayerControlSettings(
                KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER
        ));

        // Joueur 4 - Pavé numérique
        playerControls.put(3, new PlayerControlSettings(
                KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0
        ));
    }

    /**
     * Charge les paramètres depuis les préférences système.
     */
    private void loadSettings() {
        // Charger les paramètres généraux
        difficulty = prefs.get("difficulty", difficulty);
        playerCount = prefs.getInt("playerCount", playerCount);
        gameDuration = prefs.getInt("gameDuration", gameDuration);
        powerupsEnabled = prefs.getBoolean("powerupsEnabled", powerupsEnabled);
        obstaclesEnabled = prefs.getBoolean("obstaclesEnabled", obstaclesEnabled);

        // Charger les contrôles personnalisés
        for (int playerId = 0; playerId < 4; playerId++) {
            loadPlayerControls(playerId);
        }

        System.out.println("✅ Paramètres chargés depuis les préférences");
    }

    /**
     * Charge les contrôles d'un joueur spécifique.
     */
    private void loadPlayerControls(int playerId) {
        String prefix = "player" + playerId + "_";

        try {
            String upKey = prefs.get(prefix + "up", null);
            String downKey = prefs.get(prefix + "down", null);
            String leftKey = prefs.get(prefix + "left", null);
            String rightKey = prefs.get(prefix + "right", null);
            String bombKey = prefs.get(prefix + "bomb", null);

            // Si tous les contrôles sont définis, les charger
            if (upKey != null && downKey != null && leftKey != null && rightKey != null && bombKey != null) {
                PlayerControlSettings controls = new PlayerControlSettings(
                        KeyCode.valueOf(upKey),
                        KeyCode.valueOf(downKey),
                        KeyCode.valueOf(leftKey),
                        KeyCode.valueOf(rightKey),
                        KeyCode.valueOf(bombKey)
                );
                playerControls.put(playerId, controls);
                System.out.println("🎮 Contrôles du joueur " + (playerId + 1) + " chargés");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Erreur lors du chargement des contrôles du joueur " + (playerId + 1) + ": " + e.getMessage());
            // Les contrôles par défaut restent en place
        }
    }

    /**
     * Sauvegarde tous les paramètres.
     */
    public synchronized void saveSettings() {
        try {
            // Sauvegarder les paramètres généraux
            prefs.put("difficulty", difficulty);
            prefs.putInt("playerCount", playerCount);
            prefs.putInt("gameDuration", gameDuration);
            prefs.putBoolean("powerupsEnabled", powerupsEnabled);
            prefs.putBoolean("obstaclesEnabled", obstaclesEnabled);

            // Sauvegarder les contrôles
            for (Map.Entry<Integer, PlayerControlSettings> entry : playerControls.entrySet()) {
                savePlayerControls(entry.getKey(), entry.getValue());
            }

            prefs.flush();
            System.out.println("💾 Paramètres sauvegardés");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde les contrôles d'un joueur.
     */
    private void savePlayerControls(int playerId, PlayerControlSettings controls) {
        String prefix = "player" + playerId + "_";
        prefs.put(prefix + "up", controls.up.name());
        prefs.put(prefix + "down", controls.down.name());
        prefs.put(prefix + "left", controls.left.name());
        prefs.put(prefix + "right", controls.right.name());
        prefs.put(prefix + "bomb", controls.bomb.name());
    }

    /**
     * Met à jour les contrôles d'un joueur (thread-safe).
     */
    public synchronized void setPlayerControls(int playerId, KeyCode up, KeyCode down,
                                               KeyCode left, KeyCode right, KeyCode bomb) {
        PlayerControlSettings controls = new PlayerControlSettings(up, down, left, right, bomb);
        playerControls.put(playerId, controls);
        System.out.println("🔄 Contrôles du joueur " + (playerId + 1) + " mis à jour");
    }

    /**
     * Obtient une copie défensive des contrôles d'un joueur.
     */
    public synchronized PlayerControlSettings getPlayerControls(int playerId) {
        PlayerControlSettings controls = playerControls.get(playerId);
        return controls != null ? controls.copy() : null;
    }

    /**
     * Obtient tous les contrôles sous forme de copie défensive.
     */
    public synchronized Map<Integer, PlayerControlSettings> getAllPlayerControls() {
        Map<Integer, PlayerControlSettings> copy = new HashMap<>();
        for (Map.Entry<Integer, PlayerControlSettings> entry : playerControls.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return copy;
    }

    /**
     * Remet les contrôles par défaut pour un joueur.
     */
    public synchronized void resetPlayerControls(int playerId) {
        loadDefaultControls(); // Recharge les contrôles par défaut
        System.out.println("🔄 Contrôles du joueur " + (playerId + 1) + " remis par défaut");
    }

    /**
     * Remet tous les paramètres par défaut.
     */
    public synchronized void resetAllSettings() {
        difficulty = "Normal";
        playerCount = 2;
        gameDuration = 180;
        powerupsEnabled = true;
        obstaclesEnabled = true;
        loadDefaultControls();
        System.out.println("🔄 Tous les paramètres remis par défaut");
    }

    /**
     * Convertit un nom de touche en KeyCode de manière sécurisée.
     */
    public static KeyCode parseKeyCode(String keyName) {
        if (keyName == null || keyName.trim().isEmpty()) {
            return null;
        }

        try {
            // Gestion des cas spéciaux
            switch (keyName.toUpperCase()) {
                case "ESPACE":
                case "SPACE":
                    return KeyCode.SPACE;
                case "ENTREE":
                case "ENTER":
                    return KeyCode.ENTER;
                case "HAUT":
                    return KeyCode.UP;
                case "BAS":
                    return KeyCode.DOWN;
                case "GAUCHE":
                    return KeyCode.LEFT;
                case "DROITE":
                    return KeyCode.RIGHT;
                default:
                    return KeyCode.valueOf(keyName.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Touche invalide: " + keyName);
            return null;
        }
    }

    /**
     * Convertit un KeyCode en nom lisible.
     */
    public static String keyCodeToDisplayName(KeyCode keyCode) {
        if (keyCode == null) return "";

        switch (keyCode) {
            case SPACE: return "ESPACE";
            case ENTER: return "ENTREE";
            case UP: return "HAUT";
            case DOWN: return "BAS";
            case LEFT: return "GAUCHE";
            case RIGHT: return "DROITE";
            default: return keyCode.name();
        }
    }

    // Getters et setters pour les paramètres généraux
    public synchronized String getDifficulty() { return difficulty; }
    public synchronized void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public synchronized int getPlayerCount() { return playerCount; }
    public synchronized void setPlayerCount(int playerCount) { this.playerCount = playerCount; }

    public synchronized int getGameDuration() { return gameDuration; }
    public synchronized void setGameDuration(int gameDuration) { this.gameDuration = gameDuration; }

    public synchronized boolean isPowerupsEnabled() { return powerupsEnabled; }
    public synchronized void setPowerupsEnabled(boolean powerupsEnabled) { this.powerupsEnabled = powerupsEnabled; }

    public synchronized boolean isObstaclesEnabled() { return obstaclesEnabled; }
    public synchronized void setObstaclesEnabled(boolean obstaclesEnabled) { this.obstaclesEnabled = obstaclesEnabled; }
}