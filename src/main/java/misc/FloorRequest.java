package misc;

import systemwide.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * FloorRequest is an event data structure for when a user presses a FloorButton in an elevator.
 * 
 * @author Liam Tripp
 */
public class FloorRequest implements ServiceRequest {

	private LocalTime time;
	private int floorNumber; // floorToVisit
	private Direction direction;
	private int elevatorNumber;
	private String basicAction;

	public FloorRequest(LocalTime time, int floorNumber, Direction direction) {
		this.time = time;
		this.floorNumber = floorNumber;
		this.direction = direction;
	}

	public FloorRequest(LocalTime time, int floorNumber, Direction direction, int elevatorNumber) {
		this(time, floorNumber, direction);
		this.elevatorNumber = elevatorNumber;
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
	}

	//TODO move to a separate class
	public FloorRequest(String basicAction) {
		this.basicAction = basicAction;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
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
	 * Convert FloorRequest to a String in the format:
	 * "hh:mm:ss.mmm desiredFloor direction elevatorNumber"
	 *
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
		String formattedDate = time.format(dateTimeFormat);
		return formattedDate + " " + floorNumber + " " + direction.getName() + " " + elevatorNumber;
	}
}
