package elevatorsystem;

import client_server_host.Client;
import client_server_host.Port;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SubsystemMessagePasser, SystemEventListener {

  	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Client server;
  	private final ArrayList<Elevator> elevatorList;
	private Queue<SystemEvent> requestQueue;
	private Origin origin;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
		elevatorList = new ArrayList<>();
		requestQueue = new LinkedList<>();
		origin = Origin.ELEVATOR_SYSTEM;
	}

	/**
	 * Constructor for ElevatorSubsystem.
	 */
	public ElevatorSubsystem() {
		this.elevatorSubsystemBuffer = null;
		server = new Client(Port.SERVER.getNumber());
		elevatorList = new ArrayList<>();
		requestQueue = new LinkedList<>();
		origin = Origin.ELEVATOR_SYSTEM;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while (true) {
			if (server != null) {
				Object object = null;
				if (!requestQueue.isEmpty()) {
					object = server.sendAndReceiveReply(requestQueue.remove());
				} else {
					object = server.sendAndReceiveReply(new FloorRequest(LocalTime.now(), 0, Direction.NONE, 0, origin));
				}

				if (object instanceof ElevatorRequest elevatorRequest) {
					int chosenElevator = chooseElevator(elevatorRequest);
					// Choose elevator
					// Move elevator
					elevatorList.get(chosenElevator - 1).addRequest(elevatorRequest);
					requestQueue.add(new FloorRequest(elevatorRequest, chosenElevator));
				} else if (object instanceof ApproachEvent approachEvent) {
					elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
				}
			} else {
				if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
					SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
					if (request instanceof ElevatorRequest elevatorRequest) {
						int chosenElevator = chooseElevator(elevatorRequest);
						// Choose elevator
						// Move elevator
						elevatorList.get(chosenElevator - 1).addRequest(elevatorRequest);
						requestQueue.add(new FloorRequest(elevatorRequest, chosenElevator));
					} else if (request instanceof ApproachEvent approachEvent) {
						elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
					}
				}
				// send message if possible
				if (!requestQueue.isEmpty()) {
					SystemEvent request = requestQueue.remove();
					sendMessage(request, elevatorSubsystemBuffer, origin);
				}
			}

		}
	}

	/**
	 * Adds an elevator to the subsystem's list of elevators.
	 *
	 * @param elevator an elevator
	 */
	public void addElevator(Elevator elevator) {
		elevatorList.add(elevator);
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		requestQueue.add(approachEvent);
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
			double tempExpectedTime = elevator.getExpectedTime(elevatorRequest);
			if (elevator.getMotor().isIdle()) {
				return elevator.getElevatorNumber();

			} else if (!elevator.getMotor().isActive()) {
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
}
