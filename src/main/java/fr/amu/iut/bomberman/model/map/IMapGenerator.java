package fr.amu.iut.bomberman.model.map;

/**
 * Interface pour les générateurs de cartes.
 * Permet d'avoir différents algorithmes de génération (Strategy Pattern).
 */
public interface IMapGenerator {

    /**
     * Génère une carte selon l'algorithme implémenté.
     * @param width Largeur de la carte
     * @param height Hauteur de la carte
     * @return Une carte générée
     */
    IMap generateMap(int width, int height);
}