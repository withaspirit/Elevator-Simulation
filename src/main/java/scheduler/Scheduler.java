package scheduler;

import elevatorsystem.Elevator;
import elevatorsystem.MovementState;
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
	private final ArrayList<ElevatorMonitor> elevatorMonitorList;
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
	public Scheduler(BoundedBuffer elevatorSubsystemBuffer, BoundedBuffer floorSubsystemBuffer, int numberOfElevators) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
		this.floorSubsystemBuffer = floorSubsystemBuffer;
		elevatorMonitorList = new ArrayList<>();
		for (int i = 1; i <= numberOfElevators; i++) {
			ElevatorMonitor monitor = new ElevatorMonitor(i);
			elevatorMonitorList.add(monitor);
		}
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
		double elevatorWorstExpectedTime = 0.0;
		int chosenBestElevator = 0;
		int chosenWorstElevator = 0;
		for (ElevatorMonitor monitor : elevatorMonitorList) {
//			sendMessage(new StatusRequest(elevatorRequest,Origin.currentOrigin(), i), elevatorSubsystemBuffer, Origin.currentOrigin());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Origin.currentOrigin());
			double tempExpectedTime = monitor.getQueueTime();
			if (monitor.getState() == MovementState.IDLE) {
				return monitor.getElevatorNumber();

			} else if (monitor.getState() == MovementState.STUCK) {
				System.err.println("Elevator is stuck");

			} else if (monitor.getDirection() == elevatorRequest.getDirection()) {
				if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
					if (elevatorRequest.getDirection() == Direction.DOWN && monitor.getCurrentFloor() > elevatorRequest.getDesiredFloor()) {
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = monitor.getElevatorNumber();

					} else if (elevatorRequest.getDirection() == Direction.UP && monitor.getCurrentFloor() < elevatorRequest.getDesiredFloor()) {
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = monitor.getElevatorNumber();

					} else {
						// Add to the third queue of the monitor
					}
				}
			} else {
				if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
					elevatorWorstExpectedTime = tempExpectedTime;
					chosenWorstElevator = monitor.getElevatorNumber();
				}
			}
		}
		if (chosenBestElevator == 0) {
			chosenBestElevator = chosenWorstElevator;
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
					if (request instanceof StatusUpdate statusUpdate) {
						elevatorMonitorList.get(statusUpdate.getElevatorNumber()-1).updateMonitor(statusUpdate);
						sendMessage(statusUpdate, floorSubsystemBuffer, origin);
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
