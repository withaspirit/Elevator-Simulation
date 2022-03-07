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
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private ArrayList<Elevator> elevatorList;;
	private Origin origin;

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
		for (Elevator elevator : elevatorList) {
//			sendMessage(new StatusRequest(elevatorRequest,Origin.currentOrigin(), i), elevatorSubsystemBuffer, Origin.currentOrigin());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Origin.currentOrigin());
			//TODO this is a temporary way to access the elevatorSubsystem, UDP messaging will be direct using port
			ElevatorSubsystem elevatorSubsystem = elevator.getElevatorSubsystem();

			double tempExpectedTime = elevatorSubsystem.getExpectedTime(elevatorRequest);
			if (elevatorSubsystem.getMotor().isIdle()) {
				return elevator.getElevatorNumber();

			} else if (!elevator.getElevatorSubsystem().getMotor().isActive()) {
				System.err.println("Elevator is stuck");

			} else if (elevator.getElevatorSubsystem().getMotor().getDirection() == elevatorRequest.getDirection()) {
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
			SystemEvent request = receiveMessage(floorSubsystemBuffer, origin);
			if (request instanceof ElevatorRequest elevatorRequest) {
				int chosenElevator = chooseElevator(elevatorRequest);

				sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
				//TODO destination ElevatorSubsystem = default port e.g. 5000 + ElevatorNumber.
				//TODO Current implementation can choose an Elevator but not send to a specific Elevator as that
				//TODO requires an ElevatorNumber in the ElevatorRequest for the ElevatorSubsystem to compare to.
				//TODO This will be resolved when changing from bounded buffer to UDP.
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				// FIXME: this code might be redundant as it's identical to the one above
				sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
			}

			request = receiveMessage(elevatorSubsystemBuffer, origin);
			if (request instanceof StatusResponse) {

			} else if (request instanceof FloorRequest floorRequest){
				sendMessage(floorRequest, floorSubsystemBuffer, origin);
				System.out.println("Scheduler Sent Request to Floor Successful");
			} else if (request instanceof ApproachEvent approachEvent) {
				sendMessage(approachEvent, floorSubsystemBuffer, origin);
				System.out.println("Scheduler Sent Request to Floor Successful");
			} else {
				System.out.println(request.toString());
			}
		}
	}
}
