package client_server_host;

/**
 * Server receives and sends message to an IntermediateHost.
 *
 * @author Liam Tripp
 */
public class Server {

    private MessageTransfer messageTransfer;

    /**
     * Constructor for Server.
     */
    public Server() {
        messageTransfer = new MessageTransfer();
    }
}
