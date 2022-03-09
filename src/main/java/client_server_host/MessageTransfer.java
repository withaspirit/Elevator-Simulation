package client_server_host;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * MessageTransfer provides methods for other classes to send, receive, and
 * print DatagramPacket data using a DatagramSocket.
 *
 * @author Liam Tripp, Ryan Dash, Gregory Franks?
 */
public class MessageTransfer {

    private DatagramSocket socket;

    /**
     * Constructor for MessageTransfer.
     *
     * @param portNumber number of the port associated with the DatagramSocket
     */
    public MessageTransfer(int portNumber) {
        try {
            socket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message from this object's socket to the socket corresponding to the
     * packet's destination.
     *
     * @param packet a DatagramPacket containing data to be sent
     */
    public void sendMessage(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.print("Send error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Receives a message from a socket and transfers it to the socket associated
     * with the packet's specified port.
     *
     * @param packet the DatagramPacket containing data received from the
     *               DatagramSocket
     */
    public void receiveMessage(DatagramPacket packet) {
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
     * Prints the contents of a packet and what class is sending the packet.
     *
     * @param name   the name of the class sending the packet
     * @param packet the DatagramPacket containing data
     */
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

    /**
     * Prints the contents of a packet and what class is receiving the packet.
     *
     * @param name   the name of the class receiving the packet
     * @param packet the DatagramPacket containing data
     */
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
            packet = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), portNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return packet;
    }

    public byte[] encodeObject(Object object) {
        byte[] objectBytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            objectBytes = bos.toByteArray();
        } catch (Exception ex) {
            // ignore exception
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return objectBytes;
    }

    public Object decodeObject(byte[] objectBytes) {
        Object object = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
        } catch (Exception ex) {
            // ignore exception
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return object;
    }

}
