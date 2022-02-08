package floorsystem;

import misc.*;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, ServiceRequestListener {

	private final BoundedBuffer floorSubsystemBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private Origin origin;

	/**
	 * Constructor for FloorSubsystem.
	 *
	 * @param buffer the buffer the FloorSubsystem passes messages to and receives messages from
	 */
	public FloorSubsystem(BoundedBuffer buffer) {
		this.floorSubsystemBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
		origin = Origin.FLOOR_SYSTEM;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 */
	public void run() {
		int size = requests.size();
		for (int i = 0; i < size; i++) {
			System.out.println("Queue Size " + requests.size());
			ElevatorRequest elevatorRequest = requests.get(0);
			sendMessage(elevatorRequest, floorSubsystemBuffer, origin);
			FloorRequest floorRequest = (FloorRequest) receiveMessage(floorSubsystemBuffer, origin);
			System.out.println("Printing buffer contents for buffer");
			floorSubsystemBuffer.printBufferContents();
			requests.remove(0);
		}
	}
}
