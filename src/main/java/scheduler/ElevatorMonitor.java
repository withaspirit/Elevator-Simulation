package scheduler;

import elevatorsystem.MovementState;
import requests.StatusUpdate;
import systemwide.Direction;

/**
 * An elevator monitor for the scheduler to quickly decide on an elevator to send new
 * service requests with regularly updated elevator information.
 *
 * @author Ryan Dash
 * @version 2022/03/10
 */
public class ElevatorMonitor {

    private int elevatorNumber;
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
        this.elevatorNumber = elevatorNumber;
        queueTime = 0.0;
        state = MovementState.IDLE;
        currentFloor = 0;
        currentDirection = Direction.NONE;
    }

    /**
     * Updates the elevator information of the elevator monitor.
     *
     * @param statusUpdate a StatusUpdate request containing new elevator information
     */
    public void updateMonitor(StatusUpdate statusUpdate) {
        queueTime = statusUpdate.getExpectedTime();
        state = statusUpdate.getState();
        currentFloor = statusUpdate.getCurrentFloor();
        currentDirection = statusUpdate.getDirection();
    }

    /**
     * Gets the direction of the elevator.
     *
     * @return the latest direction of the elevator
     */
    public int getElevatorNumber() {
        return elevatorNumber;
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
}
