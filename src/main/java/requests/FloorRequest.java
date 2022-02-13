package requests;

import systemwide.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FloorRequest is an event data structure for when a user presses a FloorButton in an elevator.
 * 
 * @author Liam Tripp, Ramit Mahajan, Ryan Dash
 */
public class FloorRequest extends ServiceRequest {

	private int elevatorNumber;
//	private String basicAction;

	/**
	 * Constructor for FloorRequest read from an input file.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
	 * @param elevatorNumber the number of the elevator taking the request
	 * @param origin the system from which the message originated
	 */
	public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber, Thread origin) {
		super(time ,floorNumber, direction, origin);
		this.elevatorNumber = elevatorNumber;
	}

	/**
	 * Constructor for FloorRequest. Used for button presses within Elevators.
	 *
	 * @param time the time the Request was made
	 * @param floorNumber the number of the floor on which the request was made
	 * @param direction the direction selected by the user
     * @param origin the system from which the message originated
	 */
	public FloorRequest(LocalTime time, int floorNumber, Direction direction, Thread origin) {
		super(time ,floorNumber, direction, origin);
	}

	/**
	 * Constructor for FloorRequest given an ElevatorRequest and an Elevator's number.
	 *
	 * @param elevatorRequest a ServiceRequest for an Elevator made by someone on a Floor
	 * @param elevatorNumber the number of the elevator
	 * @param origin the system from which the message originated
	 */
	public FloorRequest(ElevatorRequest elevatorRequest, int elevatorNumber, Thread origin) {
		this(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
				elevatorRequest.getDirection(), elevatorNumber, origin);
		if (getDirection().equals(Direction.UP)){
			setDirection(Direction.DOWN);
		} else {
			setDirection(Direction.UP);
		}
	}

//	//TODO move to a separate class
//	public FloorRequest(String basicAction) {
//		this.basicAction = basicAction;
//	}

	/**
	 * Returns the number of the elevator corresponding to the floorRequest.
	 *
	 * @return elevatorNumber the number of the elevator corresponding to the request
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Convert FloorRequest to a String in the format:
	 * "hh:mm:ss.mmm desiredFloor direction elevatorNumber"
	 *
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = getTime().format(dateTimeFormat);
		return formattedDate + " " + getFloorNumber() + " " + getDirection().getName() + " " + elevatorNumber;
	}
}
