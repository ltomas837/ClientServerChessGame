package inputs_interpreter;

import java.util.HashMap;
import java.util.Map;


/**
 * One implementation of the interpretation of the command line launching the server.
 */
public class ServerInterpreterImpl implements ServerInterpreter {

    private static final String usage = "Usage: \tjava Server [-v] [-i ip_address] [-p port_value] [-h]";
    private String error_message = null;

    /* Inputs */
    private String ip       = null;
    private Integer port    = null;
    private boolean verbose = false;
    private boolean help    = false;

    /**
     * Fetching the inputs in the command line. An error message is set if wrong usage.
     *
     * @param args The command line launching the server
     */
    public ServerInterpreterImpl(String[] args){

        /* Expected inputs */
        Map<String, String> arg_values = new HashMap<>();
        arg_values.put("-v", null);
        arg_values.put("-i", null);
        arg_values.put("ip", null);
        arg_values.put("-p", null);
        arg_values.put("port", null);
        arg_values.put("-h", null);

        /* (Bonus check) Max length 6, to avoid server latencies for nothing if
        someone try to enter a large number of arguments. */
        if (args.length > 6){
            error_message = usage;
            return;
        }

        /*
            Checks:
                (1) Each inputs is one of -v, -i, -p, -h, int (port range), [0.255].[0-255].[0-255].[0-255]
                (2) Only one input for each kind (see (1) for the list)
                (3) Option -i must be followed by the ip address. Same for -p and the port
                (4) Check if the -i option is present if and only if an ip address is present. Same for -p and the port
         */
        for (int i=0; i<args.length; i++){

            /* (1) */
            if ( !valid(args[i])  ){
                error_message = usage;
                return;
            }

            /* (2) */
            String type_arg = type(args[i]);
            if ( arg_values.get(type_arg) != null ){
                error_message = usage;
                return;
            }
            arg_values.put(type_arg, args[i]);

            /* (3) */
            if ( type_arg.equals("-i") && ( (i==(args.length-1)) || !(type(args[i+1]).equals("ip")) ) ){
                error_message = usage;
                return;
            }
            else if ( type_arg.equals("-p") && ( (i==(args.length-1)) || !(type(args[i+1]).equals("port")) ) ){
                error_message = usage;
                return;
            }
        }

        /* (4) */
        if ( ((arg_values.get("ip") != null) && (arg_values.get("-i") == null))
                || ((arg_values.get("ip") == null) && (arg_values.get("-i") != null))
                || ((arg_values.get("port") != null) && (arg_values.get("-p") == null))
                || ((arg_values.get("port") == null) && (arg_values.get("-p") != null))){
            error_message = usage;
            return;
        }

        set_inputs(arg_values);
    }

    /*
     * Fill the attributes of the class with the inputs
     *
     * @param arg_values The map including the inputs
     */
    private void set_inputs(Map<String, String> arg_values) {

        if ( arg_values.get("ip") != null )
            ip = arg_values.get("ip");
        else
            ip = "127.0.0.1";

        if ( arg_values.get("port") != null )
            port = Integer.parseInt(arg_values.get("port"));
        else
            port = 2000;

        if ( arg_values.get("-v") != null )
            verbose = true;

        if ( arg_values.get("-h") != null )
            help = true;
    }

    /*
     * Return the input type of arg.
     * Remember that the argument has already been validated before.
     *
     * @param arg the input
     * @return the input type
     */
    private static String type(String arg) {
        if ( arg.contains(".") )
            return "ip";
        try {
            Integer.parseInt(arg);
            return "port";
        } catch (NumberFormatException ignored) {
            return arg;
        }
    }

    /*
     * Check if arg is a valid input.
     *
     * @param arg The input
     * @return Validity
     */
    private static boolean valid(String arg) {

        /* check if valid option */
        if ( arg.equals("-v") || arg.equals("-i") || arg.equals("-p") || arg.equals("-h")  )
            return true;

        /* check if valid port */
        try {
            int port = Integer.parseInt(arg);
            if ( ( 0 < port ) && ( port < 65354 ) )
                return true;
        } catch (NumberFormatException ignored) {}

        /* check if valid ip address */
        String[] ips = arg.split("\\.");
        if ( ips.length != 4 )
            return false;
        try{
            for (int i=0; i<4; i++){
                int ip = Integer.parseInt(ips[i]);
                if ( ( ip < 0) || (255 < ip ) )
                    return false;
            }
        } catch (NumberFormatException ignored) {return false;}

        return true;
    }

    @Override
    public String getIp() { return ip; }

    @Override
    public Integer getPort() { return port; }

    @Override
    public boolean verboseIsActivated() { return verbose; }

    @Override
    public boolean helpIsActivated() { return help; }

    /**
     * {@inheritDoc}
     *
     * @return The error message
     */
    @Override
    public String getErrorMessage() {
        return error_message;
    }

    @Override
    public String getHelper() {
        return usage + "\n" +
                "\t-v: Activate the verbose mode\n" +
                "\t-i: IP address of the interface to bind (by default 127.0.0.1)\n" +
                "\t-p: Port for listening to connection (by default 2000)\n" +
                "\t-h: Helper";
    }
}
