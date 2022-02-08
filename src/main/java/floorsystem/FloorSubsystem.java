package floorsystem;

import javax.swing.JButton;

import misc.*;
import scheduler.Scheduler;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable {

	private final BoundedBuffer floorSubsystemBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private FloorRequest floorRequest;
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

	// readInputFile();

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
		}
		System.exit(0);
	}

	/**
	 * Puts a request into the buffer.
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		floorSubsystemBuffer.addLast(request, origin);
		requests.remove(0);

		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		return true;
	}

	/**
	 * Removes a request from the Buffer.
	 *
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = floorSubsystemBuffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		return request;
	}
}
