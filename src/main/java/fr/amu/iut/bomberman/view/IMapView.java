package fr.amu.iut.bomberman.view;

/**
 * Interface pour les vues de carte.
 * Permet d'avoir différents rendus visuels.
 */
public interface IMapView {

    /**
     * Met à jour l'affichage de la carte.
     */
    void update();

    /**
     * Rafraîchit un bloc spécifique.
     * @param x Coordonnée X du bloc
     * @param y Coordonnée Y du bloc
     */
    void refreshBlock(int x, int y);
}