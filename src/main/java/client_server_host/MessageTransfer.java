package client_server_host;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * MessageTransfer provides methods for other classes to send, receive, and
 * print DatagramPacket data using DatagramSockets.
 *
 * @author Liam Tripp, Ryan Dash, Gregory Franks?
 */
public class MessageTransfer {

    /**
     * Constructor for MessageTransfer.
     */
    public MessageTransfer() {
    }

    /**
     * Sends a message from a given socket to the socket corresponding to the
     * packet's destination port.
     *
     * @param socket the DatagramSocket sending the DatagramPacket
     * @param packet a DatagramPacket containing data to be sent
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

    /**
     * Receives a message from a given socket and transfers it to the socket
     * associated with the packet's specified port.
     *
     * @param socket a DatagramSocket receiving a DatagramPacket
     * @param packet the DatagramPacket containing data received from the DatagramSocket
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

    // FIXME: this could be easily be removed
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
