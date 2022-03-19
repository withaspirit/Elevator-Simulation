package scheduler;

import client_server_host.MessageTransfer;
import client_server_host.Port;
import client_server_host.RequestMessage;
import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import elevatorsystem.MovementState;
import org.junit.jupiter.api.*;
import requests.ElevatorMonitor;
import requests.ElevatorRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevatorSelectionTest {

    static MessageTransfer messageTransfer;
    static ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 1, Direction.UP, 2, Origin.FLOOR_SYSTEM);
    static byte[] messageElevator;
    static byte[] messageString;
    static DatagramPacket elevatorPacket;
    static DatagramPacket messagePacket;
    static ElevatorSubsystem elevatorSubsystem;
    static Elevator elevator1;
    static Elevator elevator2;
    static Scheduler schedulerClient;
    static Scheduler schedulerServer;
    static Thread schedulerClientThread;
    static Thread schedulerServerThread;
    static Thread elevatorSubsystemThread;

    @BeforeAll
    static void oneSetUp() {
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
        schedulerClientThread = new Thread(schedulerClient, schedulerClient.getClass().getSimpleName());
        schedulerServerThread = new Thread(schedulerServer, schedulerServer.getClass().getSimpleName());
        elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
        schedulerClientThread.start();
        schedulerServerThread.start();
        elevatorSubsystemThread.start();
    }

    @AfterEach
    void cleanUP(){
        while (!elevator1.getRequestQueue().isEmpty()){
            elevator1.getRequestQueue().removeRequest();
        }

        while (!elevator2.getRequestQueue().isEmpty()){
            elevator2.getRequestQueue().removeRequest();
        }
        elevator1.getMotor().setMovementState(MovementState.IDLE);
        elevator1.getMotor().setMovementState(MovementState.IDLE);
    }

    @Test
    void testSelectingIdleElevators() {
        //Both elevator's status' are idle
        Assertions.assertEquals(elevator1.getMotor().getMovementState(), MovementState.IDLE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.IDLE);

        //Both elevators expected time to completion with new requests are 0.0
        assertEquals(elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor()), 0.0);
        assertEquals(elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor()), 0.0);

        ElevatorMonitor monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(monitor.getState(), MovementState.ACTIVE);

        monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(monitor.getState(), MovementState.ACTIVE);

        //Both elevator status' are now active
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.ACTIVE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.ACTIVE);

        //Both elevators expected time to completion with new requests is now 16.67268228800782
        assertEquals(14.57185228514697, elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor()));
        assertEquals(14.57185228514697, elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor()));
    }

    @Test
    void testSelectingActiveElevators(){
        ElevatorMonitor monitor = sendReceiveMonitor();
        assertEquals(monitor.getElevatorNumber(), 1);

        monitor = sendReceiveMonitor();
        System.err.println(monitor.getQueueTime());

        //Both elevator status' are now active
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.ACTIVE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.ACTIVE);

        sendReceiveMonitor();
        sendReceiveMonitor();

        //Elevators expected completion times have increased by 19 seconds
        //Both elevators expected time to completion with new requests is now 19.0
        assertEquals(43.71555685544091, elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor()));
        assertEquals(43.71555685544091, elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor()));
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
