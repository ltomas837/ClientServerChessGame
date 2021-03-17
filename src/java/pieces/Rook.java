package pieces;

import java.util.Arrays;

/**
 * Represents a rook.
 */
public class Rook extends Piece {

    private boolean can_castle = true;

    public Rook(int color){
        super(color);
        for (int i=1; i<8; i++){
            allowed_moves.add(Arrays.asList(i,0));
            allowed_moves.add(Arrays.asList(-i,0));
            allowed_moves.add(Arrays.asList(0,i));
            allowed_moves.add(Arrays.asList(0,-i));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The description
     */
    @Override
    public String str() { return "rook"; }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 't'; }

    public boolean canCastle() { return can_castle; }

    public void setCanCastle(boolean can_castle) { this.can_castle = can_castle; }
}
