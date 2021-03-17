package game;

import pieces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Strike {
    
    private static final int KING_CASTLING_COLUMN_START        = 4;
    private static final int KING_LITTLE_CASTLING_COLUMN_END   = 6;
    private static final int KING_BIG_CASTLING_COLUMN_END      = 2;
    private static final int ROOK_LITTLE_CASTLING_COLUMN_START = 0;
    private static final int ROOK_LITTLE_CASTLING_COLUMN_END   = 3;
    private static final int ROOK_BIG_CASTLING_COLUMN_START    = 7;
    private static final int ROOK_BIG_CASTLING_COLUMN_END      = 5;


    /**
     * Starting cells for the strike. Can include one or two moves (two if castling).
     */
    private final List<List<Integer>> start_cells = new ArrayList<>();

    /**
     * Ending cells for the strike. Can include one or two moves (two if castling).
     */
    private final List<List<Integer>> end_cells   = new ArrayList<>();

    /**
     * Human understandable representation of the strike (ex:a2-a3).
     */
    private String str_strike = null;

    public List<List<Integer>> getStartCells() {
        return start_cells;
    }

    public String getStrStrike() {
        return str_strike;
    }

    public List<List<Integer>> getEndCells() {
        return end_cells;
    }

    /**
     * The constructor initialize the starting cells and ending cells of the strike,
     * depending on the message received from the clients.
     * @see Player#nextStrike(Board) 
     *
     * @param str_strike The strike received from the client
     * @param color Color of the piece(s) moving
     */
    public Strike(String str_strike, int color) {
        // For castles, first [i,j] is the king, the second is the rook
        if ( str_strike.equals("0-0") || str_strike.equals("0-0-0") ) {
            this.str_strike = str_strike;
            start_cells.add(new ArrayList<>());
            start_cells.add(new ArrayList<>());
            end_cells.add(new ArrayList<>());
            end_cells.add(new ArrayList<>());

            // King side castle
            if ( str_strike.equals("0-0") ) {
                if (color == Color.BLACK) {
                    start_cells.get(0).add(0);
                    start_cells.get(0).add(4);
                    end_cells.get(0).add(0);
                    end_cells.get(0).add(6);
                    start_cells.get(1).add(0);
                    start_cells.get(1).add(7);
                    end_cells.get(1).add(0);
                    end_cells.get(1).add(5);
                }
                if (color == Color.WHITE) {
                    start_cells.get(0).add(7);
                    start_cells.get(0).add(4);
                    end_cells.get(0).add(7);
                    end_cells.get(0).add(6);
                    start_cells.get(1).add(7);
                    start_cells.get(1).add(7);
                    end_cells.get(1).add(7);
                    end_cells.get(1).add(5);
                }
            }
            // Queen side castle
            else {
                if ( color == Color.BLACK ) {
                    start_cells.get(0).add(0);
                    start_cells.get(0).add(4);
                    end_cells.get(0).add(0);
                    end_cells.get(0).add(2);
                    start_cells.get(1).add(0);
                    start_cells.get(1).add(0);
                    end_cells.get(1).add(0);
                    end_cells.get(1).add(3);
                }
                if ( color == Color.WHITE ) {
                    start_cells.get(0).add(7);
                    start_cells.get(0).add(4);
                    end_cells.get(0).add(7);
                    end_cells.get(0).add(2);
                    start_cells.get(1).add(7);
                    start_cells.get(1).add(0);
                    end_cells.get(1).add(7);
                    end_cells.get(1).add(3);
                }
            }
        }
        /* Normal strike (ex: a2-a3) */
        else if ( str_strike.length() == 5 ){
            String[] cells = str_strike.split("-");
            if ( ( cells.length == 2 ) && (cells[0].length() == 2) &&  (cells[1].length() == 2) ){
                List<Integer> start_cell = fillCell(cells[0]);
                List<Integer> end_cell   = fillCell(cells[1]);
                if ( ( start_cell != null ) && ( end_cell != null ) ) {
                    this.str_strike = str_strike;
                    start_cells.add(start_cell);
                    end_cells.add(end_cell);
                }
            }
        }

    }

    /*
     * Gives the cell indexes [i, j] on the board, given a cell for example "a2"
     * 
     * @param str_cell The cell in human understandable representation
     * @return The cell index in the board [i, j]
     */
    private List<Integer> fillCell(String str_cell) {
        char column = str_cell.charAt(0);
        int column_index;
        if ( ( 'a' <= column ) && ( column <= 'h' ) )
            column_index = column - 'a';
        else
            return null;
        char row = str_cell.charAt(1);
        int row_index;
        if ( ( '1' <= row ) && ( row <= '8' ) )
            row_index = 7-(row - '1');
        else
            return null;
        List<Integer> cell = new ArrayList<>();
        cell.add(row_index);
        cell.add(column_index);
        return cell;
    }

    /**
     * This method determines is a strike is allowed. It depends on the rules and
     * the current state of the board. This function uses the method of the piece
     * to determine if the piece can move.
     * 
     * @param board The board the consider
     * @param player The player to consider
     * @return A boolean for the purpose
     * @see Piece#canMove(Board, Player, List) 
     */
    public boolean isAllowed(Board board, Player player) {

        /* Check the move is in good format */
        if ( start_cells.size() == 0 )
            return false;

        /* Check the player is playing one of his/her piece */
        for (List<Integer> start_cell : start_cells) {
            Piece piece = board.getBoard()[start_cell.get(0)][start_cell.get(1)];
            if ( ( piece == null ) || ( piece.getColor() != player.getColor() ) )
                return false;
        }

        /* Manage first the  */
        if ( str_strike.equals("0-0") )
            return castleIsAllowed(board, player, KING_LITTLE_CASTLING_COLUMN_END, ROOK_LITTLE_CASTLING_COLUMN_START, ROOK_LITTLE_CASTLING_COLUMN_END );
        else if ( str_strike.equals("0-0-0") )
            return castleIsAllowed(board, player, KING_BIG_CASTLING_COLUMN_END, ROOK_BIG_CASTLING_COLUMN_START, ROOK_BIG_CASTLING_COLUMN_END );
        else {
            /* Normal cases, start end end cells are length 1 */
            Piece piece = board.getBoard()[start_cells.get(0).get(0)][start_cells.get(0).get(1)];
            return piece.canMove(board, player, start_cells.get(0), end_cells.get(0));
        }
    }

    /*
     * Check if the castle is allowed. A castle is allowed if
     *        - The king and rook have not moved yet
     *        - The king is not in check
     *        - There is no piece between the rook and the king
     *        - The king does not go into a check after castling
     *        - The king does not go through a check by castling
     *                      (on each cell from the starting to the ending cell )
     * 
     * @param board The board to consider
     * @param player The player to consider
     * @param k_column_end Ending king column after the castle
     * @param r_column_start Starting rook column after the castle
     * @param r_column_end Ending rook column after the castle
     * @return A boolean for the purpose
     */
    private boolean castleIsAllowed(Board board, Player player, int k_column_end, int r_column_start, int r_column_end){

        int color = player.getColor();
        Piece piece_k = board.getBoard()[color*(Board.BOARD_HEIGHT-1)][KING_CASTLING_COLUMN_START];
        Piece piece_r = board.getBoard()[color*(Board.BOARD_HEIGHT-1)][r_column_start];
        int castle_dir = 1;

        /* Determine the direction of the castling: little or big */
        if ( r_column_start < k_column_end )
            castle_dir = -1;

        /* Check that no piece between the rook and the king */
        for (int j=Math.min(KING_CASTLING_COLUMN_START+castle_dir, r_column_start-castle_dir);
                 j<=Math.max(KING_CASTLING_COLUMN_START+castle_dir, r_column_start-castle_dir); j++){
            if ( board.getBoard()[color*(Board.BOARD_HEIGHT-1)][j] != null )
                return false;
        }

        /* Check king not in castle / not going through one a castle / both rook and king can move */
        /* No need to check color as can_castle ensure the color */
        if ( !piece_k.str().equals("king") || !((King) piece_k).canCastle() ||
                !piece_r.str().equals("rook") || !((Rook) piece_r).canCastle() ||
                player.isInCheck(board) ||
                !piece_r.canMove(board, player, Arrays.asList(color*(Board.BOARD_HEIGHT-1), r_column_start),
                        Arrays.asList(color*(Board.BOARD_HEIGHT-1), r_column_end)) ||
                !piece_k.canMove(board, player, Arrays.asList(color*(Board.BOARD_HEIGHT-1), KING_CASTLING_COLUMN_START),
                        Arrays.asList(color*(Board.BOARD_HEIGHT-1), KING_CASTLING_COLUMN_START+castle_dir)) )
            return false;

        /* Have to simulate because moving 2 is not an allowed move for the king */
        Board new_board = board.getClone();
        new_board.simulation(Arrays.asList(color*7, KING_CASTLING_COLUMN_START), Arrays.asList(color*7, KING_CASTLING_COLUMN_START+2*castle_dir));
        return !player.isInCheck(new_board);
    }
}
