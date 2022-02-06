package misc;

import systemwide.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FloorRequest is an event data structure for when a user presses a FloorButton in an elevator.
 * 
 * @author Liam Tripp, Ramit Mahajan
 */
public class FloorRequest extends ServiceRequest {

	private int elevatorNumber;
//	private String basicAction;

	public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber, Origin origin) {
		super(time ,floorNumber, direction, origin);
		this.elevatorNumber = elevatorNumber;
	}

	public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber) {
		super(time ,floorNumber, direction);
		this.elevatorNumber = elevatorNumber;
	}

	public FloorRequest(LocalTime time, int floorNumber, Direction direction) {
		super(time ,floorNumber, direction);
	}

	/**
	 * Constructor for FloorRequest given an ElevatorRequest and an Elevator's number.
	 *
	 * @param elevatorRequest a ServiceRequest for an Elevator made by someone on a Floor
	 * @param elevatorNumber the number of the elevator
	 */
	public FloorRequest(ElevatorRequest elevatorRequest, int elevatorNumber) {
		this(elevatorRequest.getTime(), elevatorRequest.getDesiredFloor(),
				elevatorRequest.getDirection(), elevatorNumber);
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
