package client_server_host;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * IntermediateHost receives and sends message from both Client and Server.
 *
 * @author Liam Tripp
 */
public class IntermediateHost {

    private int portNumber;
    private Queue<DatagramPacket> messageQueue;
    private MessageTransfer messageTransfer;

    /**
     * Constructor for IntermediateHost.
     *
     * @param portNumber the port number corresponding to the IntermediateHost thread
     */
    public IntermediateHost(int portNumber) {
        this.portNumber = portNumber;
        messageQueue = new LinkedList<>();
        messageTransfer = new MessageTransfer();
    }
}
