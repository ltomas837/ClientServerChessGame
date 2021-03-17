package game;

import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.Rook;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents the history of the boards of a chess game.
 */
public class History {

    List<Piece[][]> boards = new ArrayList<>();

    public void add(Board board){
        boards.add(board.getBoard());
    }

    /**
     * Checks if the current board configuration already happened 5 times.
     *
     * @param board The board to consider
     * @param current_color The color of the player to play
     * @return A boolean for the purpose
     */
    public boolean FiveInHistory(Board board, int current_color){

        int len_history = boards.size();
        int counter = 0;

        for (int h=0; h<len_history; h++){
            if ( (h%2) == current_color ) // BLACK=0 and WHITE=1... and the first strike is done by the white player
                continue;
            if ( equivalent(boards.get(h), board.getBoard()) )
                counter++;
        }

        return (5 < counter);
    }


    /*
     * Checks if the board configurations are equivalent.
     * By definition, two boards configuration are the same if:
     *      - The same kind of pieces occupy the same cells
     *      - The possibility to take "in_passing" are the same
     *      - The possibility of castling are the same
     * @see https://en.wikipedia.org/wiki/Threefold_repetition
     *
     * @param board1 First board
     * @param board2 Second board
     * @return A boolean for the purpose
     */
    private boolean equivalent(Piece[][] board1, Piece[][] board2) {

        for (int i=0; i<Board.BOARD_HEIGHT; i++){
            for (int j=0; j<Board.BOARD_WIDTH; j++){
                Piece history_p = board1[i][j];
                Piece piece     = board2[i][j];

                if ( ( piece == null ) && ( history_p == null ) )
                    continue;
                if ( (( piece == null ) && ( history_p != null )) || (( piece != null ) && ( history_p == null )) )
                    return false;

                if ( !piece.str().equals(history_p.str()) )
                    return false;

                switch (piece.str()) {
                    case "pawn":
                        if ((((Pawn) piece).isInPassing() != ((Pawn) history_p).isInPassing()))
                            return false;
                        break;
                    case "rook":
                        if ((((Rook) piece).canCastle() != ((Rook) history_p).canCastle()))
                            return false;
                        break;
                    case "king":
                        if ((((King) piece).canCastle() != ((King) history_p).canCastle()))
                            return false;
                        break;
                }
            }
        }

        return true;
    }


}
