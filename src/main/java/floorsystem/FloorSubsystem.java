package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import requests.*;
import systemwide.InputFileReader;
import systemwide.Structure;
import systemwide.SystemStatus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, SystemEventListener {

    private final Client client;
    private final ArrayList<SystemEvent> eventList;
    private final ArrayList<SystemEvent> requestList;
    private final ArrayList<Floor> floorList;
    private final SystemStatus systemStatus;
    private long startTime;
    private long delayToSendRequest;

    /**
     * Constructor for FloorSubsystem.
     */
    public FloorSubsystem() {
        client = new Client(Port.CLIENT.getNumber());
        InputFileReader inputFileReader = new InputFileReader();
        requestList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
        eventList = new ArrayList<>();
        floorList = new ArrayList<>();
        systemStatus = new SystemStatus(false);
        delayToSendRequest = 0;
        startTime = -1;
    }

    /**
     * Simple message requesting and sending between subsystems.
     * FloorSubsystem
     * Sends: ApproachEvent, ElevatorRequest
     * Receives: ApproachEvent
     */
    @Override
    public void run() {
        Collections.reverse(requestList);

        systemStatus.setSystemActivated(true);
        while (systemStatus.activated()) {
            subsystemUDPMethod();
        }
        System.out.println(getClass().getSimpleName() + " Thread terminated");
    }

    /**
     * Processes an ApproachEvent, checking its corresponding floor to see whether
     * an Elevator should stop.
     *
     * @param approachEvent the ApproachEvent used to determine whether the Elevator should stop
     */
    public void processApproachEvent(ApproachEvent approachEvent) {
        Floor floor = floorList.get(approachEvent.getFloorNumber() - 1);
        floor.receiveApproachEvent(approachEvent);
        eventList.add(approachEvent);
    }

    /**
     * Adds a floor to the FloorSubsystem's list of floors.
     *
     * @param floor a floor in the FloorSubsystem
     */
    public void addFloor(Floor floor) {
        floorList.add(floor);
    }

    /**
     * Adds a SystemEvent to a System's queue of events.
     *
     * @param systemEvent the SystemEvent to add
     */
    @Override
    public void addEventToQueue(SystemEvent systemEvent) {
        eventList.add(systemEvent);
    }

    /**
     * Gets the size of the event list.
     *
     * @return the number of events in the event list
     */
    public int getEventListSize() {
        return eventList.size();
    }

    /**
     * Adds a SystemEvent to the FloorSubsystem.
     *
     * @param systemEvent a SystemEvent originating from the FloorSubsystem
     */
    public void addEvent(SystemEvent systemEvent) {
        eventList.add(systemEvent);
    }

    /**
     * Returns the size of the list of requests.
     *
     * @return size of the list of requests
     */
    public int getRequestListSize() {
        return requestList.size();
    }

    /**
     * Adds a ServiceRequest to the list of Requests.
     *
     * @param serviceRequest serviceRequest to be added to the list of requests.
     */
    public void addRequest(ServiceRequest serviceRequest) {
        requestList.add(serviceRequest);
    }

    /**
     * Gets the SystemStatus of the System.
     *
     * @return the SystemStatus of the System
     */
    public SystemStatus getSystemStatus() {
        return systemStatus;
    }

    /**
     * Indicates whether the delay time to send an ElevatorRequest has been passed.
     *
     * @return true if the system can send a request, false otherwise
     */
    public boolean delayTimeElapsed() {
        // convert from nanoSeconds to milliseconds
        return (System.nanoTime() - startTime) * Math.pow(10, -6) > delayToSendRequest;
    }

    /**
     * Sends and receives messages for the system using UDP packets.
     */
    private void subsystemUDPMethod() {
        if (!requestList.isEmpty() && delayTimeElapsed()) {
            SystemEvent request = requestList.remove(requestList.size() - 1);
            // update request's time to now
            request.setTime(LocalTime.now());
            client.sendAndReceiveReply(request);
            startTime = System.nanoTime();
        } else if (!eventList.isEmpty()) {
            client.sendAndReceiveReply(eventList.remove(eventList.size() - 1));
        } else {
            Object object = client.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());

            if (object instanceof ApproachEvent approachEvent) {
                processApproachEvent(approachEvent);
            } else if (object instanceof ElevatorRequest elevatorRequest) {
                requestList.add(elevatorRequest);
            } else if (object instanceof String string) {
                if (string.trim().equals(RequestMessage.EMPTYQUEUE.getMessage())) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (string.trim().equals(RequestMessage.TERMINATE.getMessage())) {
                    systemStatus.setSystemActivated(false);
                }
            }
        }
    }

    /**
     * Initializes the specified number of Floors and the time the
     * FloorSubsystem waits before sending another request.
     *
     * @param structure contains information about the system
     */
    public void initializeFloors(Structure structure) {
        for (int i = 1; i <= structure.getNumberOfFloors(); i++) {
            Floor floor = new Floor(i, this);
            this.addFloor(floor);
        }
        // if possible, start the timer and delay for sending events
        int elevatorTime = structure.getElevatorTime();
        int doorsTime = structure.getDoorsTime();
        // FIXME: unclear if these are sound conditions
        if (elevatorTime > 0 && doorsTime > 0) {
            delayToSendRequest = (long) (elevatorTime + doorsTime) / 5 + 100;
            startTime = System.nanoTime();
        }
    }

    /**
     * Returns the list of Floors in the FloorSubystem.
     *
     * @return the list of Floors
     */
    public ArrayList<Floor> getFloorList() {
        return floorList;
    }

    /**
     * Receives and returns a Structure from the Scheduler.
     *
     * @return Structure contains information to initialize the floors and elevators
     */
    @Override
    public Structure receiveStructure() {
        return (Structure) client.receive();
    }

    public static void main(String[] args) {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        Structure structure = floorSubsystem.receiveStructure();

        floorSubsystem.initializeFloors(structure);
        System.out.println("Floors initialized");
        Thread floorSubsystemThread = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());
        floorSubsystemThread.start();
    }
}
