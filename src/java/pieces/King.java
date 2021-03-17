package pieces;

import java.util.Arrays;

/**
 * Represents a king.
 */
public class King extends Piece {

    private boolean can_castle = true;

    public King(int color){
        super(color);
        allowed_moves.add(Arrays.asList(1,1));
        allowed_moves.add(Arrays.asList(-1,1));
        allowed_moves.add(Arrays.asList(1,-1));
        allowed_moves.add(Arrays.asList(-1,-1));
        allowed_moves.add(Arrays.asList(1,0));
        allowed_moves.add(Arrays.asList(-1,0));
        allowed_moves.add(Arrays.asList(0,1));
        allowed_moves.add(Arrays.asList(0,-1));
    }

    /**
     * {@inheritDoc}
     *
     * @return The description
     */
    @Override
    public String str() { return "king"; }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 'r'; }


    public boolean canCastle() { return can_castle; }

    public void setCanCastle(boolean can_castle) { this.can_castle = can_castle; }
}
