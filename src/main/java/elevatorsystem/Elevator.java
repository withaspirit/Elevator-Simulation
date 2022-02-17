package elevatorsystem;

import requests.ApproachEvent;
import requests.FloorRequest;
import requests.ServiceRequest;

import java.time.LocalTime;

/**
 * Elevator is a model for simulating an elevator.
 * 
 * @author Liam Tripp
 */
public class Elevator {
	
	private int elevatorNumber;
	private ElevatorSubsystem subsystem;
	// private int currentFloor;
	// private Direction direction;
	
	public Elevator(ElevatorSubsystem elevatorSubsystem) {
		this.subsystem = elevatorSubsystem;
	}

	/**
	 * Passes an ApproachEvent to the ElevatorSubsystem.
	 *
	 * @param request the request for which the ApproachEvent is made
	 */
	public void passApproachEvent(FloorRequest request) {
		subsystem.handleApproachEvent(new ApproachEvent(request));
	}
}
