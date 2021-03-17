package game;

import exceptions.Break75StrikeRuleException;
import exceptions.FiveInHistoryException;
import pieces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * This class represents the board of the game. It manages some rules as the promotion of pawns to
 * lighten the code of the server.
 *
 */
public class Board {

    public static final int BOARD_HEIGHT = 8;
    public static final int BOARD_WIDTH = 8;
    private final Piece[][] board = new Piece[BOARD_HEIGHT][BOARD_WIDTH];
    private final History history= new History();

    private int strike_counter = 0;
    private int last_eaten_strike = 0;

    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Initialize the board.
     */
    public Board() {

        /* Pawns + empty cases */
        for (int i=0; i<BOARD_WIDTH*2; i++){
            board[1][i%BOARD_WIDTH] = new Pawn(Color.BLACK);
            board[6][i%BOARD_WIDTH] = new Pawn(Color.WHITE);
            board[2+i%2][i%BOARD_WIDTH] = null;
            board[3+i%2][i%BOARD_WIDTH] = null;
        }

        /* All pieces except pawns */
        board[0][0] = new Rook(Color.BLACK);
        board[0][7] = new Rook(Color.BLACK);
        board[7][0] = new Rook(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);
        board[0][1] = new Knight(Color.BLACK);
        board[0][6] = new Knight(Color.BLACK);
        board[7][1] = new Knight(Color.WHITE);
        board[7][6] = new Knight(Color.WHITE);
        board[0][2] = new Bishop(Color.BLACK);
        board[0][5] = new Bishop(Color.BLACK);
        board[7][2] = new Bishop(Color.WHITE);
        board[7][5] = new Bishop(Color.WHITE);
        board[0][3] = new Queen(Color.BLACK);
        board[0][4] = new King(Color.BLACK);
        board[7][3] = new Queen(Color.WHITE);
        board[7][4] = new King(Color.WHITE);

    }


    /**
     * This function update the board by playing the strike. It also update the attribute of the pieces 
     * (in_passing, can_castle, has_moved). A promotion step is done if necessary (a pawn reached the
     * other side of the board). It finally checks that no draw rules are broken (75 moves, 5 hold).
     *
     * @param players The players
     * @param strike The to play by one of the player
     * @return The message to send to the players
     * @throws IOException if a player is unreachable
     * @throws ClassNotFoundException if a player is unreachable
     * @throws Break75StrikeRuleException if there is a breach of the 75 strike rule
     * @throws FiveInHistoryException if there is a breach of 5 hold rule
     */
    public String update(List<Player> players, Strike strike) throws IOException, ClassNotFoundException, Break75StrikeRuleException, FiveInHistoryException {

        strike_counter++;

        /* Initializing variables */
        List<List<Integer>> start_cells = strike.getStartCells();
        List<List<Integer>> end_cells   = strike.getEndCells();
        Piece moved_piece = board[start_cells.get(0).get(0)][start_cells.get(0).get(1)];
        Player player = players.get(moved_piece.getColor());
        String message;

        /* In passing pawn updates */
        updateInPassing(player.getColor());

        /* Play the move */
        Piece eaten_piece = playMoves(start_cells, end_cells);

        /* Generate the message for the players */
        String str_start_cell = ((char) (start_cells.get(0).get(1)+'a'))+""+((char) (BOARD_HEIGHT-1-start_cells.get(0).get(0)+'1'));
        String str_end_cell   = ((char) (end_cells.get(0).get(1)+'a'))+""+((char) (BOARD_HEIGHT-1-end_cells.get(0).get(0)+'1'));
        if ( strike.getStrStrike().equals("0-0") )
            message =  strike_counter+". " +Color.str(moved_piece.getColor())+
                    " king does a little castling from case "+str_start_cell+" to "+str_end_cell+".";
        else if ( strike.getStrStrike().equals("0-0-0") )
            message = strike_counter+". " +Color.str(moved_piece.getColor())+
                    " king does a big castling from case "+str_start_cell+" to "+str_end_cell+".";
        else if ( eaten_piece == null )
            message =  strike_counter + ". " + Color.str(moved_piece.getColor()) + " " +
                    moved_piece.str() + " moves from " + str_start_cell + " to " + str_end_cell+".";
        else {
            last_eaten_strike = strike_counter;
            message = strike_counter + ". " + Color.str(moved_piece.getColor()) + " " +
                    moved_piece.str() + " on " + str_start_cell + " takes " + Color.str(eaten_piece.getColor()) +
                    " " + eaten_piece.str() + " on " + str_end_cell + ".";
        }

        /* Promotion */
        if ( moved_piece.str().equals("pawn") &&
                ((( player.getColor() == Color.BLACK ) && ( end_cells.get(0).get(0) == BOARD_HEIGHT ))
                        || (( player.getColor() == Color.WHITE ) && ( end_cells.get(0).get(0) == 0 )) ))
            message += promotion(player, end_cells.get(0));

        /* Check */
        if ( players.get(Color.getOpponentColor(player.getColor())).isInCheck(this) )
            message += " Check";

        /* Check if no piece eaten for 75 'moves' (1 move here is 2 strikes, one for each player) */
        if ( ( strike_counter - last_eaten_strike ) == 150 ) {
            for ( Player p: players  )
                p.sendMessage(message);
            throw new Break75StrikeRuleException();
        }

        /* Check if  the configuration of the game didn't already happened 5 times */
        history.add(this.getClone());
        if ( history.FiveInHistory(this, moved_piece.getColor()) ) {
            for ( Player p: players  )
                p.sendMessage(message);
            throw new FiveInHistoryException();
        }

        return message;

    }

