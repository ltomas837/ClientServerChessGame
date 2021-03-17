

/**
 * Class launching the GUI via the native c++ library.
 */
public class GUI {

    static {
        System.loadLibrary("gui");
    }

    char[][] board;

    /**
     *  The native function launching the GUI.
     */
    private native void launchGUI(char[][] board);

    public GUI(char[][] board) {
        this.board = board;
    }

    public void launchGUI() {
        launchGUI(board);
    }

}

