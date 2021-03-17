package game;

import pieces.Piece;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;


/**
 * This class represents a player of the chess game.
 */
public class Player {

    private int color;
    private final Socket socket;


    public Player(Socket socket) {
        this.socket = socket;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    /**
     * Update the timeout of the the socket.
     *
     * @param delay The delay to subtract to the current timeout
     */
    public void updateTimeout(int delay){
        try {
            socket.setSoTimeout(socket.getSoTimeout()-delay);
        } catch (SocketException e) { e.printStackTrace(); }
    }

    /**
     * Checks whether the player can play a move (not in Checkmate or Stalemate).
     *
     * @param board The board to consider
     * @return A boolean for the purpose
     */
    public boolean canPlay(Board board){

        /* Fetching the piece cells of the player */
        List<List<Integer>> piece_cells = board.fetchPieces(color);

        for ( List<Integer> piece_cell: piece_cells ){
            Piece piece = board.getBoard()[piece_cell.get(0)][piece_cell.get(1)];
            if ( piece.canMove(board, this, piece_cell) )
                return true;
        }
        return false;
    }


    /**
     * Given the board, this function checks is the player is in check
     *
     * @param board The board to consider
     * @return A boolean for the purpose
     */
    public boolean isInCheck(Board board) {

        Piece[][] board_game = board.getBoard();

        /* Fetching the cells of the pieces of the opponent player */
        int opponent_color = Color.getOpponentColor(color);
        List<List<Integer>> opponent_piece_cells = board.fetchPieces(opponent_color);

        /* Fetching king cell of current player */
        List<List<Integer>> piece_cells = board.fetchPieces(color);
        List<Integer> king_cell = piece_cells.stream().filter(i -> board_game[i.get(0)][i.get(1)].str().equals("king") ).findFirst().get();

        /* Checking if the king is in check */
        for (List<Integer> opponent_piece_cell: opponent_piece_cells){
            Piece opponent_piece = board_game[opponent_piece_cell.get(0)][opponent_piece_cell.get(1)];
            if ( opponent_piece.allowedMove(opponent_piece_cell, king_cell)
                && opponent_piece.canGoThrough(board_game, opponent_piece_cell, king_cell) )
                return true;
        }

        return false;
    }

    /**
     * Fetch the next strike of the player.
     *
     * @param board The board to consider
     * @return The next strike
     * @throws IOException if the player is unreachable
     * @throws ClassNotFoundException if the player is unreachable
     */
    public Strike nextStrike(Board board) throws IOException, ClassNotFoundException {
        String from_client = readFromClient();
        while ( from_client.equals("display_board") ){
            sendMessage(board.getState());
            from_client = readFromClient();
        }
        return new Strike(from_client, color);
    }

    /**
     * Read a message from the player.
     *
     * @return The message read
     * @throws IOException if the player is unreachable
     * @throws ClassNotFoundException if the player is unreachable
     */
    public String readFromClient() throws IOException, ClassNotFoundException {
        long start_time = System.currentTimeMillis();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        long end_time   = System.currentTimeMillis();
        updateTimeout((int) (end_time - start_time));
        return (String) ois.readObject();
    }

    /**
     * Sends a message to the player.
     *
     * @param message The message to sent
     * @throws IOException if the player is unreachable
     */
    public void sendMessage(Object message) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        oos.flush();
    }

    /**
     * Disconnect the player.
     */
    public void disconnect() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Set the timeout of the player.
     *
     * @param strike_timeout Timeout to set in milliseconds
     */
    public void setTimeout(int strike_timeout) {
        try {
            socket.setSoTimeout(strike_timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
