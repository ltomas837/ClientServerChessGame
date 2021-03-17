import interpreter.ClientInterpreterImpl;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;


/**
 * Interface fetching the inputs from the command line when launching the server.
 */
public class Client {

    private static Socket socket = null;
    private static BufferedReader br = null;
    private static FileReader file = null;
    private static Thread GUI_thread = null;

    public static void main(String[] args) {

        ClientInterpreterImpl inputs = new ClientInterpreterImpl(args);

        if ( inputs.getErrorMessage() != null ) {
            display(inputs.getErrorMessage());
            return;
        }

        if ( inputs.helpIsActivated() ) {
            display(inputs.getHelper());
            return;
        }

        if ( inputs.getFile() != null )
            file = inputs.getFile();

        /* Connect to the server and wait for another player */
        try {
            socket = new Socket(InetAddress.getByName(inputs.getIp()), inputs.getPort());
        } catch (IOException e) {
            display("Server " +inputs.getIp()+":"+inputs.getPort()+ " is unavailable");
            return;
        }
        display("Connected to the chess server "+inputs.getIp()+":"+inputs.getPort());


        try {

            /* Initializing where to reads the inputs */
            if ( inputs.getFile() == null )
                br = new BufferedReader(new InputStreamReader(System.in));
            else
                br = new BufferedReader(inputs.getFile());

            /* Another player joined the game/Waiting for another player */
            String from_server = readFromServer();
            display(from_server);

            /* If the client is the first player... */
            if ( Objects.requireNonNull(from_server).equals("Waiting for another player... 1 minute timeout") ) {
                from_server = readFromServer();
                display(from_server);
            }

            /* Fetching the obtained color */
            from_server = readFromServer();
            display(from_server);

            /* The white player begins  */
            if (Objects.requireNonNull(from_server).contains("white")) {
                /* That is your turn... */
                from_server = readFromServer();
                display(from_server);

                /* Asking move + result of the move */
                sendValidMoveToServer();
            }
            else{
                /* Skipping the first line of the file for the black player */
                if ( file != null)
                    br.readLine();
            }

            /* Game loop */
            while (true) {

                /* "Waiting for your opponent to play..." */
                from_server = readFromServer();
                display(from_server);

                /* Move of the opponent */
                from_server = readFromServer();
                display(from_server);

                /* That is your turn... */
                from_server = readFromServer();
                display(from_server);

                /* Asking move + result of the move */
                sendValidMoveToServer();
            }
        } catch (IOException | ClassNotFoundException e) {
            /* If there is a last message from the server to display */
            try {
                display(readFromServer());
            } catch (IOException | ClassNotFoundException ignored) {
                display("The server disconnected");
            }
            disconnect();
        }

    }

    /*
     * Disconnecting the client.
     */
    private static void disconnect() {
        try {
            if (socket != null)
                socket.close();
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Sending a valid move to the server.
     *
     * @throws IOException If the server disconnected
     * @throws ClassNotFoundException If the server disconnected
     */
    private static void sendValidMoveToServer() throws IOException, ClassNotFoundException {
        String from_server = null;

        /* Empty buffered reader if the player entered commands before his/her turn */
        if ( file == null )
            System.in.read(new byte[System.in.available()]);

        while ((from_server == null) || from_server.equals("Invalid move") || from_server.contains("Promotion")) {

            /* Asking move / reading from file */
            String to_server = br.readLine();

            if ( file != null ){
                /* If end of file, switch to command line */
                if ( to_server == null ) {
                    file = null;
                    br.close();
                    br = new BufferedReader(new InputStreamReader(System.in));
                    continue;
                }
                /* Ignore comment line */
                if ( to_server.startsWith("//") || to_server.startsWith("draw") )
                    continue;
            }

            writeToServer(to_server);

            /* Result of the move */
            from_server = readFromServer();
            display(from_server);
        }

        /* Skip the following line as this is for opponent player  */
        if ( file != null )
            br.readLine();
    }

    /*
     * Write the strike to the server.
     *
     * @param message The message to send to the server
     * @throws IOException If the server disconnected
     */
    private static void writeToServer(String message) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        oos.flush();
    }

    /*
     * Read a message from the server. Launch the GUI is the server sends the
     * state of the board.
     *
     * @return The message from the server, null if this is the state of the board
     * @throws IOException If the server disconnected
     * @throws ClassNotFoundException If the server disconnected
     */
    private static String readFromServer() throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Object abstract_message = ois.readObject();

        if ( abstract_message instanceof String ){
            String from_server = (String) abstract_message;
            /* Disconnect when an invalid move is in the file */
            if ( ( file != null ) && ( from_server.equals("Invalid move") ) ) {
                display("Incorrect move in file, please check. Disconnection processing...");
                throw new IOException();
            }
            /* A heartbeat is sent from the server if something expected happened */
            else if ( from_server.equalsIgnoreCase("HEARTBEAT") ){
                throw new IOException();
            }
            /* If the game ends */
            else if ( from_server.contains("unreachable") ||
                    from_server.contains("Stalemate") ||
                    from_server.contains("checkmate") ||
                    from_server.contains("winner") ||
                    from_server.contains("Draw") ){
                display(from_server);
                throw new IOException();
            }

            return from_server;
        }
        /* The message is the board sent by the server */
        else if ( abstract_message instanceof char[][] ){

            /* Prevent from opening multiple window */
            if ( (GUI_thread != null) && GUI_thread.isAlive() )
                return "Please close the board window before opening a new one !";

            char[][] board = (char[][]) abstract_message;

            /* Launching a different thread to continue interacting with the user even if opened */
            GUI_thread = new Thread(() -> {
                System.setProperty("java.library.path", ".");
                GUI gui = new GUI(board);
                gui.launchGUI();
            });
            GUI_thread.start();
        }

        return null;
    }

    /*
     * Display a message on the terminal.
     *
     * @param message The message to display
     */
    private static void display(String message) {
        if ( ( message != null ) && !message.isEmpty()) {
            System.out.println(message);
            System.out.flush();
        }
    }

}



