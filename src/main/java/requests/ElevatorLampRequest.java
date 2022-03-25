package requests;

import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorLampRequest is a request to change the floor lamp state.
 *
 * @author Ryan Dash
 */
public class ElevatorLampRequest extends SystemEvent {

	private final ElevatorLampState lampState;

	/**
	 * Constructor for ElevatorDoorRequest.
	 *
	 * @param time the time the Request was made
	 * @param origin the system from which the message originated
	 * @param lampState the state of an elevator's lamp
	 * @param elevatorNumber an elevator number
	 */
	public ElevatorLampRequest(LocalTime time, Origin origin, ElevatorLampState lampState, int elevatorNumber) {
		super(time, origin);
		this.lampState = lampState;
		setElevatorNumber(elevatorNumber);
	}

	/**
	 * Gets an elevator's lamp state
	 *
	 * @return the state of the door.
	 */
	public ElevatorLampState getLampState() {
		return lampState;
	}

	/**
	 * Convert ElevatorLampRequest to a String "hh:mm:ss.mmm "
	 *
	 * @return a string representation of an elevator light request
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = getTime().format(dateTimeFormat);

		return formattedDate + " Elevator#" + getElevatorNumber() + " lamp:"+ lampState.toString();
	}
}
