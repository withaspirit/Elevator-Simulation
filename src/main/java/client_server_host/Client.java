package client_server_host;

/**
 * Client sends and receives messages from an IntermediateHost.
 *
 * @author Liam Tripp
 */
public class Client {

    private MessageTransfer messageTransfer;

    /**
     * Constructor for Client.
     */
    public Client() {
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());
    }
}