    /**
     * This function takes care of promoting a pawn if the pawn reached the other side
     * of the board.
     *
     * @param player The player owning the pawn
     * @param end_cell The cell where the pawn is moving
     * @return The promotion message
     * @throws IOException if the player is unreachable
     * @throws ClassNotFoundException if the player is unreachable
     */
    private String promotion(Player player, List<Integer> end_cell) throws IOException, ClassNotFoundException {

        String str_end_cell   = ((char) (end_cell.get(1)+'a'))+""+((char) (BOARD_HEIGHT-end_cell.get(0)+'1'));
        player.sendMessage("Promotion: choose a piece to replace your pawn on "+str_end_cell+": queen/rook/bishop/knight");
        Piece elected = null;

        while ( elected == null  ) {
            String choice = player.readFromClient();
            switch (choice) {
                case "queen":
                    elected = new Queen(player.getColor());
                    break;
                case "rook":
                    elected = new Rook(player.getColor());
                    ((Rook) elected).setCanCastle(false);
                    break;
                case "bishop":
                    elected = new Bishop(player.getColor());
                    break;
                case "knight":
                    elected = new Knight(player.getColor());
                    break;
                default:
                    player.sendMessage("Promotion: piece not recognised, please use the following format: queen/rook/bishop/knight");
                    break;
            }
        }

        board[end_cell.get(0)][end_cell.get(1)] = elected;

        return " The pawn on "+str_end_cell+" has been promoted to a "+elected.str()+".";
    }

    /**
     * Play all the moves of a strike. A strike includes two moves for the rocks.
     *
     * @param start_cells Starting cells to consider
     * @param end_cells Ending cells to consider
     * @return The eaten piece (null by default)
     */
    private Piece playMoves(List<List<Integer>> start_cells, List<List<Integer>> end_cells) {

        Piece eaten_piece = null;

        for (int i=0; i<start_cells.size(); i++){
            List<Integer> start_cell = start_cells.get(i);
            List<Integer> end_cell   = end_cells.get(i);
            eaten_piece = simulation(start_cell, end_cell);
        }

        return eaten_piece;
    }

    /**
     * Update attribute "in_passing" of the pawn: the piece cannot be eaten "in_passing" anymore when
     * the turn comes back to the player again
     *
     * @param color Color to consider
     */
    private void updateInPassing(int color) {
        List<List<Integer>> piece_cells = fetchPieces(color);
        for (List<Integer> piece_cell: piece_cells){
            Piece piece = board[piece_cell.get(0)][piece_cell.get(1)];
            if ( piece.str().equals("pawn") && ( ((Pawn) piece).isInPassing() ) && (((Pawn) piece).hasMoved()) )
                ((Pawn) piece).setInPassing(false);
        }
    }


    /**
     * Return the result (checkmate/stalemate) of the game when a player cannot move anymore.
     *
     * @param player The player which cannot move anymore
     * @return A string representing the result
     */
    public String result(Player player) {
        if ( player.isInCheck(this) )
            return "The "+Color.str(player.getColor())+" player is in checkmate. The "+
                    Color.str(Color.getOpponentColor(player.getColor()))+" player wins !";
        return "The "+Color.str(player.getColor())+" player cannot move and is not in check. Stalemate !";
    }

