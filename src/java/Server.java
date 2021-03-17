import exceptions.Break75StrikeRuleException;
import exceptions.FiveInHistoryException;
import game.*;
import inputs_interpreter.ServerInterpreterImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.exit;


/**
 * Class representing the server of the chess game.
 */
public class Server {

    private static ServerInterpreterImpl inputs;
    private static ServerSocket server_socket;
    private static final int strike_timeout     = 600000; // 10 minutes
    private static final int connection_timeout = 60000;  // 1 minute



    public static void main(String[] args) {

        inputs = new ServerInterpreterImpl(args);

        if (inputs.getErrorMessage() != null) {
            display(inputs.getErrorMessage());
            return;
        }

        if (inputs.helpIsActivated()) {
            display(inputs.getHelper());
            return;
        }

        List<Player> players = connectServerAndPlayers();

        /* Choose the color of the players, players.get(0) choose randomly, and notify the players */
        int random_color = new Random().nextInt(2);
        if ( random_color == Color.WHITE )
            swap(players);
        players.get(Color.BLACK).setColor(Color.BLACK);
        players.get(Color.WHITE).setColor(Color.WHITE);
        try {
            players.get(Color.BLACK).sendMessage("You have been designated to be the " + Color.str(Color.BLACK) + " color");
            players.get(Color.WHITE).sendMessage("You have been designated to be the " + Color.str(Color.WHITE) + " color");
        } catch (IOException ignored) {}

        /* *********************************** Game Loop *********************************** */
        Board board = new Board();
        int color_to_play = Color.WHITE;
        display("Game starts");

        try {

            while (players.get(color_to_play).canPlay(board)) {

                players.get(color_to_play).sendMessage("That is your turn. Please enter a strike (format ex: a2-a3)...");
                players.get(Color.getOpponentColor(color_to_play)).sendMessage("Waiting for your opponent to play... (any command except exit will be ignored)");

                /* Reinitializing the timeout of the player */
                players.get(color_to_play).setTimeout(strike_timeout);

                Strike strike = players.get(color_to_play).nextStrike(board);

                while (!strike.isAllowed(board, players.get(color_to_play))) {
                    players.get(color_to_play).sendMessage("Invalid move");
                    strike = players.get(color_to_play).nextStrike(board);
                }

                String message = board.update(players, strike);
                sendToPlayers(players, message);
                //display(message); // Will be include in future verbose mode
                color_to_play = Color.getOpponentColor(color_to_play);
                //board.displayBoard(); // Will be include in future verbose mode
            }

            String result = board.result(players.get(color_to_play));
            sendToPlayers(players, result);

        } catch (SocketTimeoutException e){ // If timeout expired
            try {
                /* A heartbeat need to be sent to make sure the player didn't disconnected while waiting */
                players.get(Color.getOpponentColor(color_to_play)).sendMessage("heartbeat");
            } catch (IOException ignored) {}
            sendToPlayers(players, "Timeout for strikes expired - " + (strike_timeout/60000) + " minutes. Game ends. Player "
                    +Color.str(Color.getOpponentColor(color_to_play))+" is the winner !");
            display("Timeout expired - " + (strike_timeout/60000) + " minutes. Game ends. Player "
                    +Color.str(Color.getOpponentColor(color_to_play))+" wins");
        }
        catch (IOException | ClassNotFoundException e) { // If someone disconnected
            int alive_color = Color.BLACK;
            try {
                players.get(Color.BLACK).sendMessage("heartbeat");
            } catch (IOException ignored) {
                alive_color = Color.WHITE;
            }
            try {
                players.get(alive_color).sendMessage("Player "+Color.str(Color.getOpponentColor(alive_color))+" unreachable, the connection is corrupted. Game ends. You are the winner !");
            } catch (IOException ignored) {}
            display("Player "+Color.str(Color.getOpponentColor(alive_color))+" is unreachable, the connection is corrupted. Game ends. Player "+Color.str(alive_color)+" wins.");
        }
        catch (Break75StrikeRuleException e) { // If the 75 strike rule applies
            sendToPlayers(players, "Each player has played 50 strikes without eating any piece. Draw !");
            display("Each player has played 50 strikes without eating any piece. Draw.");
        } catch (FiveInHistoryException e) { // If the 76 hold rule applies
            sendToPlayers(players, "This game configuration already happened 5 times. Draw !");
            display("This game configuration already happened 5 times. Draw.");
        }

        disconnectPlayers(players);
        disconnectServer();

    }

