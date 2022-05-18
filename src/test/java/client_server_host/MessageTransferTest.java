package client_server_host;

import elevatorsystem.Doors;
import elevatorsystem.Fault;
import elevatorsystem.MovementState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;
import systemwide.Direction;
import systemwide.Origin;
import systemwide.Structure;

import java.net.DatagramPacket;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTransferTest ensures the encoding/decoding and send/receive methods
 * provided by the MessageTransfer class are functional.
 *
 * @author Julian, Liam Tripp
 */
public class MessageTransferTest {

    private MessageTransfer msgTransfer;
    private ElevatorRequest elevatorRequest;
    private ServiceRequest serviceRequest;
    private ApproachEvent approachEvent;
    private ElevatorMonitor elevatorMonitor;
    private Structure structure;
    private LocalTime timeNow;
    private int portNumber;

    @BeforeEach
    void setUp() {
        timeNow = LocalTime.now();
        msgTransfer = new MessageTransfer(0);
        portNumber = msgTransfer.getPortNumber();
        elevatorRequest = new ElevatorRequest(timeNow, 2, Direction.UP, 4, Origin.FLOOR_SYSTEM);
        serviceRequest = new ServiceRequest(timeNow, 7, Direction.DOWN, Origin.SCHEDULER);
        serviceRequest.setElevatorNumber(0);
        approachEvent = new ApproachEvent(elevatorRequest, 3, 5);
        elevatorMonitor = new ElevatorMonitor(0, 1, Direction.UP, MovementState.IDLE, Direction.UP, Doors.State.OPEN, Fault.NONE, true, 0);
        structure = new Structure(22, 4, 1000, 1000);
    }

    @Test
    void testEncodingWithElevatorRequest() {
        byte[] requestIn;
        requestIn = msgTransfer.encodeObject(elevatorRequest);
        SystemEvent systemEventOut = (SystemEvent) msgTransfer.decodeObject(requestIn);

        //Test for correct class instance
        assertTrue(systemEventOut instanceof ElevatorRequest);

        //Test for proper attributes
        ElevatorRequest requestOut = (ElevatorRequest) systemEventOut;
        assertEquals(requestOut.getOrigin(), Origin.FLOOR_SYSTEM);
        assertEquals(requestOut.getTime(), timeNow);
        assertEquals(requestOut.getFloorNumber(), 2);
        assertEquals(requestOut.getDirection(), Direction.UP);
        assertEquals(requestOut.getDesiredFloor(), 4);
    }

    @Test
    void testEncodingWithServiceRequest() {
        byte[] requestIn;
        requestIn = msgTransfer.encodeObject(serviceRequest);
        SystemEvent systemEventOut = (SystemEvent) msgTransfer.decodeObject(requestIn);

        //Test for correct class instance
        assertTrue(systemEventOut instanceof ServiceRequest);

        //Test for proper attributes
        ServiceRequest requestOut = (ServiceRequest) systemEventOut;
        assertEquals(requestOut.getOrigin(), Origin.SCHEDULER);
        assertEquals(requestOut.getTime(), timeNow);
        assertEquals(requestOut.getFloorNumber(), 7);
        assertEquals(requestOut.getDirection(), Direction.DOWN);
        assertEquals(requestOut.getElevatorNumber(), 0);
    }

    @Test
    void testEncodingWithApproachEvent() {
        byte[] requestIn;
        requestIn = msgTransfer.encodeObject(approachEvent);
        SystemEvent systemEventOut = (SystemEvent) msgTransfer.decodeObject(requestIn);

        //Test for correct class instance
        assertTrue(systemEventOut instanceof ApproachEvent);

        //Test for proper attributes
        ApproachEvent requestOut = (ApproachEvent) systemEventOut;
        assertEquals(requestOut.getOrigin(), approachEvent.getOrigin());
        assertEquals(requestOut.getTime(), approachEvent.getTime());
        assertEquals(requestOut.getFloorNumber(), approachEvent.getFloorNumber());
        assertEquals(requestOut.getDirection(), approachEvent.getDirection());
        assertEquals(requestOut.getElevatorNumber(), approachEvent.getElevatorNumber());
    }

    @Test
    void testMaxByteArraySizeOfMessageNotExceeded() {
        int maxByteArraySize = MessageTransfer.MAX_BYTE_ARRAY_SIZE;

        byte[] byteArray = msgTransfer.encodeObject(approachEvent);
        assertTrue(byteArray.length < maxByteArraySize);

        byteArray = msgTransfer.encodeObject(elevatorRequest);
        assertTrue(byteArray.length < maxByteArraySize);

        byteArray = msgTransfer.encodeObject(serviceRequest);
        assertTrue(byteArray.length < maxByteArraySize);

        byteArray = msgTransfer.encodeObject(elevatorMonitor);
        assertTrue(byteArray.length < maxByteArraySize);

        byteArray = msgTransfer.encodeObject(structure);
        assertTrue(byteArray.length < maxByteArraySize);
    }

    @Test
    void sendAndReceivePacketBetweenTwoSockets() {
        MessageTransfer messageTransfer2 = new MessageTransfer(0);

        // send message
        byte[] byteArray = messageTransfer2.encodeObject(elevatorRequest);
        DatagramPacket packetToSend = messageTransfer2.createPacket(byteArray, portNumber);
        messageTransfer2.sendMessage(packetToSend);

        // receive message
        DatagramPacket receivePacket = msgTransfer.receiveMessage();
        Object object = msgTransfer.decodeObject(receivePacket.getData());

        // assert object has been transferred successfully
        assertTrue(object instanceof SystemEvent);
        assertTrue(object instanceof ElevatorRequest);
    }
}
