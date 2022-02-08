package elevatorsystem;

import misc.*;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		while(true) {
			ServiceRequest request = receiveRequest();
			if (request instanceof ElevatorRequest elevatorRequest) {
				if (sendRequest(new FloorRequest(elevatorRequest, 1))) {
					System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				} else {
					System.err.println(Thread.currentThread().getName() + " failed Sending Successful");
				}
			}
		}
	}

	/**
	 * Puts a request into a buffer.
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		elevatorSubsystemBuffer.addLast(request, Thread.currentThread());
		return true;
	}

	/**
	 * Removes a request from the Buffer.
	 *
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = elevatorSubsystemBuffer.removeFirst(Thread.currentThread());
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);
		return request;
	}
}
