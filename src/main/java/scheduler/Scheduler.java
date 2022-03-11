package scheduler;

import elevatorsystem.Elevator;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, SubsystemMessagePasser {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private final ArrayList<Elevator> elevatorList;
	private Origin origin;
	private Queue<SystemEvent> requestQueue;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler
	 *
	 * @param elevatorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
     * @param floorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and floorSubsystem
	 */
	public Scheduler(BoundedBuffer elevatorSubsystemBuffer, BoundedBuffer floorSubsystemBuffer, ArrayList<Elevator> elevatorList) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
		this.floorSubsystemBuffer = floorSubsystemBuffer;
		this.elevatorList = elevatorList;
		requestQueue = new LinkedList<>();
		origin = Origin.SCHEDULER;
	}

	/**
	 * Returns an elevator number corresponding to an elevator that is
	 * best suited to perform the given ElevatorRequest based on
	 * expected time to fulfill the request and direction of elevator.
	 *
	 * @param elevatorRequest an ElevatorRequest
	 * @return a number corresponding to an elevator
	 */
	public int chooseElevator(ElevatorRequest elevatorRequest) {

		double elevatorBestExpectedTime = 0.0;
		// Best elevator is an elevator traveling in path that collides with request floor
		double elevatorOkExpectedTime = 0.0;
		// Ok elevator is an elevator that is traveling in the other direction
		double elevatorWorstExpectedTime = 0.0;
		// Worst elevator is an elevator that is traveling in the same direction but missed the request
		int chosenBestElevator = 0;
		int chosenOkElevator = 0;
		int chosenWorstElevator = 0;
		for (Elevator elevator : elevatorList) {
//			sendMessage(new StatusRequest(elevatorRequest,Origin.currentOrigin(), i), elevatorSubsystemBuffer, Origin.currentOrigin());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Origin.currentOrigin());
			double tempExpectedTime = elevator.getExpectedTime(elevatorRequest);
			Direction requestDirection = elevatorRequest.getDirection();
			int currentFloor = elevator.getCurrentFloor();
			int desiredFloor = elevatorRequest.getDesiredFloor();
			int elevatorNumber = elevator.getElevatorNumber();

			if (elevator.getMotor().isIdle()) {
				return elevatorNumber;

			} else if (!elevator.getMotor().isActive()) {
				System.err.println("Elevator is stuck");

			} else if (elevator.getServiceDirection() == requestDirection) {
				if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
					if (requestDirection == Direction.DOWN && currentFloor > desiredFloor) {
						//check if request is in path current floor > directed floor going down
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (requestDirection == Direction.UP && currentFloor < desiredFloor) {
						//check if request is in path current floor < directed floor going up
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (elevatorOkExpectedTime == 0 || elevatorOkExpectedTime > tempExpectedTime){
						//if request is in the correct direction but not in path of elevator
						elevatorWorstExpectedTime = tempExpectedTime;
						chosenWorstElevator = elevatorNumber;
					}
				}
			} else {
				if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
					//if the elevator traveling in the wrong direction
					elevatorOkExpectedTime = tempExpectedTime;
					chosenOkElevator = elevatorNumber;
				}
			}
		}
		if (chosenBestElevator == 0) {
			if (chosenOkElevator == 0){
				chosenBestElevator = chosenWorstElevator;
			} else {
				chosenBestElevator = chosenOkElevator;
			}
		}
		return chosenBestElevator;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while(true) {
			SystemEvent request;
			// remove from either floorBuffer or ElevatorBuffer
			if (floorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				request = receiveMessage(floorSubsystemBuffer, origin);
				requestQueue.add(request);
			} else if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				request = receiveMessage(elevatorSubsystemBuffer, origin);
				requestQueue.add(request);
			}

			// send a request if possible
			if (!requestQueue.isEmpty()) {
				request = requestQueue.remove();

				if (request.getOrigin() == Origin.FLOOR_SYSTEM) {
					if (request instanceof ElevatorRequest elevatorRequest){
						elevatorRequest.setElevatorNumber(chooseElevator(elevatorRequest));
						sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Elevator Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						// FIXME: this code might be redundant as it's identical to the one above
						sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
					}
				} else if (request.getOrigin() == Origin.ELEVATOR_SYSTEM) {
					if (request instanceof StatusResponse) {

					} else if (request instanceof FloorRequest floorRequest){
						sendMessage(floorRequest, floorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Floor Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						sendMessage(approachEvent, floorSubsystemBuffer, origin);
					}
				} else {
					System.err.println("Scheduler should not contain items whose origin is Scheduler: " + request);
				}
 			}
		}
	}
}
