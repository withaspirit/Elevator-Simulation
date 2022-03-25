package requests;

import elevatorsystem.Fault;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FaultRequest is a data structure for when a fault is detected in the system.
 *
 * @author Ryan Dash
 */
public class FaultRequest extends SystemEvent {

	private final Fault fault;

	/**
	 * Constructor for FaultRequest.
	 *
	 * @param time the time the Request was made
	 * @param origin the system from which the message originated
	 * @param fault a fault for an elevator to handle
	 * @param elevatorNumber an elevator number corresponding to an elevator to receive the fault
	 */
	public FaultRequest(LocalTime time, Origin origin, Fault fault, int elevatorNumber) {
		super(time, origin);
		this.fault = fault;
		setElevatorNumber(elevatorNumber);
	}

	/**
	 * Convert FaultRequest to a String.
	 * "hh:mm:ss.mmm faultName for Elevator# elevatorNumber"
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = getTime().format(dateTimeFormat);

		return formattedDate + " " + fault.getName() + " for Elevator#" + getElevatorNumber();
	}
}