    /*
     * Swap the players. May be used when deciding the color.
     *
     * @param players The players
     */
    private static void swap(List<Player> players) {
        if ( players != null ) {
            Player tmp = players.get(0);
            players.set(0, players.get(1));
            players.set(1, tmp);
        }
    }

    /*
     * Disconnect the server.
     */
    private static void disconnectServer() {
        try {
            if (server_socket != null)
                display("Disconnection...");
                server_socket.close();
        } catch (IOException ignored) {}
        exit(0);
    }

    /*
     * Disconnect the players.
     *
     * @param players The players to disconnect
     */
    private static void disconnectPlayers(List<Player> players){
        /* Should disconnect the players too */
        if ( players != null ) {
            for (Player player : players) {
                player.disconnect();
            }
        }
    }

    /*
     * Connect both the server and 2 players.
     *
     * @return The list of the 2 players
     */
    private static List<Player> connectServerAndPlayers(){

        List<Player> players;

        try {
            server_socket = new ServerSocket(inputs.getPort(), 2, InetAddress.getByName(inputs.getIp()));
            server_socket.setSoTimeout(connection_timeout);
        } catch (IOException e) {
            disconnectServer();
        }

        players = connectPlayers(server_socket);
        if ( players == null ){
            display("Not enough players joined the game... timeout is "+(connection_timeout/60000)+" minute(s) per connection");
            disconnectServer();
        }

        return players;
    }

    /*
     * Connect 2 players for the game to start.
     *
     * @param server_socket The socket of the server
     * @return The connected players, null otherwise
     */
    private static List<Player> connectPlayers(ServerSocket server_socket){

        List<Player> players = new ArrayList<>();

        try {
            /* Connecting first player */
            display("Chess server listening on " + inputs.getIp() + ":" + inputs.getPort());
            Socket socket = server_socket.accept();
            players.add(new Player(socket));
            display("Client connected");
            players.get(0).sendMessage("Waiting for another player... 1 minute timeout");

            /* Connecting second player */
            display("Chess server listening on " + inputs.getIp() + ":" + inputs.getPort());
            socket = server_socket.accept();
            players.add(new Player(socket));
            display("Client connected");

            players.get(0).sendMessage("Another player has joined the game, be ready to play. You have 10 minutes allocated to each strike.");
            players.get(1).sendMessage("Another player has joined the game, be ready to play. You have 10 minutes allocated to each strike.");

        } catch (IOException e) {
            if ( players.size() > 0 ) {
                display("The player left the game, the server will disconnect");
                try {
                    players.get(0).sendMessage("You have been disconnect from the server, no other player joined the game");
                } catch (IOException ignored) {}
                disconnectPlayers(players);
                disconnectServer();
            }
            return null;
        }

        return players;
    }

    /*
     * Display a message on the terminal.
     *
     * @param message The message to display
     */
    private static void display(String message) {
        System.out.println(message);
        System.out.flush();
    }

    /*
     * Sends a message to both players.
     *
     * @param players The players
     * @param message The message to send
     */
    private static void sendToPlayers(List<Player> players, String message) {
        for (Player player: players){
            try {
                player.sendMessage(message);
            } catch (IOException ignored) {} // The client disconnected, nothing to do, the disconnections are already managed in the main
        }
    }

}

