package scheduler;

import client_server_host.MessageTransfer;
import client_server_host.Port;
import client_server_host.RequestMessage;
import elevatorsystem.*;
import misc.InputFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import requests.SystemEvent;
import systemwide.Direction;
import systemwide.Structure;

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
    static ArrayList<ElevatorMonitor> monitorList;
    static Thread schedulerClientThread, schedulerServerThread, elevatorSubsystemThread;
    private final InputFileReader inputFileReader = new InputFileReader();
    private final ArrayList<SystemEvent> eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
    private final static int loadTIme = 100;
    private final static int travelTIme = 1000;

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

        for (Elevator elevator: elevatorSubsystem.getElevatorList()){
            elevator.setTravelTime(travelTIme);
            elevator.setDoorTime(loadTIme);
        }

        // Setup and start Scheduler Threads to send to ElevatorSubsystem
        schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
        schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());
        schedulerClient.addElevatorMonitor(elevator1.getElevatorNumber());
        schedulerClient.addElevatorMonitor(elevator2.getElevatorNumber());
        monitorList = Scheduler.getElevatorMonitorList();
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


        for (ElevatorMonitor elevatorMonitor: monitorList){
            elevatorMonitor.updateMonitor(new ElevatorMonitor(elevatorMonitor.getElevatorNumber(), 1, Direction.UP, MovementState.IDLE, Direction.NONE, Doors.State.OPEN, Fault.NONE, true, 0.0));
        }
    }

    /**
     * Sends an event to the scheduler.
     *
     * @param event a SystemEvent containing a request
     */
    private void sendEvent(SystemEvent event){
        messageElevator = messageTransfer.encodeObject(event);
        try {
            elevatorPacket = new DatagramPacket(messageElevator, messageElevator.length, InetAddress.getLocalHost(), Port.CLIENT_TO_SERVER.getNumber());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        messageTransfer.sendMessage(elevatorPacket);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSelectingIdleElevators() {
        //Both elevator's status' are idle
        assertTrue(elevator1.getRequestQueue().isEmpty());
        assertTrue(elevator2.getRequestQueue().isEmpty());

        //Both elevators expected time to completion with new requests are 0.0
        assertEquals(elevator1.getRequestQueue().getExpectedTime(elevator1.getCurrentFloor(), loadTIme, travelTIme), 0.0);
        assertEquals(elevator2.getRequestQueue().getExpectedTime(elevator2.getCurrentFloor(), loadTIme, travelTIme), 0.0);

        sendEvent(eventList.get(0));
        assertEquals(monitorList.get(0).getElevatorNumber(), 1);
        assertFalse(monitorList.get(0).getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(1.2, monitorList.get(0).getQueueTime());
        // Elevator move from floor 1 to 2 elevator was idle

        sendEvent(eventList.get(1));
        assertEquals(monitorList.get(1).getElevatorNumber(), 2);
        assertFalse(monitorList.get(1).getHasNoRequests());
        assertFalse(elevator2.getRequestQueue().isEmpty());
        assertEquals(3.4, monitorList.get(1).getQueueTime());
        // Elevator move from floor 2 to 4 elevator was idle
    }

    @Test
    void testSelectingActiveElevators(){
        sendEvent(eventList.get(0));
        assertEquals(monitorList.get(0).getElevatorNumber(), 1);
        assertFalse(monitorList.get(0).getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(1.2, monitorList.get(0).getQueueTime());
        // Elevator 1 move from floor 1 to 2 elevator was idle

        sendEvent(eventList.get(1));
        assertEquals(monitorList.get(1).getElevatorNumber(), 2);
        assertFalse(monitorList.get(1).getHasNoRequests());
        assertFalse(elevator1.getRequestQueue().isEmpty());
        assertEquals(3.4, monitorList.get(1).getQueueTime());
        // Elevator 2 move from floor 2 to 4 elevator was idle

        sendEvent(eventList.get(2));
        assertEquals(monitorList.get(1).getElevatorNumber(), 2);
        assertEquals(6.6, monitorList.get(1).getQueueTime());
        // Elevator 2 move from floor 4 to 1 elevator 2 traveling to floor 4

        sendEvent(eventList.get(3));
        assertEquals(monitorList.get(0).getElevatorNumber(), 1);
        assertEquals(5.4, monitorList.get(0).getQueueTime());
        // Elevator 1 move from floor 2 to 6 elevator 1 traveling to floor 2

        sendEvent(eventList.get(4));
        assertEquals(monitorList.get(1).getElevatorNumber(), 2);
        assertEquals(13.2, monitorList.get(1).getQueueTime());
        // Elevator 2 move from floor 7 to 3 elevator 2 less time to complete its queue, so it was picked

        sendEvent(eventList.get(5));
        assertEquals(monitorList.get(0).getElevatorNumber(), 1);
        assertEquals(5.8, monitorList.get(0).getQueueTime());
        // Elevator 1 move from floor 3 to 5 elevator 1 traveling passing floor 3 to destination floor 4 takes priority

        sendEvent(eventList.get(6));
        assertEquals(monitorList.get(1).getElevatorNumber(), 2);
        assertEquals(13.2, monitorList.get(1).getQueueTime());
        // Elevator 2 traveling in same direction has higher priority and Elevator 2 has 3 and 1 already in queue
    }
}
