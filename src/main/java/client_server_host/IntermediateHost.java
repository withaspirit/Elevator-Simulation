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

    private Queue<DatagramPacket> messageQueue;

    /**
     * Constructor for IntermediateHost.
     */
    public IntermediateHost() {
        messageQueue = new LinkedList<>();
    }
}
