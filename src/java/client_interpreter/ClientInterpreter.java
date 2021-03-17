package client_interpreter;

import java.io.FileReader;

/**
 * Interface fetching the inputs from the command line when launching the client.
 */
public interface ClientInterpreter {

    FileReader getFile();
    Integer getPort();
    boolean verboseIsActivated();
    boolean helpIsActivated();
    String getHelper();

    /**
     * The IP should fit the following format "127.0.0.1".
     *
     * @return The IP as string
     */
    String getIp();

    /**
     * Provides the error message which should be printed to the users launching the class.
     *
     * @return The error message, null if no error
     */
    String getErrorMessage();

}
