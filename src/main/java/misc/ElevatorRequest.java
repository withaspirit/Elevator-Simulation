package misc;

import systemwide.Direction;

import java.time.LocalTime;

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
}
