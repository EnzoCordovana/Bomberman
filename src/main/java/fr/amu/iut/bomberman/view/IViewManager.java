package fr.amu.iut.bomberman.view;

/**
 * Interface pour la gestion de la navigation entre les vues de l'application.
 * Respecte le principe de ségrégation des interfaces du pattern SOLID.
 * Définit les méthodes de navigation disponibles dans l'application Bomberman.
 */
public interface IViewManager {

    /**
     * Affiche la vue du menu principal.
     * Permet de retourner à l'écran d'accueil de l'application.
     */
    void showMenuView();

    /**
     * Affiche la vue de jeu principale.
     * Lance une nouvelle partie de Bomberman.
     */
    void showPlayView();

    /**
     * Affiche la vue des paramètres de l'application.
     * Permet de configurer les contrôles, le son et autres options.
     */
    void showSettingsView();

    /**
     * Affiche la vue de gestion du profil utilisateur.
     * Permet de modifier les informations personnelles et statistiques.
     */
    void showProfileView();
}