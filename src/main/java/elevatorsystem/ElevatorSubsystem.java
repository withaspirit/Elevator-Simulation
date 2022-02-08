package elevatorsystem;

import misc.*;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer elevatorSubBuffer; // Elevator Subsystem - Scheduler link

	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubBuffer = buffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		while(true) {
			ServiceRequest request = receiveRequest();
			if (request instanceof ElevatorRequest elevatorRequest){
				if (sendRequest(new FloorRequest(elevatorRequest, 1))){
					System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				} else {
					System.err.println(Thread.currentThread().getName() + " failed Sending Successful");
				}
			}
		}
	}

	/**
	 * Puts the request message into the buffer
	 *
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		elevatorSubBuffer.addLast(request, Thread.currentThread());
		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest() {
		if (!elevatorSubBuffer.checkFirst().isOrigin()) {
			ServiceRequest request = elevatorSubBuffer.removeFirst();
			System.out.println(Thread.currentThread().getName() + " received the request: " + request);
			return request;
		}
		return null;
	}
}
