package fr.amu.iut.bomberman.model;

/**
 * Classe singleton pour gérer les paramètres du jeu.
 * Stocke et fournit l'accès aux paramètres de configuration.
 */
public class GameSettings {
    private static GameSettings instance;

    // Paramètres de jeu
    private String difficulty;
    private int playerCount;
    private int gameDuration;
    private boolean powerupsEnabled;
    private boolean obstaclesEnabled;

}