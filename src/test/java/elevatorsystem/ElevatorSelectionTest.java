package elevatorsystem;

import client_server_host.MessageTransfer;
import client_server_host.Port;
import client_server_host.RequestMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import requests.ElevatorRequest;
import scheduler.Scheduler;
import systemwide.Direction;
import systemwide.Origin;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ElevatorSelectionTest {

    MessageTransfer messageTransfer;
    ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 0, Direction.UP, 2, Origin.FLOOR_SYSTEM);
    byte[] messageElevator;
    byte[] messageString;
    DatagramPacket elevatorPacket;
    DatagramPacket messagePacket;
    ElevatorSubsystem elevatorSubsystem;
    Elevator elevator1, elevator2;
    Scheduler schedulerClient;
    Scheduler schedulerServer;
    Thread schedulerClientThread, schedulerServerThread, elevatorSubsystemThread;

    @BeforeEach
    void setUp() {
        // Create a fake Client
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());
        // Create a Request to send
        messageElevator = messageTransfer.encodeObject(elevatorRequest);
        messageString = messageTransfer.encodeObject(RequestMessage.REQUEST.getMessage());
        try {
            elevatorPacket = new DatagramPacket(messageElevator, messageElevator.length, InetAddress.getLocalHost(), Port.CLIENT_TO_SERVER.getNumber());
            messagePacket = new DatagramPacket(messageString, messageString.length, InetAddress.getLocalHost(), Port.SERVER_TO_CLIENT.getNumber());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //Setup a ElevatorSubsystem
        elevatorSubsystem = new ElevatorSubsystem();
        elevator1 = new Elevator(1, elevatorSubsystem);
        elevator2 = new Elevator(2, elevatorSubsystem);
        elevatorSubsystem.addElevator(elevator1);
        elevatorSubsystem.addElevator(elevator2);

        // Setup and start Scheduler Threads to send to ElevatorSubsystem
        schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
        schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());
        schedulerClient.addElevatorMonitor(elevator1.getElevatorNumber());
        schedulerClient.addElevatorMonitor(elevator2.getElevatorNumber());
        schedulerClientThread = new Thread (schedulerClient, schedulerClient.getClass().getSimpleName());
        schedulerServerThread = new Thread (schedulerServer, schedulerServer.getClass().getSimpleName());
        elevatorSubsystemThread = new Thread (elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
        schedulerClientThread.start();
        schedulerServerThread.start();
        elevatorSubsystemThread.start();
    }

    @Test
    void testSelectingElevators() {
        //Test sending to idle elevators

        //Both elevator's status' are idle
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.IDLE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.IDLE);

        //Both elevators expected time to completion with new requests are 9.5
        assertEquals(elevator1.getExpectedTime(elevatorRequest), 9.5);
        assertEquals(elevator2.getExpectedTime(elevatorRequest), 9.5);

        ElevatorMonitor monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(monitor.getState(), MovementState.ACTIVE);

        monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(monitor.getState(), MovementState.ACTIVE);

        //Both elevator status' are now active
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.ACTIVE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.ACTIVE);

        //Both elevators expected time to completion with new requests is now 19.0
        assertEquals(elevator1.getExpectedTime(elevatorRequest), 19.0);
        assertEquals(elevator2.getExpectedTime(elevatorRequest), 19.0);

        //Test sending to active elevators

        monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 1);

        monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 2);

        //Elevators expected completion times have increased to 28.5
        assertEquals(elevator1.getExpectedTime(elevatorRequest), 28.5);
        assertEquals(elevator2.getExpectedTime(elevatorRequest), 28.5);
    }

    /**
     * Sending a ElevatorRequest packet through its journey of the system
     * and receiving a packet back on the same socket.
     *
     * @return an ElevatorMonitor with updates elevator information
     */
    private ElevatorMonitor sendReceiveMonitor(){
        messageTransfer.sendMessage(elevatorPacket);
        messageTransfer.receiveMessage();
        // Wait for Threads to finish transferring and processing requests
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageTransfer.sendMessage(messagePacket);
        Object object = messageTransfer.decodeObject(messageTransfer.receiveMessage().getData());
        if (object instanceof ElevatorMonitor elevatorMonitor){
            return elevatorMonitor;
        } else {
            return null; // Crash and burn
        }
    }
}
