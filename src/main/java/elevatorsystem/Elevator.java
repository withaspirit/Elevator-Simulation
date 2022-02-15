package elevatorsystem;

import requests.ElevatorRequest;

import java.util.PriorityQueue;

/**
 * Elevator is a model for simulating an elevator.
 *
 * @author Liam Tripp
 */
public class Elevator {

    private int elevatorNumber;
    private int currentFloor;
    private MovementState state;
    private PriorityQueue<Integer> queue;
    private double queueTime;
    private final double MAX_SPEED = 2.67;
    private final double ACCELERATION = 0.152;
    private final int ACCELERATION_TIME = 0; //TODO need to determine time of acceleration
    private final int ACCELERATION_DISTANCE = 0; //TODO need to determine distance at which elevator stops accelerating
    private final double LOADING_TIME = 9.50;
    private final int floorSeparation = 4;

    // private Direction direction;

	/**
	 * Main Constructor for Elevator Class.
	 *
	 * @param elevatorNumber an integer corresponding to the elevator's number
	 */
    public Elevator(int elevatorNumber) {
        this.elevatorNumber = elevatorNumber;
        currentFloor = 0;
        state = MovementState.IDLE;
        queue = new PriorityQueue<>();
        queueTime = 0;
    }

    /**
     * Gets the state of the elevator
     *
     * @return the current state of the elevator.
     */
    public MovementState getState() {
        return state;
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
     * Adds the expected time it will take for the elevator to perform the
	 * elevator request to the queueTime and adds a request to the queue.
     *
     * @param elevatorRequest an elevator request from the floorSubsystem
     */
    public void addRequest(ElevatorRequest elevatorRequest) {
        queueTime = getExpectedTime(elevatorRequest);
        queue.add(elevatorRequest.getDesiredFloor());
    }

	/**
	 * Returns the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorRequest an elevator request from the floorSubsystem
	 * @return a double containing the elevator's total expected queue time
	 */
    public double getExpectedTime(ElevatorRequest elevatorRequest) {
		int distance = Math.abs(elevatorRequest.getDesiredFloor() - currentFloor) * floorSeparation;
		double expectedTime = queueTime + LOADING_TIME;
		if (distance > ACCELERATION_DISTANCE * 2) {
			return expectedTime + (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
		} else {
			return expectedTime + Math.sqrt(distance * 2 / ACCELERATION);
		}
    }
}
