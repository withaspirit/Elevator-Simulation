package scheduler;

import misc.*;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, ServiceRequestListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;
	private Origin origin;

	/**
	 * Constructor for Scheduler
	 *
	 * @param buffer1
	 * @param buffer2
	 */
	public Scheduler(BoundedBuffer buffer1, BoundedBuffer buffer2) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = buffer1;
		this.floorSubsystemBuffer = buffer2;
		origin = Origin.SCHEDULER;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		// TODO: make getting input file size a method?
		InputFileReader inputFileReader = new InputFileReader();
		int numberOfInputs = inputFileReader.readInputFile("inputs").size();
		for (int i = 0; i < numberOfInputs * 2; i++) {
			ElevatorRequest elevatorRequest = (ElevatorRequest) receiveMessage(floorSubsystemBuffer, origin);
			sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
			FloorRequest floorRequest = (FloorRequest) receiveMessage(elevatorSubsystemBuffer, origin);
			sendMessage(floorRequest, floorSubsystemBuffer, origin);
		}
	}
}
