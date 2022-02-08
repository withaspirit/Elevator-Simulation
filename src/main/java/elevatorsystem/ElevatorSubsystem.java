package elevatorsystem;

import misc.*;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, ServiceRequestListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Origin origin;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
		origin = Origin.ELEVATOR_SYSTEM;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		// need to get proper number from somewhere - maybe instantiate a FileInputReader, read in the inputs
		InputFileReader inputFileReader = new InputFileReader();
		int numberOfInputs = inputFileReader.readInputFile("inputs").size();
		for (int i = 0; i < numberOfInputs * 2; i++) {
			ElevatorRequest elevatorRequest = (ElevatorRequest) receiveMessage(elevatorSubsystemBuffer, origin);
			sendMessage(new FloorRequest(elevatorRequest, 1), elevatorSubsystemBuffer, origin);
		}
	}
}
