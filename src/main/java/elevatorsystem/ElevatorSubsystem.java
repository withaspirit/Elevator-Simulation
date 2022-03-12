package elevatorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import requests.*;
import scheduler.ElevatorMonitor;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SubsystemMessagePasser, SystemEventListener {

    private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
    private final ArrayList<Elevator> elevatorList;
    private final ArrayList<ElevatorMonitor> elevatorMonitorList;
    private Client server;
    private final Queue<SystemEvent> requestQueue;
    private Origin origin;

    /**
     * Constructor for ElevatorSubsystem.
     *
     * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
     */
    public ElevatorSubsystem(BoundedBuffer buffer) {
        this.elevatorSubsystemBuffer = buffer;
        elevatorList = new ArrayList<>();
        elevatorMonitorList = new ArrayList<>();
        requestQueue = new LinkedList<>();
        origin = Origin.ELEVATOR_SYSTEM;
    }

    /**
     * Constructor for ElevatorSubsystem.
     */
    public ElevatorSubsystem() {
        elevatorSubsystemBuffer = null;
        server = new Client(Port.SERVER.getNumber());
        elevatorList = new ArrayList<>();
        elevatorMonitorList = new ArrayList<>();
        requestQueue = new LinkedList<>();
    }

    /**
     * Simple message requesting and sending between subsystems.
     * ElevatorSubsystem
     * Sends: ApproachEvent
     * Receives: ApproachEvent, ElevatorRequest
     */
    public void run() {
        if (server != null) {
            subsystemUDPMethod();
        } else {
            subsystemBufferMethod();
        }
    }

    /**
     * Adds an elevator to the subsystem's list of elevators.
     *
     * @param elevator an elevator
     */
    public void addElevator(Elevator elevator) {
        elevatorList.add(elevator);
    }

    /**
     * Passes an ApproachEvent between a Subsystem component and the Subsystem.
     *
     * @param approachEvent the approach event for the system
     */
    @Override
    public void handleApproachEvent(ApproachEvent approachEvent) {
        requestQueue.add(approachEvent);
    }

    /**
     * Sends and receives messages for system using UDP packets.
     */
    private void subsystemUDPMethod() {
        while (true) {
            Object object;
            if (!requestQueue.isEmpty()) {
                object = server.sendAndReceiveReply(requestQueue.remove());
            } else {
                object = server.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());
            }


            if (object instanceof ElevatorRequest elevatorRequest) {
                int chosenElevator = chooseElevator(elevatorRequest);
                // Choose elevator
                // Move elevator
                System.err.println(chosenElevator + " expected");
                Elevator elevator = elevatorList.get(chosenElevator - 1);
                elevator.addRequest(elevatorRequest);
                elevatorMonitorList.get(chosenElevator - 1).updateMonitor(elevator.makeStatusUpdate());
                requestQueue.add(elevator.makeStatusUpdate());
            } else if (object instanceof ApproachEvent approachEvent) {
                elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
            } else if (object instanceof String string) {
                if (string.trim().equals(RequestMessage.EMPTYQUEUE.getMessage())) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Sends and receives messages for the system using the BoundedBuffer.
     */
    private void subsystemBufferMethod() {
        while (true) {
            if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
                SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
                if (request instanceof ElevatorRequest elevatorRequest) {
                    int chosenElevator = chooseElevator(elevatorRequest);
                    // Choose elevator
                    // Move elevator
                    Elevator elevator = elevatorList.get(chosenElevator - 1);
                    elevator.addRequest(elevatorRequest);
                    requestQueue.add(elevator.makeStatusUpdate());
                } else if (request instanceof ApproachEvent approachEvent) {
                    elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
                }
            }
            // send message if possible
            if (!requestQueue.isEmpty()) {
                SystemEvent request = requestQueue.remove();
                sendMessage(request, elevatorSubsystemBuffer, origin);
            }
        }
    }

    /**
     * Returns an elevator number corresponding to an elevator that is
     * best suited to perform the given ElevatorRequest based on
     * expected time to fulfill the request and direction of elevator.
     *
     * @param elevatorRequest an ElevatorRequest
     * @return a number corresponding to an elevator
     */
    public int chooseElevator(ElevatorRequest elevatorRequest) {

        double elevatorBestExpectedTime = 0.0;
        // Best elevator is an elevator traveling in path that collides with request floor
        double elevatorOkExpectedTime = 0.0;
        // Ok elevator is an elevator that is traveling in the other direction
        double elevatorWorstExpectedTime = 0.0;
        // Worst elevator is an elevator that is traveling in the same direction but missed the request
        int chosenBestElevator = 0;
        int chosenOkElevator = 0;
        int chosenWorstElevator = 0;
        for (ElevatorMonitor monitor : elevatorMonitorList) {

            MovementState state = monitor.getState();
            Direction requestDirection = elevatorRequest.getDirection();
            double tempExpectedTime = monitor.getQueueTime();
            int currentFloor = monitor.getCurrentFloor();
            int desiredFloor = elevatorRequest.getDesiredFloor();
            int elevatorNumber = monitor.getElevatorNumber();

            if (state == MovementState.IDLE) {
                System.err.println(elevatorNumber + " is idle");
                return elevatorNumber;

            } else if (state == MovementState.STUCK) {
                System.err.println("Elevator is stuck");

            } else if (monitor.getDirection() == requestDirection) {
                if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
                    if (requestDirection == Direction.DOWN && currentFloor > desiredFloor) {
                        //check if request is in path current floor > directed floor going down
                        elevatorBestExpectedTime = tempExpectedTime;
                        chosenBestElevator = elevatorNumber;

                    } else if (requestDirection == Direction.UP && currentFloor < desiredFloor) {
                        //check if request is in path current floor < directed floor going up
                        elevatorBestExpectedTime = tempExpectedTime;
                        chosenBestElevator = elevatorNumber;

                    } else if (elevatorOkExpectedTime == 0 || elevatorOkExpectedTime > tempExpectedTime) {
                        //if request is in the correct direction but not in path of elevator
                        elevatorWorstExpectedTime = tempExpectedTime;
                        chosenWorstElevator = elevatorNumber;
                    }
                }
            } else {
                if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
                    //if the elevator traveling in the wrong direction
                    elevatorOkExpectedTime = tempExpectedTime;
                    chosenOkElevator = elevatorNumber;
                }
            }
        }
        if (chosenBestElevator == 0) {
            if (chosenOkElevator == 0) {
                chosenBestElevator = chosenWorstElevator;
            } else {
                chosenBestElevator = chosenOkElevator;
            }
        }
        return chosenBestElevator;
    }


    /**
     * Initialize the elevatorSubsystem with elevators
     * and start threads for each elevator and the elevatorSubsystem.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int numberOfElevators = 2;
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
        ArrayList<Elevator> elevatorList = new ArrayList<>();
        for (int elevatorNumber = 1; elevatorNumber <= numberOfElevators; elevatorNumber++) {
            Elevator elevator = new Elevator(elevatorNumber, elevatorSubsystem);
            elevatorSubsystem.addElevator(elevator);
            elevatorList.add(elevator);
        }
        new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName()).start();

        // Start elevator Origins
        for (int i = 0; i < numberOfElevators; i++) {
            (new Thread(elevatorList.get(i), elevatorList.get(i).getClass().getSimpleName())).start();
        }
    }
}
