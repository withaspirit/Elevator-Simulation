package scheduler;

import misc.*;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler
	 *
	 * @param elevatorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
	 * @param floorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and floorSubsystem
	 */
	public Scheduler(BoundedBuffer elevatorSubsystemBuffer, BoundedBuffer floorSubsystemBuffer) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
		this.floorSubsystemBuffer = floorSubsystemBuffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		while(true) {
			ServiceRequest request = receiveRequest(floorSubsystemBuffer);
			if (request instanceof ElevatorRequest elevatorRequest){
				if (sendRequest(elevatorRequest, elevatorSubsystemBuffer)) {
					System.out.println("Scheduler Sent Request to Elevator Successful");
				} else {
					System.err.println("Failed Successful");
				}
			}

			request = receiveRequest(elevatorSubsystemBuffer);
			if (request instanceof FloorRequest floorRequest){
				if (sendRequest(floorRequest, floorSubsystemBuffer)) {
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
	 * Removes a ServiceRequest from the Buffer.
	 *
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst(Thread.currentThread());
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);
		return request;
	}
}
