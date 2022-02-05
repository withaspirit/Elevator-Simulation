package elevatorsystem;

import misc.*;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer schedulerElevatorsubBuffer; // Elevator Subsystem - Scheduler link
	private FloorRequest floorRequest;

	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.schedulerElevatorsubBuffer = buffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		while(true) {
			// Receiving Data from Scheduler
			ServiceRequest request = receiveRequest();
			if (sendRequest(new FloorRequest((ElevatorRequest) request, 1))) {
				System.out.println("Elevator SubSystem Sent Request to Scheduler Successful");
			} else {
				System.out.println(Thread.currentThread().getName() + " failed sending Successful");
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
		schedulerElevatorsubBuffer.addLast(request);

		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest() {
		while (schedulerElevatorsubBuffer.checkFirst() instanceof FloorRequest) {}
		ServiceRequest request = schedulerElevatorsubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		return request;
	}
}
