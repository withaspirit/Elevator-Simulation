package scheduler;

import java.util.ArrayList;
import misc.BoundedBuffer;
import misc.ElevatorRequest;
import misc.FloorRequest;
import misc.ServiceRequest;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian
 */
public class Scheduler implements Runnable {

	private final BoundedBuffer schedulerElevatorsubBuffer; // Elevator Subsystem - Scheduler link
	private final BoundedBuffer schedulerFloorsubBuffer; // Floor Subsystem- Scheduler link
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

		// Receiving Data from Floor Subsystem
		if (receiveRequest(schedulerFloorsubBuffer)) {
			System.out.println("Receive Request from Floor SubSystem Successful");
		} else {
			System.out.println("Failed Successful");
		}

		// Receiving Data from Elevator Subsystem
		if (receiveRequest(schedulerElevatorsubBuffer)) {
			System.out.println("Receive Request from Elevator SubSystem Successful");
		} else {
			System.out.println("Failed Successful");
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
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		buffer.addLast(request);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		return true;
	}

	/**
	 * Checks the buffer for messages
	 * 
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequest(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		if (request instanceof FloorRequest floorRequest){
			if (sendRequest(floorRequest, schedulerFloorsubBuffer)) {
				System.out.println("Send Request to floor Successful");
			} else {
				System.out.println("Failed Successful");
			}
		} else if (request instanceof ElevatorRequest elevatorRequest){
			if (sendRequest(elevatorRequest, schedulerElevatorsubBuffer)) {
				System.out.println("Send Request to Elevator Successful");
			} else {
				System.out.println("Failed Successful");
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		return true;
	}

}
