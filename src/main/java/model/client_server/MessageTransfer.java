package model.client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * MessageTransfer provides methods for other classes to send and receive messages.
 *
 * @author Liam Tripp, Gregory Franks?
 */
public class MessageTransfer {

    /**
     * Constructor for MessageTransfer.
     */
    public MessageTransfer() {
    }

    /**
     * Receives a message from the socket and transfers it to the
     * packet's specified port.
     *
     * @param socket the dDatagramSocket receiving the message
     * @param packet the DatagramPacket receiving the information
     */
    public void receiveMessage(DatagramSocket socket, DatagramPacket packet) {
        // Block until a DatagramPacket is received from a socket
        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Sends a message from the socket to the packet's destination port.
     *
     * @param socket the DatagramSocket sending the message
     * @param packet the DatagramPacket being sent
     */
    public void sendMessage(DatagramSocket socket, DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.print("Send error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // host prints out information it has received
    // Process the received datagram.
    public void printReceiveMessage(String name, DatagramPacket packet) {
        System.out.println(name + ": Packet received:");
//		System.out.println("From host: " + packet.getAddress());
        System.out.println("Host port: " + packet.getPort());
        int len = packet.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        // Form a String from the byte array.
        String received = new String(packet.getData(), 0, len, StandardCharsets.UTF_8);
        System.out.println(received);
        System.out.println();
    }

    public void printSendMessage(String name, DatagramPacket packet) {
        System.out.println(name + ": Sending packet:");
//		System.out.println("To host: " + packet.getAddress());
        System.out.println("Destination host port: " + packet.getPort());
        int len = packet.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        // Form a String from the byte array.
        String sent = new String(packet.getData(), 0, len, StandardCharsets.UTF_8);
        System.out.println(sent);
        System.out.println();
    }

    public DatagramPacket createPacket(byte[] msg, int portNumber) {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), portNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return packet;
    }
}
