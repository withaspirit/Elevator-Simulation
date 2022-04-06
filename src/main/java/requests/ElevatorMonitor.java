package requests;

import elevatorsystem.Doors;
import elevatorsystem.Fault;
import elevatorsystem.MovementState;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

/**
 * ElevatorMonitor retains Elevator information to allow the Scheduler to quickly decide
 * which Elevator to send new ServiceRequests to. Scheduler's list of ElevatorMonitors is
 * updated by Elevator sending ElevatorMonitors to Scheduler.
 *
 * @author Ryan Dash, Brady Norton
 * @version 2022/04/05
 */
public class ElevatorMonitor extends SystemEvent {

    private int currentFloor;
    private Direction currentDirection;
    private MovementState state;
    private Direction movementDirection;
    private Doors.State doorsState;
    private Fault fault;
    private boolean hasNoRequests;
    private double queueTime;

    /**
     * Main Constructor for ElevatorMonitor.
     *
     * @param elevatorNumber the elevator number of the elevator that is being monitored for status changes
     */
    public ElevatorMonitor(int elevatorNumber) {
        super(LocalTime.now(), Origin.ELEVATOR_SYSTEM);
        setElevatorNumber(elevatorNumber);
        queueTime = 0.0;
        state = MovementState.IDLE;
        currentFloor = 1;
        currentDirection = Direction.NONE;
        hasNoRequests = true;
    }

    /**
     * Constructor for all of ElevatorMonitor's properties.
     *
     * @param elevatorNumber the number of the elevator
     * @param currentFloor the currentFloor of the Elevator
     * @param serviceDirection the direction that the elevator is serving
     * @param movementState the MovementState of the Elevator's motor
     * @param movementDirection
     * @param doorState
     * @param fault
     * @param queueTime the estimated time for elevator to fulfill all of its requests
     */
    public ElevatorMonitor(int elevatorNumber, int currentFloor, Direction serviceDirection, MovementState movementState, Direction movementDirection, Doors.State doorState, Fault fault, Boolean empty, double queueTime) {
        this(elevatorNumber);
        this.currentFloor = currentFloor;
        this.currentDirection = serviceDirection;
        this.state = movementState;
        this.movementDirection = movementDirection;
        this.doorsState = doorState;
        this.fault = fault;
        this.hasNoRequests = empty;
        this.queueTime = queueTime;
    }

    /**
     * Gets the queue time for the elevator.
     *
     * @return the queueTime for the elevator
     */
    public double getQueueTime() {
        return queueTime;
    }

    /**
     * Gets the MovementState of the elevator.
     *
     * @return the movement state of the elevator
     */
    public MovementState getState() {
        return state;
    }

    /**
     * Gets the current floor of the elevator.
     *
     * @return the current floor of the elevator
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Gets the current direction of the elevator.
     *
     * @return the current direction of the elevator
     */
    public Direction getDirection() {
        return currentDirection;
    }


    /**
     * Gets whether of not the elevator has requests.
     *
     * @return true if the elevator has no requests, false otherwise
     */
    public boolean getHasNoRequests() {
        return hasNoRequests;
    }

    /**
     * Updates the ElevatorMonitor with the latest ElevatorMonitor information.
     *
     * @param elevatorMonitor an elevator monitor containing new elevator information
     */
    public void updateMonitor(ElevatorMonitor elevatorMonitor) {
        this.queueTime = elevatorMonitor.getQueueTime();
        this.state = elevatorMonitor.getState();
        this.currentFloor = elevatorMonitor.getCurrentFloor();
        this.currentDirection = elevatorMonitor.getDirection();
        this.hasNoRequests = elevatorMonitor.getHasNoRequests();
    }

    /**
     * Returns a string representation of the elevator monitor's information.
     *
     * @return a string containing elevator monitor's information
     */
    @Override
    public String toString() {
        String formattedString = "[ElevatorNumber, queueTime, MovementState, CurrFloor, CurrDirxn]:\n";
        formattedString += getElevatorNumber() + " " + String.format("%.2f", getQueueTime()) + " " + getState().toString() + " " + getCurrentFloor() + " " + getDirection();
        return formattedString;
    }

    public String[] propertiesToStringArray() {
        String[] properties = new String[6];
        //{"CurrentFloor", "ServiceDirection", "MovementState", "MovementDirection", "DoorState", "Fault"};
        properties[0] = String.valueOf(getCurrentFloor());
        properties[1] = getDirection().toString();
        properties[2] = state.getName();
        properties[3] = movementDirection.getName();
        properties[4] = doorsState.toString();
        properties[5] = fault.getName();

        return properties;
    }
}
