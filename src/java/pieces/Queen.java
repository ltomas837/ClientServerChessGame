package pieces;

import java.util.Arrays;

/**
 * Represents a queen.
 */
public class Queen extends Piece {

    public Queen(int color){
        super(color);
        for (int i=1; i<8; i++){
            allowed_moves.add(Arrays.asList(i,i));
            allowed_moves.add(Arrays.asList(-i,i));
            allowed_moves.add(Arrays.asList(i,-i));
            allowed_moves.add(Arrays.asList(-i,-i));
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
    public String str() {
        return "queen";
    }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 'd'; }


}
