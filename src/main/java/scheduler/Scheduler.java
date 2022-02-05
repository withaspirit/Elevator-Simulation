package scheduler;

import misc.*;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable {

	private final BoundedBuffer schedulerElevatorsubBuffer; // Elevator Subsystem - Scheduler link
	private final BoundedBuffer schedulerFloorsubBuffer; // Floor Subsystem- Scheduler link
	private ServiceRequest request;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

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
			redirectRequest(schedulerFloorsubBuffer);
			redirectRequest(schedulerElevatorsubBuffer);
		}
	}

	private void redirectRequest(BoundedBuffer buffer) {
		request = receiveRequest(buffer);
		if (request instanceof FloorRequest floorRequest){
			if (sendRequest(floorRequest, schedulerFloorsubBuffer)) {
				System.out.println("Scheduler Sent Request to floor Successful");
			} else {
				System.out.println("Failed Successful");
			}
		} else if (request instanceof ElevatorRequest elevatorRequest){
			if (sendRequest(elevatorRequest, schedulerElevatorsubBuffer)) {
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else {
				System.out.println("Failed Successful");
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
		if (request != null) {
			System.out.println(Thread.currentThread().getName() + " sending: " + request);
			buffer.addLast(request);

			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks the buffer for messages
	 * 
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return true if request is successful, false otherwise
	 */
	public ServiceRequest receiveRequest(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		return request;
	}
}
