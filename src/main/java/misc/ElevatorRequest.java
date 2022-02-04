package misc;

import systemwide.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ElevatorRequest is an event data structure for when a user presses an up/down
 * button on a Floor.
 *
 * @author Liam Tripp, Ramit Mahajan
 */
public class ElevatorRequest extends ServiceRequest {

	private LocalTime time;
	private int floorNumber;
	private Direction direction;
	private int desiredFloor;
	private String basicAction;

	/**
	 * Constructor for ElevatorRequest read from an input file.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 * @param desiredFloor the floor the user wishes to visit
	 */
	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction, int desiredFloor) {
			super(time ,floorNumber, direction);
			this.desiredFloor = desiredFloor;
		}

	/**
	 * Constructor for ElevatorRequest read from an input file.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 */
	public ElevatorRequest(LocalTime time, int floorNumber, Direction direction) {
		super(time, floorNumber, direction);
		this.desiredFloor = -1; // Error Value
	}

//	//TODO move to a different class
//	public ElevatorRequest(String basicAction) {
//		this.basicAction = basicAction;
//	}

	// do not use if created from floorRequest
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
		String formattedString = formattedDate + " " + floorNumber + " " + direction.getName();

		if (desiredFloor != -1) {
			formattedString += " " + desiredFloor;
		}
		return formattedString;
	}
}