    /**
     * Fetching the cells on the board of the pieces for a given color.
     *
     * @param color The color to consider
     * @return A list of the cells
     */
    public List<List<Integer>> fetchPieces(int color) {

        List<List<Integer>> piece_cells = new ArrayList<>();

        for (int i=0; i<board.length; i++){
            for (int j=0; j<board[i].length; j++){
                Piece piece = board[i][j];
                if ( ( piece != null ) && ( piece.getColor() == color ) ) {
                    List<Integer> piece_cell = new ArrayList<>();
                    piece_cell.add(i);
                    piece_cell.add(j);
                    piece_cells.add(piece_cell);
                }
            }
        }

        return piece_cells;
    }

    /**
     * Gets a clone of the board. Useful to simulates a strike not on the actual board.
     * For example to check if then the player is not in check. Do not copy the counter
     * and the history as not useful for the purpose.
     *
     * @return The cloned board
     */
    public Board getClone(){

        Board new_board = new Board();

        for (int i=0; i<board.length; i++){
            for (int j=0; j<board[i].length; j++){
                new_board.getBoard()[i][j] = PieceFactoryPrototype.getPiece(board[i][j]);
            }
        }

        return new_board;
    }

    /**
     * Simulates a strike on the calling board. Is used to play a move or to see if
     * a piece can do the move. It also updates the attributes of the moved piece
     * if it is a pawn, rook or king.
     *
     * @param start_cell Starting cell of the strike.
     * @param end_cell Ending cell of the strike.
     * @return The eaten piece, or null if no piece has been eaten.
     */
    public Piece simulation(List<Integer> start_cell, List<Integer>end_cell) {

        Piece moved_piece = board[start_cell.get(0)][start_cell.get(1)];
        Piece eaten_piece = board[end_cell.get(0)][end_cell.get(1)];

        /* Manages the case of in passing and updates the attribute(s) */
        switch (moved_piece.str()) {
            case "pawn":
                if (!((Pawn) moved_piece).hasMoved()) {
                    ((Pawn) moved_piece).setHasMoved(true);
                    ((Pawn) moved_piece).setInPassing(true);
                }
                int horizontal_move = end_cell.get(1) - start_cell.get(1);
                Piece next_to = board[start_cell.get(0)][start_cell.get(1) + horizontal_move];

                /* Conditions to eat in passing */
                if ((horizontal_move != 0) && (next_to != null) && next_to.str().equals("pawn")
                        && (((Pawn) next_to).isInPassing()) && (next_to.getColor() != moved_piece.getColor())) {
                    board[start_cell.get(0)][start_cell.get(1) + horizontal_move] = null;
                    eaten_piece = next_to;
                }
                break;
            case "king":
                ((King) moved_piece).setCanCastle(false);
                break;
            case "rook":
                ((Rook) moved_piece).setCanCastle(false);
                break;
        }

        /* Actually plays the move */
        board[start_cell.get(0)][start_cell.get(1)] = null;
        board[end_cell.get(0)][end_cell.get(1)] = moved_piece;

        return eaten_piece;
    }

    /**
     * Gets the state of the board to send to the client and be drawn be the GUI.
     *
     * @return The state of the board.
     */
    public char[][] getState() {

        char[][] board_state = new char[BOARD_HEIGHT][BOARD_WIDTH];
        Function<Character, Character> color;

        for (int i=0; i<BOARD_HEIGHT; i++){
            for (int j=0; j<BOARD_WIDTH; j++){
                Piece piece = board[i][j];

                /* Empty cell */
                if ( piece == null ) {
                    board_state[i][j] = ' ';
                    continue;
                }

                if ( piece.getColor() == Color.BLACK )
                    color = Character::toLowerCase;
                else
                    color = Character::toUpperCase;

                board_state[i][j] = color.apply(piece.getID());
            }
        }

        return board_state;
    }


    /*
     * This function can be used for the verbose mode in a next version. Will be amended.
     */
    /*
    public void displayBoard(){

        display("______________________________\n");
        for (Piece[] pieces : board) {
            for (Piece piece : pieces) {
                if (piece == null) {
                    display(".");
                } else {
                    if (piece.getColor() == Color.BLACK)
                        display(piece.str().substring(0, 1));
                    else
                        display(piece.str().substring(0, 1).toUpperCase(Locale.ROOT));
                }
            }
            display("\n");
        }
        display("______________________________\n");
    }

    private void display(String message){
        System.out.print(message);
        System.out.flush();
    }
    */

}
