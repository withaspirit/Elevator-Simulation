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

    private MessageTransfer messageTransfer;
    private Origin origin;

    /**
     * Constructor for Client.
     */
    public Client(int portNumber) {
        messageTransfer = new MessageTransfer(portNumber);
    }
    
    /**
     * Constructor for Client with origin.
     */
    public Client(int portNumber, Origin origin) {
    	messageTransfer = new MessageTransfer(portNumber);
    	this.origin = origin;
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
        if (getOrigin() == Origin.ELEVATOR_SYSTEM) {
        	newPacket = messageTransfer.createPacket(newByteArray, Port.CLIENT_TO_SERVER.getNumber());
        } else if (getOrigin() == Origin.FLOOR_SYSTEM) {
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
    public DatagramPacket sendAndReceiveReply(Object object) {
    	//Sending object
    	DatagramPacket sendPacket = buildPacket(object);
    	messageTransfer.sendMessage(sendPacket);
    	
    	//Receiving reply
        DatagramPacket receivePacket = messageTransfer.createEmptyPacket();
        messageTransfer.receiveMessage(receivePacket);
        return receivePacket;
    }
    
    /**
    * Converts a packet into it's corresponding SystemEvent object
    *
    * @param packet to convert to event
    * @return event of packet
    */
   public SystemEvent convertPacketToSystemEvent(DatagramPacket packet) {
       SystemEvent event = (SystemEvent) messageTransfer.decodeObject(packet.getData());
       return event;
   }
   
   /**
   * Returns the Origin of the Client 
   *
   * @return origin of the Client
   */
   public Origin getOrigin() {
	   return origin;
   }
}
