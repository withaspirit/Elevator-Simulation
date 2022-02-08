package scheduler;

import misc.*;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable {

	private final BoundedBuffer elevatorSubBuffer; // Elevator Subsystem - Scheduler link
	private final BoundedBuffer floorSubBuffer; // Floor Subsystem- Scheduler link
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	public Scheduler(BoundedBuffer buffer1, BoundedBuffer buffer2) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubBuffer = buffer1;
		this.floorSubBuffer = buffer2;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		while(true) {
			ServiceRequest request = receiveRequest(floorSubBuffer);
			if (request instanceof ElevatorRequest elevatorRequest){
				if (sendRequest(elevatorRequest, elevatorSubBuffer)) {
					System.out.println("Scheduler Sent Request to Elevator Successful");
				} else {
					System.err.println("Failed Successful");
				}
			}

			request = receiveRequest(elevatorSubBuffer);
			if (request instanceof FloorRequest floorRequest){
				if (sendRequest(floorRequest, floorSubBuffer)) {
					System.out.println("Scheduler Sent Request to Elevator Successful");
				} else {
					System.err.println("Failed Successful");
				}
			}
		}
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @param buffer the BoundedBuffer used for sending the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		buffer.addLast(request, Thread.currentThread());
		return true;
	}

	/**
	 * Checks the buffer for messages
	 * 
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest(BoundedBuffer buffer) {
		if (!buffer.checkFirst().isOrigin()) {
			ServiceRequest request = buffer.removeFirst();
			System.out.println(Thread.currentThread().getName() + " received the request: " + request);
			return request;
		}
		return null;
	}
}
