package scheduler;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Origin;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, SubsystemMessagePasser {

	private final BoundedBuffer buffer; // ElevatorSubsystem - Scheduler link
//	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private Origin origin;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler
	 *
	 * @param buffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
	 */
	public Scheduler(BoundedBuffer buffer) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.buffer = buffer;
		origin = Origin.SCHEDULER;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while(true) {
			SystemEvent request = receiveMessage(buffer, origin);
			Origin sendOrigin;
			if (request.getOrigin() == Origin.FLOOR_SYSTEM){
				sendOrigin = Origin.ELEVATOR_SYSTEM;
			} else {
				sendOrigin = Origin.FLOOR_SYSTEM;
			}
			if (request instanceof ElevatorRequest elevatorRequest){
				sendMessage(origin, elevatorRequest, buffer, sendOrigin);
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				// FIXME: this code might be redundant as it's identical to the one above
				sendMessage(origin, approachEvent, buffer, sendOrigin);
			} else if (request instanceof FloorRequest floorRequest){
				sendMessage(origin, floorRequest, buffer, sendOrigin);
			}
		}
	}
}
