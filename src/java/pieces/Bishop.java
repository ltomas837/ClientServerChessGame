package pieces;

import java.util.Arrays;

/**
 * Represents a Bishop.
 */
public class Bishop extends Piece {

    public Bishop(int color){
        super(color);
        for (int i=1; i<8; i++){
            allowed_moves.add(Arrays.asList(i,i));
            allowed_moves.add(Arrays.asList(-i,i));
            allowed_moves.add(Arrays.asList(i,-i));
            allowed_moves.add(Arrays.asList(-i,-i));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The description
     */
    @Override
    public String str() { return "bishop"; }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 'f'; }

}
