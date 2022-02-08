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
		int receive = requests.size();
		while (receive != 0) {
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				if (sendMessage(requests.get(0), floorSubsystemBuffer, origin)) {
					System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				} else {
					System.err.println(Thread.currentThread().getName() + " failed Sending Successful");
				}
				requests.remove(0);
			}
			ServiceRequest request = receiveMessage(floorSubsystemBuffer, origin);
			if (request instanceof FloorRequest floorRequest){
				receive--;
				System.out.println("Expected Elevator# " + (floorRequest).getElevatorNumber() + " Arrived \n");
			}
		}
	}
}
