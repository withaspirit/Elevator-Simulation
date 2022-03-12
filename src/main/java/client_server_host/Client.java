package client_server_host;

import requests.SystemEvent;
import systemwide.Origin;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Client sends and receives messages from an IntermediateHost.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Client {

    private final int portNumber;
    private final MessageTransfer messageTransfer;

    /**
     * Constructor for Client.
     */
    public Client(int portNumber) {
        this.portNumber = portNumber;
        messageTransfer = new MessageTransfer(portNumber);
    }

    /**
     * Builds a Datagram Packet according to its class type
     *
     * @param object to convert into a packet
     * @return packet of the object
     */
    public DatagramPacket buildPacket(Object object) {
        byte[] newByteArray;
        Port tempPort;

        //Determine type of message
        if (object instanceof SystemEvent) {
            if (portNumber == Port.CLIENT.getNumber()){
                tempPort = Port.CLIENT_TO_SERVER;
            } else {
                tempPort = Port.SERVER_TO_CLIENT;
            }
            newByteArray = messageTransfer.encodeObject(object);
        } else if (object instanceof String) {
            if (portNumber == Port.CLIENT.getNumber()){
                tempPort = Port.SERVER_TO_CLIENT;
            } else {
                tempPort = Port.CLIENT_TO_SERVER;
            }
            // NOTE: this code might be unnecessary
            newByteArray = ((String) object).getBytes();
        } else {
            throw new IllegalArgumentException("Error: Invalid Object");
        }

        if (tempPort == null){
            throw new IllegalArgumentException("Error: Invalid PortNumber");
        }

        return messageTransfer.createPacket(newByteArray, tempPort.getNumber());
    }

    /**
     * Send and Receive a reply using messageTransfer
     *
     * @param object with the message to send, either an Event or an Array
     * @return packet of the object
     */
    public Object sendAndReceiveReply(Object object) {
        send(object);
        return receive();
    }

    /**
     * Build a DatagramPacket according to its class type
     * and sends that DatagramPacket using messageTransfer.
     *
     * @param object a string or event object to send in the DatagramPacket
     */
    public void send(Object object){
        DatagramPacket sendPacket = buildPacket(object);
        messageTransfer.sendMessage(sendPacket);
        if (!(object instanceof String)) {
            messageTransfer.printSendMessage(Thread.currentThread().getName(), sendPacket);
        }
    }

    /**
     * Receive a DatagramPacket from the MessageTransfer and
     * return the string or object in the packet as an object.
     *
     * @return an object containing a string or event object.
     */
    public Object receive(){
        //Receiving reply
        DatagramPacket receivePacket = messageTransfer.receiveMessage();
        Object object = convertToSystemEvent(receivePacket);

        if (!(object instanceof String)) {
            messageTransfer.printReceiveMessage(Thread.currentThread().getName(), receivePacket);
        }
        return object;
    }

    /**
    * Converts a packet into it's corresponding SystemEvent object
    *
    * @param packet to convert to event
    * @return event of packet
    */
   public Object convertToSystemEvent(DatagramPacket packet) {
       return messageTransfer.decodeObject(packet.getData());
   }
}
