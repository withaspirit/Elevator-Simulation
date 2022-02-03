package misc;

import systemwide.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorRequest is an event data structure for when a user presses an up/down
 * button on a Floor.
 *
 * @author Liam Tripp
 */
public class ElevatorRequest implements ServiceRequest {

	private LocalTime time;
	private int floorNumber;
	private Direction direction;
	private int desiredFloor;

	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
		this.desiredFloor = -1; // Error Value
	}

	/**
	 * Constructor for ElevatorRequest read from an input file.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 * @param desiredFloor the floor the user wishes to visit
	 */
	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction, int desiredFloor) {
		this(time, floorNumber, direction);
		this.desiredFloor = desiredFloor;
	}

	// do not use if created from floorRequest
	public int getDesiredFloor() {
		return desiredFloor;
	}

	@Override
	public LocalTime getTime() {
		return time;
	}

	@Override
	public int getFloorNumber() {
		return floorNumber;
	}

	@Override
	public Direction getDirection() {
		return direction;
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
		String formattedDate = time.format(dateTimeFormat);
		String formattedString = formattedDate + " " + floorNumber + " " + direction.getName();

		if (desiredFloor != -1) {
			formattedString += " " + desiredFloor;
		}
		return formattedString;
	}
}
