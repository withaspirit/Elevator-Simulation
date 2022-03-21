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
    static ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), 2, Direction.UP, 4, Origin.FLOOR_SYSTEM);
    static byte[] messageElevator, messageElevator2, messageString;
    static DatagramPacket elevatorPacket, elevatorPacket2,  messagePacket;
    static ElevatorSubsystem elevatorSubsystem;
    static Elevator elevator1, elevator2;
    static Scheduler schedulerClient, schedulerServer;
    static Thread schedulerClientThread, schedulerServerThread, elevatorSubsystemThread;

    @BeforeAll
    static void oneSetUp() {
        // Create a fake Client
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());

        // Create a Request to send
        messageElevator = messageTransfer.encodeObject(elevatorRequest);
        messageElevator2 = messageTransfer.encodeObject(elevatorRequest2);
        messageString = messageTransfer.encodeObject(RequestMessage.REQUEST.getMessage());
        try {
            elevatorPacket = new DatagramPacket(messageElevator, messageElevator.length, InetAddress.getLocalHost(), Port.CLIENT_TO_SERVER.getNumber());
            elevatorPacket2 = new DatagramPacket(messageElevator2, messageElevator2.length, InetAddress.getLocalHost(), Port.CLIENT_TO_SERVER.getNumber());
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
    void cleanUP(){;
        while (!elevator1.getRequestQueue().isEmpty()){
            elevator1.getRequestQueue().removeRequest();
        }

        while (!elevator2.getRequestQueue().isEmpty()){
            elevator2.getRequestQueue().removeRequest();
        }
        elevator1.getMotor().setMovementState(MovementState.IDLE);
        elevator2.getMotor().setMovementState(MovementState.IDLE);

        for (ElevatorMonitor elevatorMonitor: Scheduler.getElevatorMonitorList()){
            elevatorMonitor.updateMonitor(new ElevatorMonitor(0.0, MovementState.IDLE, 1, Direction.UP, elevatorMonitor.getElevatorNumber()));
        }
    }

    @Test
    void testSelectingIdleElevators() {
        //Both elevator's status' are idle
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.IDLE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.IDLE);

        //Both elevators expected time to completion with new requests are 0.0
        assertEquals(elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor()), 0.0);
        assertEquals(elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor()), 0.0);

        ElevatorMonitor monitor = sendReceiveMonitor(elevatorPacket);
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(monitor.getState(), MovementState.ACTIVE);
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.ACTIVE);
        assertEquals(14.57185228514697, monitor.getQueueTime());

        monitor = sendReceiveMonitor(elevatorPacket);
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(monitor.getState(), MovementState.ACTIVE);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.ACTIVE);
        assertEquals(14.57185228514697, monitor.getQueueTime());
    }

    @Test
    void testSelectingActiveElevators(){
        ElevatorMonitor monitor = sendReceiveMonitor(elevatorPacket);
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(elevator1.getMotor().getMovementState(), MovementState.ACTIVE);

        monitor = sendReceiveMonitor(elevatorPacket);
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(elevator2.getMotor().getMovementState(), MovementState.ACTIVE);

        monitor = sendReceiveMonitor(elevatorPacket2);
        assertEquals(31.24453457315479, monitor.getQueueTime());

        monitor = sendReceiveMonitor(elevatorPacket2);
        assertEquals(31.24453457315479, monitor.getQueueTime());
    }

    /**
     * Sending a ElevatorRequest packet through its journey of the system
     * and receiving a packet back on the same socket.
     *
     * @return an ElevatorMonitor with updates elevator information
     */
    private ElevatorMonitor sendReceiveMonitor(DatagramPacket packet){
        messageTransfer.sendMessage(packet);
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
