package client_server_host;

import requests.SystemEvent;
import systemwide.Origin;

import java.net.DatagramPacket;

/**
 * IntermediateHost is a service class used by Scheduler. It provides methods
 * that manipulate MessageTransfer to receive and send messages to both Client and Server.
 *
 * @author Liam Tripp
 */
public class IntermediateHost {

    private MessageTransfer messageTransfer;

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
        DatagramPacket receivePacket = messageTransfer.createEmptyPacket();
        messageTransfer.receiveMessage(receivePacket);
        messageTransfer.printReceiveMessage(Thread.currentThread().getName(), receivePacket);
        return receivePacket;
    }

    /**
     *
     *
     * @param receivePacket
     * @return true if the receivePacket is data, false if it is a request for data
     */
    public boolean processPacketObject(DatagramPacket receivePacket) {
        // receive message
        // convert bytes to object
        byte[] byteArray = receivePacket.getData();
        Object object = messageTransfer.decodeObject(byteArray);
        /*
            take action depending on object type
            if packet is a data request (i.e. a String), respond to data request here
            otherwise, send a packet acknowledging that data was received
         */
        if (object instanceof String) {
            return false;
        } else {
            // packet is data
            // send message to recipient acknowledging that message was received
            byte[] acknowledgeByteArray = "received".getBytes();
            DatagramPacket acknowledgePacket = messageTransfer.createPacket(acknowledgeByteArray, receivePacket.getAddress(), receivePacket.getPort());
            messageTransfer.sendMessage(acknowledgePacket);
            return true;
        }
    }

    /**
     * Converts a packet into it's corresponding SystemEvent object
     *
     * @param packet to convert to event
     * @return event of packet
     */
    public SystemEvent convertToSystemEvent(DatagramPacket packet) {
        SystemEvent event = (SystemEvent) messageTransfer.decodeObject(packet.getData());
        return event;
    }

    public void addNewPacketToQueue(SystemEvent event, DatagramPacket packet) {
        // encode the altered event into a new packet
        byte[] newByteArray = messageTransfer.encodeObject(event);
        DatagramPacket newPacket = new DatagramPacket(newByteArray, newByteArray.length, packet.getAddress(), packet.getPort());
        messageTransfer.addPacketToQueue(newPacket);
    }

    /**
     * Responds to a data request depending on whether the queue of messages is empty.
     *
     * @param packet a packet received from a system
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
            byte[] emptyQueueByteArray = "emptyQueue".getBytes();
            packetToSend = messageTransfer.createPacket(emptyQueueByteArray, packet.getAddress(), packet.getPort());
        }
        if (packet.getPort() != Port.SERVER.getNumber()) {
            messageTransfer.sendMessage(packetToSend);
        }
    }
}
