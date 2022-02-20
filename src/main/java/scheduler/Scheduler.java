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

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private Origin origin;
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
			SystemEvent request = receiveMessage(floorSubsystemBuffer, origin);
			if (request instanceof ElevatorRequest elevatorRequest){
				sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				// FIXME: this code might be redundant as it's identical to the one above
				sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
			}

			request = receiveMessage(elevatorSubsystemBuffer, origin);
			if (request instanceof StatusResponse) {

			} else if (request instanceof FloorRequest floorRequest){
				sendMessage(floorRequest, floorSubsystemBuffer, origin);
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				sendMessage(approachEvent, floorSubsystemBuffer, origin);
			}
		}
	}
}
