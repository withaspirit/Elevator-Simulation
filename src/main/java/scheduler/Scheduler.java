package scheduler;

import elevatorsystem.MovementState;
import requests.*;
import systemwide.BoundedBuffer;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, ServiceRequestListener {

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
			SystemEvent request = receiveMessage(floorSubsystemBuffer, Thread.currentThread());

			if (request instanceof ElevatorRequest elevatorRequest){
				sendMessage(elevatorRequest, elevatorSubsystemBuffer, Thread.currentThread());
				System.out.println("Scheduler Sent Request to Elevator Successful");
			}

			request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
			if (request instanceof StatusResponse){

			}else if (request instanceof FloorRequest floorRequest){
				floorRequest = chooseElevator(floorRequest);
				sendMessage(floorRequest, floorSubsystemBuffer, Thread.currentThread());
				System.out.println("Scheduler Sent Request to Elevator Successful");
			}
		}
	}

	public FloorRequest chooseElevator(FloorRequest floorRequest) {
		int tempNumberOfElevators = 1;
		double elevatorTime = 0;
		int chosenElevator = 0;
		for (int i = 0; i < tempNumberOfElevators; i++) {
			sendMessage(new StatusRequest(floorRequest,Thread.currentThread(), i), floorSubsystemBuffer, Thread.currentThread());
			SystemEvent request = receiveMessage(floorSubsystemBuffer, Thread.currentThread());
			if (request instanceof StatusResponse statusResponse){
				if (statusResponse.getStatus() == MovementState.IDLE){
					return new FloorRequest(floorRequest, floorRequest.getElevatorNumber());
				}else if (elevatorTime == 0 || elevatorTime > statusResponse.getExpectedTime()){
					elevatorTime = statusResponse.getExpectedTime();
					chosenElevator = floorRequest.getElevatorNumber();
				}
			}
		}

		return new FloorRequest(floorRequest, chosenElevator);
	}
}
