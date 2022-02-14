package elevatorsystem;

/**
 * ElevatorMotor is the mechanism that moves an Elevator. 
 * 
 * @author Liam Tripp
 */
public class ElevatorMotor {

	private MovementState movementState;

	/**
	 * Constructor for ElevatorMotor.
	 */
	public ElevatorMotor() {
		this.movementState = MovementState.IDLE;
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
}
