package client_server_host;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 * MessageTransfer provides methods for other classes to send, receive, and
 * print DatagramPacket data using a DatagramSocket.
 *
 * @author Liam Tripp, Ryan Dash, Gregory Franks?
 */
public class MessageTransfer {

    private DatagramSocket socket;
    private Queue<DatagramPacket> messageQueue;
    public final static int MAX_BYTE_ARRAY_SIZE = 1400;

    /**
     * Constructor for MessageTransfer.
     *
     * @param portNumber number of the port associated with the DatagramSocket
     */
    public MessageTransfer(int portNumber) {
        try {
            socket = new DatagramSocket(portNumber);
            messageQueue = new LinkedList<>();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the port number associated with the MessageTransfer's DatagramSocket.
     *
     * @return the port number of a DatagramSocket
     */
    public int getPortNumber() {
        return socket.getLocalPort();
    }

    /**
     * Adds a DatagramPacket to the queue of packets to be processed.
     *
     * @param packet the packet to be added to the queue
     */
    public void addPacketToQueue(DatagramPacket packet) {
        messageQueue.add(packet);
    }

    /**
     * Removes and returns a DatagramPacket from the queue of packets to be processed.
     *
     * @return a packet from the queue
     */
    public DatagramPacket getPacketFromQueue() {
        return messageQueue.remove();
    }

    /**
     * Determines whether the queue of DatagramPackets is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean queueIsEmpty() {
        return messageQueue.isEmpty();
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
     * @return the DatagramPacket containing data received from the DatagramSocket
     */
    public DatagramPacket receiveMessage() {
        byte[] data = new byte[MAX_BYTE_ARRAY_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        // Block until a DatagramPacket is received from a socket
        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        return packet;
    }

    /**
     * Prints the contents of a packet and what class is sending the packet.
     *
     * @param name   the name of the class sending the packet
     * @param packet the DatagramPacket containing data
     */
    public void printSendMessage(String name, DatagramPacket packet) {
        // Form a String from the byte array.
        Object object = decodeObject(packet.getData());
        String messageToPrint = LocalTime.now().toString() + "\n";
        messageToPrint += name + " sending ";
        if (packet.getPort() == Port.CLIENT_TO_SERVER.getNumber() || packet.getPort() == Port.SERVER_TO_CLIENT.getNumber()) {
            messageToPrint += "to Scheduler:";
        } else if (packet.getPort() == Port.CLIENT.getNumber()) {
            messageToPrint += "to Client:";
        } else if (packet.getPort() == Port.SERVER.getNumber()) {
            messageToPrint += "to Server:";
        }
        messageToPrint += "\n";
        if (object instanceof String string) {
            messageToPrint += string;
        } else {
            messageToPrint += object.getClass().getSimpleName() + " Packet: ";
        }
        messageToPrint += "Host port: " + packet.getPort();
        messageToPrint += ", Length: " + packet.getLength() + "\n";
        messageToPrint += object + "\n";
        System.out.println(messageToPrint);
    }

    /**
     * Prints the contents of a packet and what class is receiving the packet.
     *
     * @param name the name of the class receiving the packet
     * @param packet the DatagramPacket containing data
     */
    public void printReceiveMessage(String name, DatagramPacket packet) {
        // Form a String from the byte array.
        Object object = decodeObject(packet.getData());
        if (!(object instanceof String)) {
            String messageToPrint = LocalTime.now().toString() + "\n";
            messageToPrint += name + " received ";
            if (packet.getPort() == Port.CLIENT_TO_SERVER.getNumber() || packet.getPort() == Port.SERVER_TO_CLIENT.getNumber()) {
                messageToPrint += "from Scheduler:";
            } else if (packet.getPort() == Port.CLIENT.getNumber()) {
                messageToPrint += "from Client:";
            } else if (packet.getPort() == Port.SERVER.getNumber()) {
                messageToPrint += "from Server:";
            }
            messageToPrint += "\n";
            messageToPrint += object.getClass().getSimpleName() + " Packet: ";
            messageToPrint += "Host port: " + packet.getPort();
            messageToPrint += ", Length: " + packet.getLength() + "\n";
            messageToPrint += object + "\n";
            System.out.println(messageToPrint);
        }
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

    /**
     * Encodes the object into a Byte Array, which can be used to prepare
     * requests to be sent through UDP packets.
     *
     * @param object the object to encode
     * @return objectBytes the object coded into a byte array.
     */
    public byte[] encodeObject(Object object) {
        byte[] objectBytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            objectBytes = bos.toByteArray();
        } catch (Exception ex) {
            // ignore exception
        }
        return objectBytes;
    }

    /**
     * Decodes the Byte Array to its object instance, which can be used to read
     * requests received from UDP packets.
     *
     * @param objectBytes the byte array of the object
     * @return object the object instance decoded.
     */
    public Object decodeObject(byte[] objectBytes) {
        Object object = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            object = in.readObject();
        } catch (Exception ex) {
            // ignore exception
        }
        if (object == null){
            object = new String(objectBytes);
        }
        return object;
    }
}
