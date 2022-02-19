package elevatorsystem;

import systemwide.Direction;

/**
 * ElevatorMotor is the mechanism that moves an Elevator. 
 * 
 * @author Liam Tripp
 */
public class ElevatorMotor {

	private MovementState movementState;
	private Direction direction;

	/**
	 * Constructor for ElevatorMotor.
	 */
	public ElevatorMotor() {
		this.movementState = MovementState.IDLE;
		direction = Direction.STOP;
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
	public int move(int currentFloor, Direction requestDirection) {

		/*
		try{
			Thread.sleep((long) requestTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 */

		switch (requestDirection) {
			case UP:
				setDirection(Direction.UP);
				return currentFloor + 1;
			case DOWN:
				setDirection(Direction.DOWN);
				return currentFloor - 1;
			default:
				setDirection(Direction.STOP);
				return 0;
		}
	}

	/**
	 * Simulates the elevator moving up
	 */
	public void moveUp(){
		setMovementState(MovementState.ACTIVE);
		this.setDirection(Direction.UP);
		// setCurrentFloor(currentFloor + Math.abs(currentFloor - requestFloor));
	}

	/**
	 * Simulates the elevator moving down
	 */
	public void moveDown(){
		// Set state and direction
		setMovementState(MovementState.ACTIVE);
		setDirection(Direction.DOWN);
		// Update location
		//setCurrentFloor(getCurrentFloor() - Math.abs(getCurrentFloor() - requestFloor));
	}
}
