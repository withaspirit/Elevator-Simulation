package elevatorsystem;

import systemwide.Direction;
import elevatorsystem.MovementState;

import java.util.TreeSet;

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
public class Elevator {

	// Elevator Subsystem
	private ElevatorSubsystem subsystem;

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
	private float displacement;
	//private int elevatorNumber;

	/**
	 * Constructor for Elevator class
	 * Instantiates subsystem, currentFloor, speed, displacement, and status
	 *
	 * @param subsystem
	 */
	public Elevator(ElevatorSubsystem subsystem) {
		this.subsystem = subsystem;
		currentFloor = 1;
		speed = ACCELERATION;
		displacement = 0;
		status = MovementState.IDLE;
	}

	/**
	 * Calculates the amount of time it will take for the elevator to stop at it's current speed
	 *
	 * @return total time it will take to stop as a float
	 */
	public float stopTime()
	{
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
		System.out.println("Part 1: " + part1);

		float part2 = (float) ((0.5)*(ACCELERATION)*(Math.pow(stopTime,2)));
		System.out.println("Part 2: " + part2);

		return part1 - part2;
	}

	/**
	 * Checks if the elevator is currently active (in motion)
	 *
	 * @return true if elevator is moving
	 */
	public boolean isActive(){ return status.equals(MovementState.ACTIVE); }


	/**
	 * Gets the current floor the elevator is on
	 *
	 * @return the current floor as an int
	 */
	public int getCurrentFloor() { return currentFloor; }

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
	public Direction getDirection(){ return direction; }

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
	public float getSpeed(){ return speed; }

	/**
	 * Sets the speed of the elevator
	 * @param speed
	 */
	public void setSpeed(float speed) { this.speed = speed; }


	/**
	 * Gets the displacement that the elevator has moved on the current floor
	 *
	 * @return displacement of elevator as float
	 */
	public float getFloorDisplacement(){return displacement;}

	/**
	 * Sets the displacement the elevator for the current floor
	 *
	 * @param displacement the displacement of the elevator as a float
	 */
	public void setFloorDisplacement(float displacement) {
		this.displacement = displacement;
	}
}
