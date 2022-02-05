package elevatorsystem;

import misc.*;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer schedulerElevatorsubBuffer; // Elevator Subsystem - Scheduler link

	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.schedulerElevatorsubBuffer = buffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		while(true) {
			ServiceRequest elevatorRequest = receiveRequest();
			// Receiving Data from Scheduler
			while (elevatorRequest.isOrigin()) {
				System.out.println(elevatorRequest);
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sendRequest(new FloorRequest((ElevatorRequest) elevatorRequest, 1));
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
		schedulerElevatorsubBuffer.addLast(request, Thread.currentThread());

		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = schedulerElevatorsubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		return request;
	}
}
