package elevatorsystem;

import requests.*;
import systemwide.Direction;
import elevatorsystem.MovementState;

import java.time.LocalTime;

/**
 * Elevator is a model for simulating an elevator.
 *
 * Requirements:
 * 1. Can move between floors
 * 2. Only services one elevator shaft of a structure
 * 3. Has speed
 * 4. Can stop at floors
 * 5. Knows its own location
 * 6. Takes time for elevator to move
 * 7. travels at SPEED to traverse FLOOR HEIGHT per second
 *
 * @author Liam Tripp, Brady Norton
 */
public class Elevator implements Runnable, SubsystemPasser {

	// Elevator Subsystem
	private ElevatorSubsystem subsystem;

	// Elevator Measurements
	public static final float MAX_SPEED = 2.67f; // meters/second
	public static final float ACCELERATION = 0.304f; // meters/second^2
	public static final float LOAD_TIME = 9.5f; // seconds
	public static final float FLOOR_HEIGHT = 3.91f; // meters (22 steps/floor @ 0.1778 meters/step)

	// Elevator Properties
	private int currentFloor;
	private Direction direction = Direction.UP;
	private float speed;
	private float displacement;
	private int elevatorNumber;

	private final ElevatorMotor motor;
	private Direction currentDirection;
	private final double queueTime;

	// Request Properties
	private double requestTime;
	private int requestFloor;
	private Direction requestedDirection;
	private ElevatorRequest request;

	/**
	 * Constructor for Elevator class
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param elevatorNumber
	 * @param elevatorSubsystem
	 */
	public Elevator(int elevatorNumber, ElevatorSubsystem elevatorSubsystem) {
		this.subsystem = elevatorSubsystem;
		this.elevatorNumber = elevatorNumber;
		speed = 0;
		motor = new ElevatorMotor();
		queueTime = 0.0;
		request = null;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
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
	public float getDistanceUntilNextFloor(){
		float distance = 0;
		float stopTime = stopTime();

		// Using Kinematics equation: d = vt + (1/2)at^2
		float part1 = speed*stopTime;
		// System.out.println("Part 1: " + part1);

		float part2 = (float) ((0.5)*(ACCELERATION)*(Math.pow(stopTime,2)));
		// System.out.println("Part 2: " + part2);

		return part1 - part2;
	}

	/**
	 * Checks if the elevator is currently active (in motion)
	 *
	 * @return true if elevator is moving
	 */
	public boolean isActive(){
		return motor.getMovementState().equals(MovementState.ACTIVE);
	}

	/**
	 * Gets the state of the elevator
	 *
	 * @return MovementState value
	 */
	public MovementState getState() {
		return motor.getMovementState();
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
	public Direction getDirection(){
		return direction;
	}

	/**
	 * Sets the direction of the elevator
	 *
	 * @param direction the elevator will be moving
	 */
	public void setDirection(Direction direction) {
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
	 *
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setRequest(ServiceRequest request){
		this.request = (ElevatorRequest) request;
	}

	/**
	 * Gets the displacement that the elevator has moved on the current floor
	 *
	 * @return displacement of elevator as float
	 */
	public float getFloorDisplacement(){
		return displacement;
	}

	/**
	 * Sets the displacement the elevator for the current floor
	 *
	 * @param displacement the displacement of the elevator as a float
	 */
	public void setFloorDisplacement(float displacement) {
		this.displacement = displacement;
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

			while (currentFloor != requestFloor) {
				currentFloor = motor.move(currentFloor, requestedDirection);
			}
		}
		else if(serviceRequest instanceof FloorRequest){
			// do something
		}
		// Set to idle once floor reached
		motor.setMovementState(MovementState.IDLE);
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
		motor.setMovementState(MovementState.IDLE);
		this.setDirection(Direction.STOP);

		System.out.println("Status: Stopped");
	}

	/**
	 * Passes an ApproachEvent to the ElevatorSubsystem.
	 *
	 * @param approachEvent the ApproachEvent to be passed to the subsystem
	 */
	public void passApproachEvent(ApproachEvent approachEvent) {
		subsystem.handleApproachEvent(approachEvent);
	}

	/**
	 * Receives an ApproachEvent from the Subsystem and returns it to the component.
	 *
	 * @param approachEvent the ApproachEvent to be received from the Subsystem
	 */
	@Override
	public void receiveApproachEvent(ApproachEvent approachEvent) {
		// do thing
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
