package fr.amu.iut.bomberman.model.map;

/**
 * Classe abstraite représentant un bloc sur la carte.
 * Applique le principe SOLID (Single Responsibility).
 */
public abstract class Block {
    protected int x;
    protected int y;
    protected BlockType type;

    public Block(int x, int y, BlockType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public BlockType getType() { return type; }

    /**
     * Détermine si le bloc peut être traversé par un joueur.
     * @return true si le bloc est traversable, false sinon
     */
    public abstract boolean isTraversable();

    /**
     * Détermine si le bloc peut être détruit par une explosion.
     * @return true si le bloc est destructible, false sinon
     */
    public abstract boolean isDestructible();
}