package elevatorsystem;

import systemwide.Direction;

/**
 * ElevatorMotor is the mechanism that moves an Elevator. 
 * 
 * @author Liam Tripp, Brady Norton
 */
public class ElevatorMotor {

	private MovementState movementState;
	private Direction direction;

	/**
	 * Constructor for ElevatorMotor.
	 */
	public ElevatorMotor() {
		this.movementState = MovementState.IDLE;
		direction = Direction.NONE;
	}

	/**
	 * Returns the movement state of the Elevator.
	 *
	 * @return movementState the current movement state of the elevator
	 */
	public MovementState getMovementState() {
		return movementState;
	}

	/**
	 * Sets the movement state of the Elevator.
	 *
	 * @param movementState the movement state of the elevator
	 */
	public void setMovementState(MovementState movementState) {
		this.movementState = movementState;
	}

	/**
	 * Gets the direction the elevator is heading.
	 *
	 * @return direction the current direction of the elevator
	 */
	public Direction getDirection(){
		return direction;
	}

	/**
	 * Sets the direction of the elevator.
	 *
	 * @param direction the elevator will be moving
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Simulates elevator movement.
	 */
	public int move(int currentFloor, int requestFloor, Direction requestDirection) {
		int floorDifference = currentFloor - requestFloor;
		int nextFloor = currentFloor;

		// if floor is above
		if (floorDifference < 0) {
			nextFloor += 1;
		// floor is below
		} else if (floorDifference > 0) {
			nextFloor -= 1;
		} else {
		}
		return nextFloor;
	}

	/**
	 * Changes the direction of the Motor depending on the elevator's location
	 * and the location of its next floor to visit.
	 *
	 * @param currentFloor the current floor of the elevator
	 * @param requestFloor the number of the elevator's next floor to visit
	 */
	public void changeDirection(int currentFloor, int requestFloor) {
		if (currentFloor > requestFloor) {
			setDirection(Direction.DOWN);
		} else if (currentFloor < requestFloor) {
			setDirection(Direction.UP);
		} else {
			// do nothing because elevator is on the same floor
		}
	}

	/**
	 * Stops the elevator.
	 */
	public void stop() {
		// Set state and direction
		setMovementState(MovementState.IDLE);
		setDirection(Direction.NONE);
	}

	/**
	 * Simulates the elevator moving up
	 */
	public void moveUp() {
		setMovementState(MovementState.ACTIVE);
		this.setDirection(Direction.UP);
		// setCurrentFloor(currentFloor + Math.abs(currentFloor - requestFloor));
	}

	/**
	 * Simulates the elevator moving down
	 */
	public void moveDown() {
		// Set state and direction
		setMovementState(MovementState.ACTIVE);
		setDirection(Direction.DOWN);
		// Update location
		//setCurrentFloor(getCurrentFloor() - Math.abs(getCurrentFloor() - requestFloor));
	}

	/**
	 * Checks if the elevator is currently active (in motion).
	 *
	 * @return true if elevator is moving, false otherwise
	 */
	public boolean isActive() {
		return getMovementState().equals(MovementState.ACTIVE);
	}

	/**
	 * Determines whether the elevator is idle.
	 *
	 * @return true if the elevator is not moving, false otherwise
	 */
	public boolean isIdle() {
		return getMovementState().equals(MovementState.IDLE);
	}
}
