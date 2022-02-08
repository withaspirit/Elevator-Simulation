package floorsystem;

import misc.*;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable {

	private final BoundedBuffer floorSubBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;

	public FloorSubsystem(BoundedBuffer buffer) {
		this.floorSubBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		int receive = requests.size();
		while (receive != 0) {
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				if (sendRequest(requests.get(0))) {
					System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				} else {
					System.err.println(Thread.currentThread().getName() + " failed Sending Successful");
				}
			}
			if (!floorSubBuffer.checkFirst().isOrigin()) {
				ServiceRequest floorRequest = receiveRequest();
				receive--;
				System.out.println("Expected Elevator# " + ((FloorRequest) floorRequest).getElevatorNumber() + " Arrived \n");
			}
		}
		System.exit(0);
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		floorSubBuffer.addLast(request, Thread.currentThread());
		requests.remove(0);
		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = floorSubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);
		return request;
	}
}
