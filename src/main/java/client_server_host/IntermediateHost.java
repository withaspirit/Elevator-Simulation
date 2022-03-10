package client_server_host;

import java.net.DatagramPacket;

/**
 * IntermediateHost receives and sends message from both Client and Server.
 *
 * @author Liam Tripp
 */
public class IntermediateHost {

    private MessageTransfer messageTransfer;

    /**
     * Constructor for IntermediateHost.
     *
     * @param portNumber the port number corresponding to the IntermediateHost thread
     */
    public IntermediateHost(int portNumber) {
        messageTransfer = new MessageTransfer(portNumber);
    }
}
