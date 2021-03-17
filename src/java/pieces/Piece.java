package pieces;

import game.Board;
import game.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class represents a chess piece.
 */
public abstract class Piece {

    protected final int color;

    /* Each allowed move is a list in this shape [vertical move, horizontal move] */
    protected final List<List<Integer>> allowed_moves = new ArrayList<>();


    public Piece(int color){ this.color = color; }

    /**
     * Gets a string describing the piece.
     *
     * @return The description
     */
    public abstract String str();

    /**
     *
     * @return The color of the piece
     */
    public int getColor() { return color; }

    /**
     * Gets the ID of the piece.
     *
     * @return The ID
     */
    public abstract char getID();

    /**
     * This function determines if, given a starting cell, the piece can do a move on the board.
     * It also checks that the player is not in check after the move.
     *
     * @param board The board to consider
     * @param player The player owning the piece
     * @param start_cell The starting cell to consider
     * @return A boolean for the purpose
     */
    public boolean canMove(Board board, Player player, List<Integer> start_cell) {

        for (List<Integer> move: allowed_moves){
            List<Integer> end_cell = new ArrayList<>();
            end_cell.add(start_cell.get(0)+move.get(0)*getDirection());
            end_cell.add(start_cell.get(1)+move.get(1));

            /* Check the move is not out of the board */
            if ( (-1 < end_cell.get(0)) && (end_cell.get(0) < Board.BOARD_HEIGHT) &&
                    (-1 < end_cell.get(1)) && (end_cell.get(1) < Board.BOARD_WIDTH) &&
                    canMove(board, player, start_cell, end_cell) ) {
                return true;
            }
        }
        return false;
    }


    /**
     * This function determines if, given a starting and ending cell, the piece
     * can do the move on the board. It also checks that the player is not in check
     * after the move.
     *
     * @param board The board to consider
     * @param player The player owning the piece
     * @param start_cell The starting cell to consider
     * @param end_cell The ending cell to consider
     * @return A boolean for the purpose
     */
    public boolean canMove(Board board, Player player, List<Integer> start_cell, List<Integer> end_cell) {

        Piece[][] board_game = board.getBoard();
        Piece opponent_piece = board_game[end_cell.get(0)][end_cell.get(1)];

        /* Move in allowed_moves
         + If eat a piece, this piece is the opposite color
         + Check the piece can go through another piece */
        if ( !allowedMove(start_cell, end_cell) ||
                (( opponent_piece != null ) && ( opponent_piece.getColor() == color )) ||
                !canGoThrough(board_game, start_cell, end_cell) )
            return false;

        /* Checking that not in check after playing the move */
        Board cloned_board = board.getClone();
        cloned_board.simulation(start_cell, end_cell);
        return !player.isInCheck(cloned_board);
    }

    /**
     * Checks that the move is in allowed_moves
     *
     * @param start_cell The starting cell to consider
     * @param end_cell The ending cell to consider
     * @return A boolean for the purpose
     */
    public boolean allowedMove(List<Integer> start_cell, List<Integer> end_cell) {
        int vertical_move   = (end_cell.get(0)-start_cell.get(0))*getDirection();
        int horizontal_move = end_cell.get(1)-start_cell.get(1);
        return allowed_moves.contains(Arrays.asList(vertical_move, horizontal_move));
    }

    /**
     * Checks whether the piece is stopped by other pieces for moving.
     * The default behaviour is a piece cannot go through another piece.
     *
     * @param board_game The board to consider
     * @param start_cell The starting cell to consider
     * @param end_cell The ending cell to consider
     * @return A boolean for the purpose
     */
    public boolean canGoThrough(Piece[][] board_game, List<Integer> start_cell, List<Integer> end_cell){

        int vertical_move   = (end_cell.get(0)-start_cell.get(0))*getDirection();
        int horizontal_move = end_cell.get(1)-start_cell.get(1);
        int vertical_direction   = 0;
        int horizontal_direction = 0;

        if ( vertical_move != 0 )
            vertical_direction   = vertical_move/Math.abs(vertical_move);
        if ( horizontal_move != 0 )
            horizontal_direction = horizontal_move/Math.abs(horizontal_move);

        /* The default behaviour is a piece cannot go through another piece */
        for (int i=1; i<Math.max(Math.abs(vertical_move), Math.abs(horizontal_move)); i++){
            if ( board_game[start_cell.get(0)+i*vertical_direction][start_cell.get(1)+i*horizontal_direction] != null )
                return false;
        }

        return true;
    }

    /**
     * Gets the direction. Indeed depends on the piece, some pieces have a direction - the pawns.
     *
     * @return The direction (1 or -1, by default 1)
     */
    protected int getDirection() {
        return 1;
    }

}
