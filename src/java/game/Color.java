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
     * @return The opponent color
     */
    public static int getOpponentColor(int c){
        return (c+1)%2;
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
