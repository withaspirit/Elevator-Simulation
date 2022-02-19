package elevatorsystem;

import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.ServiceRequest;
import requests.SystemEvent;
import systemwide.Direction;

/**
 * Elevator is a model for simulating an elevator.
 *
 * Requirements:
 * 1. Can move between floors
 * 2. Only services one elevator shaft of a structure
 * 3. Has speed
 * 4. Can stop at floors
 * 5. Knows it's own location
 * 6. Takes time for elevator to move
 * 7. travels at SPEED to traverse FLOOR HEIGHT per second
 *
 * @author Liam Tripp, Brady Norton
 */
public class Elevator implements Runnable{

	// Elevator Subsystem
	private ElevatorSubsystem subsystem;

	//

	// Elevator Measurements
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)

	// Elevator Properties
	private MovementState status;
	private int currentFloor;
	private Direction direction = Direction.UP;
	private float speed;
	private int elevatorNumber;

	// Request Properties
	private double requestTime;
	private int requestFloor;
	private Direction requestedDirection;
	private ElevatorRequest request;
	private final double queueTime;

	/**
	 * Constructor for Elevator class
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param subsystem
	 */
	public Elevator(int elevatorNumber, ElevatorSubsystem subsystem) {
		this.subsystem = subsystem;
		currentFloor = 1;
		speed = ACCELERATION;
		status = MovementState.IDLE;
		request = null;
		queueTime = 0.0;
	}

	/**
	 * Calculates the amount of time it will take for the elevator to stop at it's current speed
	 *
	 * @return total time it will take to stop as a float
	 */
	public float stopTime() {
		float numerator = 0 - speed;
		return numerator / ACCELERATION;
	}

	/**
	 * Gets the distance until the next floor as a float
	 *
	 * @return distance until next floor
	 */
	public double getDistanceUntilNextFloor(int requestFloor){
		return Math.abs((requestFloor - currentFloor)*3.91);
	}

	/**
	 * Checks if the elevator is currently active (in motion)
	 *
	 * @return true if elevator is moving
	 */
	public boolean isActive(){
		return status.equals(MovementState.ACTIVE);
	}

	/**
	 * Gets the state of the elevator
	 * @return MovementState value
	 */
	public MovementState getState(){
		return this.status;
	}

	/**
	 * Sets the state of the elevator
	 * @param state the state elevator will be set to
	 */
	public void setState(MovementState state){
		this.status = state;

	}

	/**
	 * Gets the current floor the elevator is on
	 *
	 * @return the current floor as an int
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	/**
	 * Sets the currentFloor that the elevator is on
	 *
	 * @param currentFloor the floor to set the elevator on
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * Gets the Direction the elevator is heading
	 *
	 * @return Direction
	 */
	public Direction getCurrentDirection(){
		return direction;
	}

	/**
	 * Sets the direction of the elevator
	 *
	 * @param direction the elevator will be moving
	 */
	public void setCurrentDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Gets the speed of the elevator
	 *
	 * @return the speed as a float
	 */
	public float getSpeed(){
		return speed;
	}

	/**
	 * Sets the speed of the elevator
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public void setRequest(ServiceRequest request){
		this.request = (ElevatorRequest) request;
	}

	/**
	 * Processes a serviceRequest and moves based on the request type
	 *
	 * @param serviceRequest the request that's sent to elevator
	 */
	public void processRequest(ServiceRequest serviceRequest){
		// If request is an elevator request
		if(serviceRequest instanceof ElevatorRequest){
			// Set time of request
			this.requestTime = requestTime((ElevatorRequest) serviceRequest);

			// Set floor of request
			this.requestFloor = ((ElevatorRequest) serviceRequest).getDesiredFloor();

			// Set direction of request
			this.requestedDirection = serviceRequest.getDirection();

			if(requestedDirection == Direction.UP){
				this.moveUp();
			}
			else if(requestedDirection == Direction.DOWN){
				this.moveDown();
			}
			else if(requestedDirection == Direction.STOP){
				this.stop();
			}
		}
		else if(serviceRequest instanceof FloorRequest){
			// do something
		}
		// Set to idle once floor reached
		status = MovementState.IDLE;
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorRequest an elevator request from the floorSubsystem
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(ElevatorRequest elevatorRequest) {
		return queueTime + LOAD_TIME + requestTime(elevatorRequest);
	}

	/**
	 *
	 * @param elevatorRequest
	 * @return
	 */
	public double requestTime(ElevatorRequest elevatorRequest) {
		double distance = Math.abs(elevatorRequest.getDesiredFloor() - currentFloor) * FLOOR_HEIGHT;
		double ACCELERATION_DISTANCE = ACCELERATION * FLOOR_HEIGHT;
		double ACCELERATION_TIME = Math.sqrt(FLOOR_HEIGHT * 2 / ACCELERATION); //s = 1/2at^2 therefore t = sqrt(s*2/a)
		if (distance > ACCELERATION_DISTANCE * 2) {
			return (distance - ACCELERATION_DISTANCE * 2) / MAX_SPEED + ACCELERATION_TIME * 2;
		} else {
			return Math.sqrt(distance * 2 / ACCELERATION);
		}
	}

	/**
	 * Stops the elevator
	 */
	public void stop(){
		// Set state and direction
		this.setState(MovementState.IDLE);
		this.setCurrentDirection(Direction.STOP);

		System.out.println("Status: Stopped");
	}

	/**
	 * Simulates the elevator moving up
	 */
	public void moveUp(){
		// Set state and direction
		this.setState(MovementState.ACTIVE);
		this.setCurrentDirection(Direction.UP);

		// Simulate time
		try{
			Thread.sleep((long) requestTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Update location
		this.setCurrentFloor(this.getCurrentFloor() + requestFloor);
	}

	/**
	 * Simulates the elevator moving down
	 */
	public void moveDown(){
		// Set state and direction
		this.setState(MovementState.ACTIVE);
		this.setCurrentDirection(Direction.DOWN);

		// Simulate time
		try{
			Thread.sleep((long) requestTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Update location
		this.setCurrentFloor(this.getCurrentFloor() - requestFloor);
	}


	@Override
	public void run() {
		while(true){
			while(!isActive()){
				if(request != null && request.getDesiredFloor() != currentFloor){
					processRequest(request);
				}
			}
		}
	}
}
