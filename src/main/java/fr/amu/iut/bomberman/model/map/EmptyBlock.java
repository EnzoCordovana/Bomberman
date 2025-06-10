package fr.amu.iut.bomberman.model.map;

/**
 * Représente un bloc vide sur la carte.
 * Les joueurs peuvent se déplacer dessus.
 */
public class EmptyBlock extends Block {

    public EmptyBlock(int x, int y) {
        super(x, y, BlockType.EMPTY);
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public boolean isDestructible() {
        return false;
    }
}