package elevatorsystem;

/**
 * Elevator is a model for simulating an elevator.
 * 
 * @author Liam Tripp
 */
public class Elevator {
	
	private int elevatorNumber;
	private ElevatorSubsystem subsystem;
	// private int currentFloor;
	// private Direction direction;
	
	public Elevator(ElevatorSubsystem elevatorSubsystem) {
		this.subsystem = elevatorSubsystem;
	}
}
