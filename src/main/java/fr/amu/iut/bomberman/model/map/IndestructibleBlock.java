package fr.amu.iut.bomberman.model.map;

/**
 * Représente un bloc indestructible (comme les murs en béton).
 * Ne peut pas être détruit et bloque le passage.
 */
public class IndestructibleBlock extends Block {

    public IndestructibleBlock(int x, int y) {
        super(x, y, BlockType.INDESTRUCTIBLE);
    }

    @Override
    public boolean isTraversable() {
        return false;
    }

    @Override
    public boolean isDestructible() {
        return false;
    }
}