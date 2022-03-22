package client_server_host;

import requests.SystemEvent;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * IntermediateHost is a service class used by Scheduler. It provides methods
 * that manipulate MessageTransfer to receive and send messages to both Client and Server.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class IntermediateHost {

    private final MessageTransfer messageTransfer;

    /**
     * Constructor for IntermediateHost.
     *
     * @param portNumber the port number corresponding to a DatagramSocket
     */
    public IntermediateHost(int portNumber) {
        messageTransfer = new MessageTransfer(portNumber);
    }

    /**
     * Receives and returns a DatagramPacket from the IntermediateHost's MessageTransfer.
     *
     * @return packet received from the IntermediateHost's MessageTransfer
     */
    public DatagramPacket receivePacket() {
        DatagramPacket receivePacket = messageTransfer.receiveMessage();
        messageTransfer.printReceiveMessage(Thread.currentThread().getName(), receivePacket);
        return receivePacket;
    }

    /**
     * Converts a packet into it's corresponding SystemEvent object
     *
     * @param packet to convert to event
     * @return object stored in the packet
     */
    public Object convertToObject(DatagramPacket packet) {
        return messageTransfer.decodeObject(packet.getData());
    }

    /**
     * Adds a packets containing an event to the MessageTransfer queue.
     *
     * @param event   an event to send to either the Client or Server
     * @param address an address to send the packet
     * @param port    a port to send the packet
     */
    public void addNewPacketToQueue(SystemEvent event, InetAddress address, int port) {
        // encode the altered event into a new packet
        byte[] newByteArray = messageTransfer.encodeObject(event);
        DatagramPacket newPacket = new DatagramPacket(newByteArray, newByteArray.length, address, port);
        messageTransfer.addPacketToQueue(newPacket);
    }

    /**
     * Responds to a data request depending on whether the queue of messages is empty.
     *
     * @param packet a packet received from a scheduler
     */
    public void respondToDataRequest(DatagramPacket packet) {
        DatagramPacket packetToSend;

        // if queue is not empty, send a packet from the list of queues
        // otherwise, send a placeholder message
        if (!messageTransfer.queueIsEmpty()) {

            // printReceiveMessage(name, packet);
            packetToSend = messageTransfer.getPacketFromQueue();
            // printSendMessage(name, packetToSend);
        } else {
            byte[] emptyQueueMessage = RequestMessage.EMPTYQUEUE.getMessage().getBytes();
            packetToSend = new DatagramPacket(emptyQueueMessage, emptyQueueMessage.length, packet.getAddress(), packet.getPort());
        }
        messageTransfer.sendMessage(packetToSend);
    }

    /**
     * Responds to a System Event object being received.
     *
     * @param packet a packet received from a scheduler
     */
    public void respondToSystemEvent(DatagramPacket packet) {
        byte[] acknowledgeMessage = RequestMessage.ACKNOWLEDGE.getMessage().getBytes();
        DatagramPacket acknowledgePacket = new DatagramPacket(acknowledgeMessage, acknowledgeMessage.length, packet.getAddress(), packet.getPort());
        messageTransfer.sendMessage(acknowledgePacket);
    }
}
