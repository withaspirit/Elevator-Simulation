package client_server_host;

import requests.SystemEvent;
import systemwide.Origin;

import java.net.DatagramPacket;

/**
 * Client sends and receives messages from an IntermediateHost.
 *
 * @author Liam Tripp, Julian
 */
public class Client {

    private MessageTransfer messageTransfer;

    /**
     * Constructor for Client.
     */
    public Client() {
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());
    }
    
    /**
     * Constructor for Client based on origin.
     */
    public Client(Origin origin) {
        
    	if (origin == Origin.ELEVATOR_SYSTEM) {
    		messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());
    	} else if (origin == Origin.FLOOR_SYSTEM) {
    		messageTransfer = new MessageTransfer(Port.SERVER.getNumber());
    	} else {
    		throw new IllegalArgumentException("Error: Invalid Origin");
    	}
    }
    
    /**
     * Builds a Datagram Packet according to its class type
     * 
     * @param object to convert into a packet
     * @return packet of the object
     */
    public DatagramPacket buildPacket(Object object) {
    	byte[] newByteArray;
    	
        if (object instanceof SystemEvent) {
        	newByteArray = messageTransfer.encodeObject(object);
        } else if (object instanceof String) {
            // NOTE: this code might be unnecessary
        	newByteArray = ((String)object).getBytes();
        } else {
        	throw new IllegalArgumentException("Error: Invalid Object");
        }
        DatagramPacket newPacket = messageTransfer.createPacket(newByteArray, Port.CLIENT_TO_SERVER.getNumber());
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
}
