package client_server_host;

import requests.SystemEvent;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * IntermediateHost is a service class used by Scheduler. It provides methods
 * that manipulate MessageTransfer to receive and send messages to both Client and Server.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class IntermediateHost {

    private final MessageTransfer messageTransfer;
    private Queue<SystemEvent> messageQueue;

    /**
     * Constructor for IntermediateHost.
     *
     * @param portNumber the port number corresponding to a DatagramSocket
     */
    public IntermediateHost(int portNumber) {
        messageTransfer = new MessageTransfer(portNumber);
        messageQueue = new LinkedList<SystemEvent>();
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
     * @param event an event to send to either the Client or Server
     */
    public void addEventToQueue(SystemEvent event) {
        // encode the altered event into a new packet
        messageQueue.add(event);
    }

    /**
     * Responds to a data request depending on whether the queue of messages is empty.
     *
     * @param object the object to send to the Server or Client
     * @param address the address to send the packet to
     * @param port the port to send the packet to
     */
    public void sendObject(Object object, InetAddress address, int port) {
        byte[] message = messageTransfer.encodeObject(object);
        messageTransfer.sendMessage(new DatagramPacket(message, message.length, address, port));
    }

    /**
     * Responds to a SystemEvent object being received.
     *
     * @param packet a packet received from a scheduler
     */
    public void acknowledgeDataReception(DatagramPacket packet){
        byte[] acknowledgeMessage = RequestMessage.ACKNOWLEDGE.getMessage().getBytes();
        DatagramPacket acknowledgePacket = new DatagramPacket(acknowledgeMessage, acknowledgeMessage.length, packet.getAddress(), packet.getPort());
        messageTransfer.sendMessage(acknowledgePacket);
    }

    /**
     * Terminates the ElevatorSubsystem and the FloorSubsystem threads.
     */
    public void terminateSystem() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber;
            if (messageTransfer.getPortNumber() == Port.CLIENT_TO_SERVER.getNumber()) {
                portNumber = Port.SERVER.getNumber();
            } else {
                portNumber = Port.CLIENT.getNumber();
            }
            sendObject(RequestMessage.TERMINATE.getMessage(), inetAddress, portNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        messageTransfer.closeSocket();
    }

    /**
     * Removes and returns a SystemEvent from the queue of packets to be processed.
     *
     * @return a packet from the queue
     */
    public SystemEvent getPacketFromQueue() {
        return messageQueue.remove();
    }

    /**
     * Determines whether the queue of SystemEvent is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean queueIsEmpty() {
        return messageQueue.isEmpty();
    }
}
