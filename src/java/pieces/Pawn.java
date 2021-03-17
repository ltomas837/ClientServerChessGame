package pieces;

import game.Board;
import game.Color;
import game.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a pawn.
 */
public class Pawn extends Piece {

    private boolean has_moved  = false;
    private boolean in_passing = false;
    private int direction = -1;



    public boolean hasMoved() { return has_moved; }

    public void setHasMoved(boolean has_moved) {
        this.has_moved = has_moved;
    }

    public boolean isInPassing() {
        return in_passing;
    }

    public void setInPassing(boolean in_passing) {
        this.in_passing = in_passing;
    }

    public Pawn(int color){
        super(color);
        if ( color == Color.BLACK )
            direction = 1;
        allowed_moves.add(Arrays.asList(1,0));
        allowed_moves.add(Arrays.asList(2,0));
        allowed_moves.add(Arrays.asList(1,-1));
        allowed_moves.add(Arrays.asList(1,1));
    }

    /**
     * {@inheritDoc}
     *
     * @return The description
     */
    @Override
    public String str() {
        return "pawn";
    }

    /**
     * {@inheritDoc}
     *
     * @return The ID
     */
    @Override
    public char getID() { return 'p'; }

    /**
     * {@inheritDoc}
     *
     * @param board The board to consider
     * @param player The player owning the piece
     * @param start_cell The starting cell to consider
     * @param end_cell The ending cell to consider
     * @return A boolean for the purpose
     */
    @Override
    public boolean canMove(Board board, Player player, List<Integer> start_cell, List<Integer> end_cell) {

        int vertical_move   = (end_cell.get(0)-start_cell.get(0))*direction;
        int horizontal_move = end_cell.get(1)-start_cell.get(1);

        Piece[][] board_game = board.getBoard();

        /* Move in allowed_moves + 2 ahead only if first move */
        if ( !allowed_moves.contains(Arrays.asList(vertical_move, horizontal_move)) || ((vertical_move == 2) && has_moved) )
            return false;
        /* Checking nobody on the cell(s) when moving ahead */
        else if ( horizontal_move == 0 ){
            for (int i=1; i<=vertical_move; i++){
                Piece piece = board_game[start_cell.get(0)+direction*i][start_cell.get(1)];
                if ( piece != null )
                    return false;
            }
        }
        /* Checks if can eat (in passing or normal) */
        else {
            Piece piece = board_game[end_cell.get(0)][end_cell.get(1)];
            /* In passing */
            if ( piece == null ) {
                Piece in_passing_piece = board_game[start_cell.get(0)][start_cell.get(1)+horizontal_move];
                if ( ( in_passing_piece == null ) || !in_passing_piece.str().equals("pawn") ||
                        ( in_passing_piece.getColor() == color ) ||
                        !((Pawn) in_passing_piece).isInPassing())
                    return false;
            }
            /* Can only eat opposite color */
            else if ( piece.getColor() == color )
                return false;
        }

        /* Checking that not in check after playing the move */
        Board cloned_board = board.getClone();
        cloned_board.simulation(start_cell, end_cell);
        return !player.isInCheck(cloned_board);
    }


    /**
     * {@inheritDoc}
     *
     * @return The direction (1 or -1, depending on the color)
     */
    @Override
    protected int getDirection() {
        return direction;
    }
}
