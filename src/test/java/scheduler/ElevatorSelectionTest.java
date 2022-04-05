package scheduler;

import client_server_host.MessageTransfer;
import client_server_host.Port;
import client_server_host.RequestMessage;
import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import elevatorsystem.MovementState;
import misc.InputFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import requests.SystemEvent;
import systemwide.Direction;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorSelectionTest {

    static MessageTransfer messageTransfer;
    static byte[] messageElevator, messageString;
    static DatagramPacket elevatorPacket, messagePacket;
    static ElevatorSubsystem elevatorSubsystem;
    static Elevator elevator1, elevator2;
    static Scheduler schedulerClient, schedulerServer;
    static Thread schedulerClientThread, schedulerServerThread, elevatorSubsystemThread;
    private final InputFileReader inputFileReader = new InputFileReader();
    private final ArrayList<SystemEvent> eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);

    @BeforeAll
    static void oneSetUp() {
        // Create a fake Client
        messageTransfer = new MessageTransfer(Port.CLIENT.getNumber());

        // Create a REQUEST message to send
        messageString = messageTransfer.encodeObject(RequestMessage.REQUEST.getMessage());
        try {
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
            if (elevator1.getRequestQueue().removeRequest() == null){
                break;
            }
        }

        while (!elevator2.getRequestQueue().isEmpty()){
            if (elevator2.getRequestQueue().removeRequest() == null){
                break;
            }
        }
        elevator1.getMotor().setMovementState(MovementState.IDLE);
        elevator2.getMotor().setMovementState(MovementState.IDLE);


        for (ElevatorMonitor elevatorMonitor: Scheduler.getElevatorMonitorList()){
            elevatorMonitor.updateMonitor(new ElevatorMonitor(elevatorMonitor.getElevatorNumber(), 1, Direction.UP, MovementState.IDLE, true, 0.0));
        }
    }

    @Test
    void testSelectingIdleElevators() {
        //Both elevator's status' are idle
        assertTrue(elevator1.getRequestQueue().isEmpty());
        assertTrue(elevator2.getRequestQueue().isEmpty());

        //Both elevators expected time to completion with new requests are 0.0
        assertEquals(elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor()), 0.0);
        assertEquals(elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor()), 0.0);

        ElevatorMonitor monitor = sendReceiveMonitor(eventList.get(0));
        assertEquals(monitor.getElevatorNumber(), 1);
        assertFalse(monitor.getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(14.57185228514697, monitor.getQueueTime());
        // Elevator move from floor 1 to 2 elevator was idle

        monitor = sendReceiveMonitor(eventList.get(1));
        assertEquals(monitor.getElevatorNumber(), 2);
        assertFalse(monitor.getHasNoRequests());
        assertFalse(elevator2.getRequestQueue().isEmpty());
        assertEquals(31.24453457315479, monitor.getQueueTime());
        // Elevator move from floor 2 to 4 elevator was idle
    }

    @Test
    void testSelectingActiveElevators(){
        ElevatorMonitor monitor = sendReceiveMonitor(eventList.get(0));
        assertEquals(monitor.getElevatorNumber(), 1);
        assertFalse(monitor.getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(14.57185228514697, monitor.getQueueTime());
        // Elevator 1 move from floor 1 to 2 elevator was idle

        monitor = sendReceiveMonitor(eventList.get(1));
        assertEquals(monitor.getElevatorNumber(), 2);
        assertFalse(monitor.getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(31.24453457315479, monitor.getQueueTime());
        // Elevator 2 move from floor 2 to 4 elevator was idle

        monitor = sendReceiveMonitor(eventList.get(2));
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(49.52924050879059, monitor.getQueueTime());
        // Elevator 2 move from floor 4 to 1 elevator 2 traveling to floor 4

        monitor = sendReceiveMonitor(eventList.get(3));
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(34.21555685544091, monitor.getQueueTime());
        // Elevator 1 move from floor 2 to 6 elevator 1 traveling to floor 2

        monitor = sendReceiveMonitor(eventList.get(4));
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(99.05848101758119, monitor.getQueueTime());
        // Elevator 2 move from floor 7 to 3 elevator 2 less time to complete its queue, so it was picked

        monitor = sendReceiveMonitor(eventList.get(5));
        assertEquals(monitor.getElevatorNumber(), 1);
        assertEquals(60.38823914344873, monitor.getQueueTime());
        // Elevator 1 move from floor 3 to 5 elevator 1 traveling passing floor 3 to destination floor 4 takes priority

        monitor = sendReceiveMonitor(eventList.get(6));
        assertEquals(monitor.getElevatorNumber(), 2);
        assertEquals(99.05848101758119, monitor.getQueueTime());
        // Elevator 2 traveling in same direction has higher priority and Elevator 2 has 3 and 1 in queue

        //Elevator 1 floor requests up [1, 2, 3, 5, 6]
        //Elevator 2 floor requests up [2, 4], down [7, 4, 3, 1]
    }

    /**
     * Sending a ElevatorRequest packet through its journey of the system
     * and receiving a packet back on the same socket.
     *
     * @return an ElevatorMonitor with updates elevator information
     */
    private ElevatorMonitor sendReceiveMonitor(SystemEvent event){
        messageElevator = messageTransfer.encodeObject(event);
        try {
            elevatorPacket = new DatagramPacket(messageElevator, messageElevator.length, InetAddress.getLocalHost(), Port.CLIENT_TO_SERVER.getNumber());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        messageTransfer.sendMessage(elevatorPacket);
        messageTransfer.receiveMessage();
        // Wait for Threads to finish transferring and processing requests before trying to get information
        boolean nullPacket = true;
        ElevatorMonitor monitor = null;
        while (nullPacket) {
            messageTransfer.sendMessage(messagePacket);
            Object object = messageTransfer.decodeObject(messageTransfer.receiveMessage().getData());
            if (object instanceof ElevatorMonitor elevatorMonitor) {
                monitor = elevatorMonitor;
                nullPacket = false;
            }
        }
        return monitor;
    }
}
