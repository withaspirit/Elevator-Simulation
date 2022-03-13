package requests;

import elevatorsystem.MovementState;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

/**
 * An elevator monitor for the scheduler to quickly decide on an elevator to send new
 * service requests with regularly updated elevator information.
 *
 * @author Ryan Dash
 * @version 2022/03/10
 */
public class ElevatorMonitor extends SystemEvent {

    private double queueTime;
    private MovementState state;
    private int currentFloor;
    private Direction currentDirection;

    /**
     * Main Constructor for ElevatorMonitor
     *
     * @param elevatorNumber the elevator number of the elevator that is being monitored for status changes
     */
    public ElevatorMonitor(int elevatorNumber) {
        super(LocalTime.now(), Origin.ELEVATOR_SYSTEM);
        setElevatorNumber(elevatorNumber);
        queueTime = 0.0;
        state = MovementState.IDLE;
        currentFloor = 0;
        currentDirection = Direction.NONE;
    }

    public ElevatorMonitor(double queueTime, MovementState movementState, int currentFloor, Direction serviceDirection, int elevatorNumber) {
        this(elevatorNumber);
        this.queueTime = queueTime;
        this.state = movementState;
        this.currentFloor = currentFloor;
        this.currentDirection = serviceDirection;
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
     * Update the ElevatorMonitor with the latest ElevatorMonitor information.
     *
     * @param elevatorMonitor an elevator monitor containing new elevator information
     */
    public void updateMonitor(ElevatorMonitor elevatorMonitor) {
        this.queueTime = elevatorMonitor.getQueueTime();
        this.state = elevatorMonitor.getState();
        this.currentFloor = elevatorMonitor.getCurrentFloor();
        this.currentDirection = elevatorMonitor.getDirection();
    }
}
