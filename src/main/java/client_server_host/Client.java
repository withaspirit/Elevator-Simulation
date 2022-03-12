package client_server_host;

import requests.SystemEvent;
import systemwide.Origin;

import java.net.DatagramPacket;

/**
 * Client sends and receives messages from an IntermediateHost.
 *
 * @author Liam Tripp
 */
public class Client {

    private final int portNumber;
    private MessageTransfer messageTransfer;

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
    	
    	//Determine type of message
        if (object instanceof SystemEvent) {
        	newByteArray = messageTransfer.encodeObject(object);
        } else if (object instanceof String) {
        	newByteArray = ((String)object).getBytes();
        } else {
        	throw new IllegalArgumentException("Error: Invalid Object");
        }
        
        //Determine origin
        DatagramPacket newPacket;
        if (portNumber == Port.CLIENT.getNumber()) {
        	newPacket = messageTransfer.createPacket(newByteArray, Port.CLIENT_TO_SERVER.getNumber());
        } else if (portNumber == Port.SERVER.getNumber()) {
        	newPacket = messageTransfer.createPacket(newByteArray, Port.SERVER_TO_CLIENT.getNumber());
        } else {
        	throw new IllegalArgumentException("Error: Invalid Origin");
        }
        
        return newPacket;
    }
 
    /**
     * Builds a Datagram Packet according to its class type
     * 
     * @param object with the message to send, either an Event or an Array
     * @return packet of the object
     */
    public Object sendAndReceiveReply(Object object) {
    	//Sending object
    	DatagramPacket sendPacket = buildPacket(object);
    	messageTransfer.sendMessage(sendPacket);
        messageTransfer.printSendMessage(Thread.currentThread().getName(), sendPacket);

    	//Receiving reply
        DatagramPacket receivePacket = messageTransfer.createEmptyPacket();
        messageTransfer.receiveMessage(receivePacket);
        messageTransfer.printReceiveMessage(Thread.currentThread().getName(), receivePacket);

        return convertToSystemEvent(receivePacket);
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
}
