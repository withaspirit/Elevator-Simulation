package client_server_host;

import requests.SystemEvent;

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

    /**
     * Receives and returns a DatagramPacket from the IntermediateHost's MessageTransfer.
     *
     * @return packet received from the IntermediateHost's MessageTransfer
     */
    public DatagramPacket receivePacket() {
        DatagramPacket receivePacket = messageTransfer.createEmptyPacket();
        messageTransfer.receiveMessage(receivePacket);
        return receivePacket;
    }

    /**
     *
     *
     * @param receivePacket
     * @return true if the receivePacket is data, false if it is a request for data
     */
    public boolean processReceivePacket(DatagramPacket receivePacket) {
        // receive message
        // convert bytes to object
        byte[] byteArray = receivePacket.getData();
        Object object = messageTransfer.decodeObject(byteArray);

        // take action depending on object type
        if (object instanceof String) {
            // packet is a data request
            respondToDataRequest(receivePacket);
            return false;
        } else {
            // packet is data
            // send acknowledge message to recipient
            DatagramPacket acknowledgePacket = createAcknowledgeMessage(receivePacket);
            messageTransfer.sendMessage(acknowledgePacket);
            return true;
        }
    }

    public SystemEvent convertPacketToSystemEvent(DatagramPacket packet) {
        SystemEvent event = (SystemEvent) messageTransfer.decodeObject(packet.getData());
        return event;
    }

    public void addNewMessageToQueue(SystemEvent event, DatagramPacket packet) {
        // encode the altered event into a new packet
        byte[] newByteArray = messageTransfer.encodeObject(event);
        DatagramPacket newPacket = new DatagramPacket(newByteArray, newByteArray.length, packet.getAddress(), packet.getPort());
        messageTransfer.addPacketToQueue(newPacket);
    }

    /**
     * Responds to a dataRequest depending on whether the queue of messages is empty.
     *
     * @param packet a packet received from a system
     */
    private void respondToDataRequest(DatagramPacket packet) {
        DatagramPacket packetToSend;

        if (!messageTransfer.queueIsEmpty()) {
            // if queue is not empty, send an actual message
            // printReceiveMessage(name, packet);
            packetToSend = messageTransfer.getPacketFromQueue();
            // printSendMessage(name, packetToSend);
        } else {
            // otherwise, send a placeholder message
            packetToSend = createEmptyQueueMessage(packet);
        }
        messageTransfer.sendMessage(packetToSend);
    }

    private DatagramPacket createEmptyQueueMessage(DatagramPacket packet) {
        byte[] emptyQueueByteArray = "emptyQueue".getBytes();
        DatagramPacket emptyQueuePacket = new DatagramPacket(emptyQueueByteArray, emptyQueueByteArray.length, packet.getAddress(), packet.getPort());
        return emptyQueuePacket;
    }

    private DatagramPacket createAcknowledgeMessage(DatagramPacket packet) {
        byte[] ackMessageByteArray = "ack".getBytes();
        DatagramPacket acknowledgePacket = new DatagramPacket(ackMessageByteArray, ackMessageByteArray.length, packet.getAddress(), packet.getPort());
        return acknowledgePacket;
    }
}
