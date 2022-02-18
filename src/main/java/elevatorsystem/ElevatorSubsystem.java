package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;

import java.util.ArrayList;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, ServiceRequestListener, SystemEventListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private ArrayList<Elevator> elevatorList;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
		elevatorList = new ArrayList<>();
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
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while(true) {
			ServiceRequest request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
			if (request instanceof ElevatorRequest elevatorRequest) {
				sendMessage(new FloorRequest(elevatorRequest, 1), elevatorSubsystemBuffer, Thread.currentThread());
				System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
			} else if (request instanceof ApproachEvent approachEvent) {
				// pass to ElevatorRequest
			}
		}
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		sendMessage(approachEvent, elevatorSubsystemBuffer, Thread.currentThread());
	}
}
