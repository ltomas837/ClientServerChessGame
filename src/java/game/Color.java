package game;


/**
 * Represents the color for the game.
 */
public class Color {

    public static int BLACK = 0;
    public static int WHITE = 1;

    /**
     * Gets the color of the opponent
     *
     * @param c The color to consider
     * @return The opponent color, -1 otherwise
     */
    public static int getOpponentColor(int c){
        if ( c == BLACK )
            return WHITE;
        else if ( c == WHITE )
            return BLACK;
        return -1;
    }

    /**
     * Gives a human understandable string representing the color
     *
     * @param color The colro to consider
     * @return A human understandable string representing the color
     */
    public static String str(int color){
        if ( color == 0 )
            return "black";
        else if (color == 1 )
            return "white";
        return null;
    }

}
