package floorsystem;

import javax.swing.JButton;

import misc.*;
import scheduler.Scheduler;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian
 */
public class FloorSubsystem implements Runnable {

	private final BoundedBuffer schedulerFloorsubBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private FloorRequest floorRequest;

	public FloorSubsystem(BoundedBuffer buffer) {
		this.schedulerFloorsubBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
	}

	// readInputFile();

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		while (!requests.isEmpty()) {
			// Sending Data to Scheduler
			if (sendRequest(requests.get(0))) {
				System.out.println("Send Request Successful");
			} else {
				System.out.println("Failed Successful");
			}

			// Receiving Data from Scheduler
			if (receiveRequest()) {
				System.out.println("Receive Request Successful");
				System.out.println("Expected Elevator# "+ floorRequest.getElevatorNumber() + " Arrived");
			} else {
				System.out.println("Failed Successful");
			}
		}
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ElevatorRequest request) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		schedulerFloorsubBuffer.addLast(request);
		requests.remove(0);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequest() {
		ServiceRequest request = schedulerFloorsubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request + "\n");

		if (request instanceof FloorRequest floorRequest){
			this.floorRequest = floorRequest;
		} else {
			System.err.println("Incorrect Request");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}
}
