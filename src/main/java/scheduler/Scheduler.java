package scheduler;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, SubsystemMessagePasser {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	//private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private Origin origin;
	private final ArrayList<ElevatorSubsystem> elevatorSubsystemList;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler
	 *
	 * @param elevatorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
	 * @param elevatorSubsystemList
	 */
	public Scheduler(BoundedBuffer elevatorSubsystemBuffer, ArrayList<ElevatorSubsystem> elevatorSubsystemList) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
		this.elevatorSubsystemList = elevatorSubsystemList;
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
		for (ElevatorSubsystem elevatorSubsystem : elevatorSubsystemList) {
//			sendMessage(new StatusRequest(elevatorRequest,Origin.currentOrigin(), i), elevatorSubsystemBuffer, Origin.currentOrigin());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Origin.currentOrigin());
			double tempExpectedTime = elevatorSubsystem.getExpectedTime(elevatorRequest);
			Elevator elevator = elevatorSubsystem.getElevator();
			if (elevatorSubsystem.getMotor().isIdle()) {
				return elevator.getElevatorNumber();

			} else if (!elevatorSubsystem.getMotor().isActive()) {
				System.err.println("Elevator is stuck");

			} else if (elevator.getDirection() == elevatorRequest.getDirection()) {
				if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
					if (elevatorRequest.getDirection() == Direction.DOWN && elevator.getCurrentFloor() > elevatorRequest.getDesiredFloor()) {
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevator.getElevatorNumber();

					} else if (elevatorRequest.getDirection() == Direction.UP && elevator.getCurrentFloor() < elevatorRequest.getDesiredFloor()) {
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevator.getElevatorNumber();

					} else {
						// Add to the third queue of the elevator
					}
				}
			} else {
				if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
					elevatorWorstExpectedTime = tempExpectedTime;
					chosenWorstElevator = elevator.getElevatorNumber();
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
			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
			if (request instanceof ElevatorRequest elevatorRequest){
				int chosenElevator = chooseElevator(elevatorRequest);
				// Choose elevator
				// Move elevator
				elevatorSubsystemList.get(chosenElevator - 1).addRequest(elevatorRequest);
				//sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
				//System.out.println("Scheduler Sent Request to Elevator Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				// FIXME: this code might be redundant as it's identical to the one above
				sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
			}
		}
	}
}
