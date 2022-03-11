package client_server_host;

import requests.SystemEvent;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Client sends and receives messages from an IntermediateHost.
 *
 * @author Liam Tripp
 */
public class Client {

    private MessageTransfer messageTransfer;
    private InetAddress inetAddress;

    /**
     * Constructor for Client.
     */
    public Client() {
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());
        try {
			inetAddress = InetAddress.getLocalHost();
		}  catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
    
    public DatagramPacket buildPacket(Object object) {
    	byte[] newByteArray;
    	
        if (object instanceof SystemEvent) {
        	newByteArray = messageTransfer.encodeObject(object);
        } else if (object instanceof String) {
        	newByteArray = ((String)object).getBytes();
        } else {
        	throw new IllegalArgumentException("Error: Invalid Object");
        }
        DatagramPacket newPacket = messageTransfer.createPacket(newByteArray, Port.CLIENT_TO_SERVER.getNumber());
        return newPacket;
    }
 
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
    *
    * @param packet
    * @return
    */
   public SystemEvent convertPacketToSystemEvent(DatagramPacket packet) {
       SystemEvent event = (SystemEvent) messageTransfer.decodeObject(packet.getData());
       return event;
   }
}
