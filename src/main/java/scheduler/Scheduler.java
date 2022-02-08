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
	private Origin origin;

	/**
	 * Constructor for Scheduler
	 *
	 * @param buffer1
	 * @param buffer2
	 */
	public Scheduler(BoundedBuffer buffer1, BoundedBuffer buffer2) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.schedulerElevatorsubBuffer = buffer1;
		this.schedulerFloorsubBuffer = buffer2;
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
		 */
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
		buffer.addLast(request, origin);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return true;
	}

	/**
	 * Removes a ServiceRequest from the Buffer.
	 *
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		}
		return request;
	}
}
