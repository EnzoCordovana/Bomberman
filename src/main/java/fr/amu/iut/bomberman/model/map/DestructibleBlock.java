package fr.amu.iut.bomberman.model.map;

/**
 * Représente un bloc destructible (comme les caisses dans Bomberman).
 * Peut être détruit par les explosions de bombes.
 */
public class DestructibleBlock extends Block {

    public DestructibleBlock(int x, int y) {
        super(x, y, BlockType.DESTRUCTIBLE);
    }

    @Override
    public boolean isTraversable() {
        return false;
    }

    @Override
    public boolean isDestructible() {
        return true;
    }
}