package pieces;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a knight.
 */
public class Knight extends Piece {

    public Knight(int color){
        super(color);
        allowed_moves.add(Arrays.asList(1,2));
        allowed_moves.add(Arrays.asList(-1,2));
        allowed_moves.add(Arrays.asList(1,-2));
        allowed_moves.add(Arrays.asList(-1,-2));
        allowed_moves.add(Arrays.asList(2,1));
        allowed_moves.add(Arrays.asList(2,-1));
        allowed_moves.add(Arrays.asList(-2,1));
        allowed_moves.add(Arrays.asList(-2,-1));
    }

    /**
     * {@inheritDoc}
     *
     * @return The description
     */
    @Override
    public String str() {
        return "knight";
    }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 'c'; }

    /**
     * Knights can go through pieces, so return true.
     *
     * @param board_game The board to consider
     * @param start_cell The starting cell to consider
     * @param end_cell The ending cell to consider
     * @return true
     */
    @Override
    public boolean canGoThrough(Piece[][] board_game, List<Integer> start_cell, List<Integer> end_cell){ return true; }

}
