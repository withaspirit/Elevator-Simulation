package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

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
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while (true) {
			if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				for (Elevator elevator : elevatorList){
					if (elevatorSubsystemBuffer.elevatorRemoveFromBuffer(elevator.getElevatorNumber())){
						SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
						if (request instanceof ElevatorRequest elevatorRequest) {
							elevatorList.get(elevatorRequest.getElevatorNumber() - 1).addRequest(elevatorRequest);
							requestQueue.add(new FloorRequest(elevatorRequest));
						} else if (request instanceof ApproachEvent approachEvent) {
							elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
						}
						System.err.println("Elevator #" + elevator.getElevatorNumber() + " is correct");
						break;
					}
				}
			}
			// send message if possible
			if (!requestQueue.isEmpty()) {
				SystemEvent request = requestQueue.remove();
				sendMessage(request, elevatorSubsystemBuffer, origin);
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
}
