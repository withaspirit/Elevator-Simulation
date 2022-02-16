package elevatorsystem;

import requests.ElevatorRequest;
import systemwide.Direction;

import java.util.*;

/**
 * Elevator is a model for simulating an elevator.
 *
 * @author Liam Tripp, Ryan Dash
 */
public class Elevator {

    private final int elevatorNumber;
    private final ElevatorMotor elevatorMotor;
    private final int currentFloor;
    private Direction currentDirection;
    private final ArrayDeque<Integer> queueDown;
    private final ArrayDeque<Integer> queueUp;
    private double queueTime;
    private final double MAX_SPEED = 2.67;
    private final double ACCELERATION = 0.152;
    private final double ACCELERATION_TIME = 1.12; //TODO need to determine time of acceleration
    private final double ACCELERATION_DISTANCE = 3.0; //TODO need to determine distance at which elevator stops accelerating
    private final double LOADING_TIME = 9.50;
    private final double floorSeparation = 4.0;

	/**
	 * Main Constructor for Elevator Class.
	 *
	 * @param elevatorNumber an integer corresponding to the elevator's number
	 */
    public Elevator(int elevatorNumber) {
        this.elevatorNumber = elevatorNumber;
        elevatorMotor = new ElevatorMotor();
        currentFloor = 0;
        currentDirection = Direction.STOP;
        queueDown = new ArrayDeque<>();
        queueUp = new ArrayDeque<>();
        queueTime = 0;
    }

    /**
     * Gets the elevator number of the elevator
     *
     * @return the elevator number corresponding to the elevator.
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    /**
     * Gets the current elevator's moving direction.
     *
     * @return a Direction corresponding to the elevator's moving direction
     */
    public Direction getCurrentDirection() {
        return currentDirection;
    }


    /**
     * Adds the expected time it will take for the elevator to perform the
	 * elevator request to the queueTime and adds a request to the queue.
     *
     * @param elevatorRequest an elevator request from the floorSubsystem
     */
    public void addRequest(ElevatorRequest elevatorRequest) {
        queueTime = getExpectedTime(elevatorRequest);
        if (queueDown.isEmpty() && queueUp.isEmpty()){
            currentDirection = elevatorRequest.getDirection();
        }

        int tempDesiredFloor = elevatorRequest.getDesiredFloor();
        if (elevatorRequest.getDirection() == Direction.UP) {
            if (currentDirection == Direction.UP && !queueUp.isEmpty()){
                if (tempDesiredFloor < queueUp.peek() && tempDesiredFloor > currentFloor){
                    queueUp.addFirst(tempDesiredFloor);
                }
            }
            queueUp.addLast(tempDesiredFloor);
        } else if (elevatorRequest.getDirection() == Direction.DOWN) {
            if (currentDirection == Direction.DOWN && !queueDown.isEmpty()){
                if (tempDesiredFloor < currentFloor && tempDesiredFloor > queueDown.peek()){
                    queueDown.addFirst(tempDesiredFloor);
                }
            }
            queueDown.addLast(tempDesiredFloor);
        } else {
            System.err.println("Invalid Direction in elevator request");
        }
        System.out.println("\nElevator #" + elevatorNumber + " QueueUP# "+ queueUp.size()+ " QueueDOWN# "+ queueDown.size()+"\n");
        elevatorMotor.setMovementState(MovementState.ACTIVE);
    }

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorRequest an elevator request from the floorSubsystem
	 * @return a double containing the elevator's total expected queue time
	 */
    public double getExpectedTime(ElevatorRequest elevatorRequest) {
		return queueTime + LOADING_TIME + requestTime(elevatorRequest);
    }

    /**
     * Gets the expected time of a new request for the current elevator
     * based on distance.
     *
     * @param elevatorRequest an elevatorRequest from a floor
     * @return a double containing the time to fulfil the request
     */
    public double requestTime(ElevatorRequest elevatorRequest) {
        double distance = Math.abs(elevatorRequest.getDesiredFloor() - currentFloor) * floorSeparation;
        if (distance > ACCELERATION_DISTANCE * 2) {
            return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
        } else {
            return Math.sqrt(distance * 2 / ACCELERATION);
        }
    }

    /**
     * Gets the current elevator's floor number.
     *
     * @return the current floor number of the elevator
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    public MovementState getState() {
        return elevatorMotor.getMovementState();
    }
}
