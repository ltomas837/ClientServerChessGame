

/**
 * Class launching the GUI via the native c++ library.
 */
public class GUI {

    static {
        System.loadLibrary("gui");
    }


    /**
     *  The native function launching the GUI.
     */
    private native void launchGUI(char[][] board);

    public GUI(char[][] board) { launchGUI(board); }

}

