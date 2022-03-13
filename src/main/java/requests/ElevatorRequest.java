package requests;

import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorRequest is a data structure for when a user presses an up/down
 * button on a Floor.
 *
 * @author Liam Tripp, Ramit Mahajan, Ryan Dash
 */
public class ElevatorRequest extends ServiceRequest {

	private final int desiredFloor;

	/**
	 * Constructor for ElevatorRequest read from an input file.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 * @param desiredFloor the floor the user wishes to visit
	 * @param origin the system from which the message originated
	 */
	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction, int desiredFloor, Origin origin) {
		super(time, floorNumber, direction, origin);
		this.desiredFloor = desiredFloor;
	}

	/**
	 * Returns the desired floor's number.
	 *
	 * @return desiredFloorNumber the number of a passenger's desired floor
	 */
	public int getDesiredFloor() {
		return desiredFloor;
	}

	/**
	 * Convert ElevatorRequest to a String.
	 *
	 * 	if ElevatorRequest was made from an InputFileReader:
	 * 		"hh:mm:ss.mmm floorNumber direction desiredFloorNumber"
	 * 	otherwise:
	 * 		"hh:mm:ss.mmm floorNumber direction"
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = getTime().format(dateTimeFormat);
		String formattedString = formattedDate + " " + getFloorNumber() + " " + getDirection().getName();

		if (desiredFloor != -1) {
			formattedString += " " + desiredFloor;
		}
		return formattedString;
	}
}
