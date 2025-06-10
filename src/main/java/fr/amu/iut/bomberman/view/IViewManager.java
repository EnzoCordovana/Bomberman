package fr.amu.iut.bomberman.view;

/**
 * Interface pour la gestion de la navigation entre les vues.
 * Respecte le principe de ségrégation des interfaces.
 */
public interface IViewManager {

    /**
     * Affiche la vue du menu principal.
     */
    void showMenuView();

    /**
     * Affiche la vue de jeu.
     */
    void showPlayView();

    /**
     * Affiche la vue des paramètres.
     */
    void showSettingsView();

    /**
     * Affiche la vue des profils.
     */
    void showProfileView();
}