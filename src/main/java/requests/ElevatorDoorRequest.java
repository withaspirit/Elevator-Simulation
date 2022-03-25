package requests;

import elevatorsystem.Doors;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorDoorRequest is a request to change the state of an elevator's doors.
 *
 * @author Ryan Dash
 */
public class ElevatorDoorRequest extends SystemEvent {

	private final Doors.State state;

	/**
	 * Constructor for ElevatorDoorRequest
	 *
	 * @param time the time the Request was made
	 * @param origin the system from which the message originated
	 */
	public ElevatorDoorRequest(LocalTime time, Origin origin, Doors.State state, int elevatorNumber) {
		super(time, origin);
		this.state = state;
		setElevatorNumber(elevatorNumber);
	}

	/**
	 * Gets the state of the door.
	 *
	 * @return the state of the door
	 */
	public Doors.State getDoorState() {
		return state;
	}

	/**
	 * Convert ElevatorDoorRequest to a String "hh:mm:ss.mmm Doors.State".
	 *
	 * @return a string representation of an elevator door request
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = getTime().format(dateTimeFormat);
		return formattedDate + " Elevator#" + getElevatorNumber() + " Door:"+ state.toString();
	}
}
